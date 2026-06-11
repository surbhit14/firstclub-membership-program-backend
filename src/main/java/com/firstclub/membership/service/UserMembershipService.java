package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.request.TierChangeRequest;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMembershipService {

    private final UserMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final TierEvaluationService tierEvaluationService;

    @Transactional
    public UserMembership subscribe(SubscribeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        // Prevent duplicate active memberships
        membershipRepository.findByUserAndStatus(user, MembershipStatus.ACTIVE).ifPresent(existing -> {
            throw new MembershipException(
                    "User already has an active membership (id=" + existing.getId() + "). Cancel it first.");
        });

        MembershipPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", request.getPlanId()));

        if (!plan.isActive()) {
            throw new MembershipException("Plan is no longer available: " + plan.getName());
        }

        // All users start at Silver — tier is earned through shopping, not chosen at subscribe time
        MembershipTier tier = tierRepository.findByTierLevel(TierLevel.SILVER)
                .orElseThrow(() -> new ResourceNotFoundException("Silver tier not found — check seed data"));

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(plan.getDurationMonths());

        UserMembership membership = UserMembership.builder()
                .user(user)
                .plan(plan)
                .tier(tier)
                .status(MembershipStatus.ACTIVE)
                .startDate(start)
                .endDate(end)
                .build();

        UserMembership saved = membershipRepository.save(membership);
        log.info("User {} subscribed to plan={} tier={} until {}", user.getId(), plan.getName(), tier.getName(), end);
        return saved;
    }

    /**
     * Upgrade moves the user to a higher tier.
     * Protected by optimistic locking — concurrent calls resolve gracefully with a 409.
     */
    @Transactional
    public UserMembership upgradeTier(TierChangeRequest request) {
        UserMembership membership = getActiveMembership(request.getUserId());
        MembershipTier targetTier = tierRepository.findById(request.getTierId())
                .orElseThrow(() -> new ResourceNotFoundException("MembershipTier", request.getTierId()));

        if (!targetTier.getTierLevel().isHigherThan(membership.getTier().getTierLevel())) {
            throw new MembershipException(
                    "Target tier '" + targetTier.getName() + "' is not higher than current tier '"
                            + membership.getTier().getName() + "'. Use downgrade for lower tiers.");
        }

        if (!tierEvaluationService.qualifiesForTier(membership.getUser(), targetTier)) {
            throw new MembershipException(
                    "User does not meet the criteria for tier: " + targetTier.getName());
        }

        MembershipTier previousTier = membership.getTier();
        membership.setTier(targetTier);
        UserMembership saved = membershipRepository.save(membership);
        log.info("User {} upgraded tier: {} -> {}", request.getUserId(), previousTier.getName(), targetTier.getName());
        return saved;
    }

    /**
     * Downgrade moves the user to a lower tier — no criteria check required.
     */
    @Transactional
    public UserMembership downgradeTier(TierChangeRequest request) {
        UserMembership membership = getActiveMembership(request.getUserId());
        MembershipTier targetTier = tierRepository.findById(request.getTierId())
                .orElseThrow(() -> new ResourceNotFoundException("MembershipTier", request.getTierId()));

        if (!targetTier.getTierLevel().isLowerThan(membership.getTier().getTierLevel())) {
            throw new MembershipException(
                    "Target tier '" + targetTier.getName() + "' is not lower than current tier '"
                            + membership.getTier().getName() + "'. Use upgrade for higher tiers.");
        }

        MembershipTier previousTier = membership.getTier();
        membership.setTier(targetTier);
        UserMembership saved = membershipRepository.save(membership);
        log.info("User {} downgraded tier: {} -> {}", request.getUserId(), previousTier.getName(), targetTier.getName());
        return saved;
    }

    @Transactional
    public UserMembership cancel(Long userId) {
        UserMembership membership = getActiveMembership(userId);
        membership.setStatus(MembershipStatus.CANCELLED);
        UserMembership saved = membershipRepository.save(membership);
        log.info("User {} cancelled membership id={}", userId, membership.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public UserMembership getActiveMembership(Long userId) {
        return membershipRepository.findByUser_IdAndStatus(userId, MembershipStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active membership found for user: " + userId));
    }

    @Transactional(readOnly = true)
    public List<UserMembership> getMembershipHistory(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        return membershipRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    /**
     * Re-evaluates whether the user qualifies for a higher tier based on current activity.
     * Call this after order creation or on-demand.
     */
    @Transactional
    public UserMembership evaluateAndAutoUpgrade(Long userId) {
        UserMembership membership = getActiveMembership(userId);
        User user = membership.getUser();
        MembershipTier currentTier = membership.getTier();

        // Find the best tier this user now qualifies for (highest rank wins)
        List<MembershipTier> allTiers = tierRepository.findAllActiveWithDetails();
        MembershipTier bestQualifyingTier = allTiers.stream()
                .filter(t -> t.getTierLevel().isHigherThan(currentTier.getTierLevel()))
                .filter(t -> tierEvaluationService.qualifiesForTier(user, t))
                .reduce((a, b) -> a.getTierLevel().isHigherThan(b.getTierLevel()) ? a : b)
                .orElse(null);

        if (bestQualifyingTier != null) {
            log.info("Auto-upgrading user {} from {} to {}", userId, currentTier.getName(), bestQualifyingTier.getName());
            membership.setTier(bestQualifyingTier);
            return membershipRepository.save(membership);
        }

        log.debug("User {} stays at tier {}: no higher tier criteria met", userId, currentTier.getName());
        return membership;
    }

    // Runs daily at midnight to mark expired memberships
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireOldMemberships() {
        List<UserMembership> expired = membershipRepository.findExpiredActiveMemberships(LocalDate.now());
        expired.forEach(m -> m.setStatus(MembershipStatus.EXPIRED));
        membershipRepository.saveAll(expired);
        if (!expired.isEmpty()) {
            log.info("Expired {} memberships", expired.size());
        }
    }
}

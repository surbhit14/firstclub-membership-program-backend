package com.firstclub.membership.service;

import com.firstclub.membership.dto.response.ApplicableBenefitResponse;
import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.service.strategy.BenefitEvaluationStrategy;
import com.firstclub.membership.service.strategy.DefaultBenefitStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Evaluates which benefits are applicable for a given user + order amount combination.
 *
 * Uses the Strategy Pattern (mirroring TierEvaluationService): each BenefitType has a
 * @Component implementing BenefitEvaluationStrategy. Unregistered types fall back to
 * DefaultBenefitStrategy so new BenefitType values work without any registration.
 *
 * Three entry points:
 *   getBenefitsForOrder()          → all benefits with conditionMet flag (for UI display)
 *   getApplicableBenefitsForOrder()→ only conditionMet=true (for checkout pipeline)
 *   isBenefitApplicable()          → single benefit point-check (for downstream services)
 */
@Slf4j
@Service
public class BenefitApplicationService {

    private final UserMembershipRepository membershipRepository;
    private final Map<BenefitType, BenefitEvaluationStrategy> strategyMap;

    // Spring collects all BenefitEvaluationStrategy beans into a list.
    // Indexed into a map by BenefitType for O(1) dispatch — same pattern as TierEvaluationService.
    public BenefitApplicationService(
            UserMembershipRepository membershipRepository,
            List<BenefitEvaluationStrategy> strategies) {
        this.membershipRepository = membershipRepository;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(BenefitEvaluationStrategy::getSupportedBenefitType, Function.identity()));
        log.info("Loaded {} benefit evaluation strategies: {}", strategyMap.size(), strategyMap.keySet());
    }

    /**
     * Returns ALL benefits for the user's active tier annotated with conditionMet.
     * Evaluation logic lives here (not in the DTO) — the DTO only carries the result.
     */
    @Transactional(readOnly = true)
    public List<ApplicableBenefitResponse> getBenefitsForOrder(Long userId, BigDecimal orderAmount) {
        UserMembership membership = getActiveMembership(userId);

        return membership.getTier().getBenefits().stream()
                .map(benefit -> {
                    boolean conditionMet = resolveStrategy(benefit.getBenefitType())
                            .isApplicable(benefit, orderAmount);
                    return ApplicableBenefitResponse.from(benefit, conditionMet);
                })
                .collect(Collectors.toList());
    }

    /**
     * Returns ONLY applicable benefits — what the checkout/delivery pipeline should use.
     */
    @Transactional(readOnly = true)
    public List<ApplicableBenefitResponse> getApplicableBenefitsForOrder(Long userId, BigDecimal orderAmount) {
        return getBenefitsForOrder(userId, orderAmount).stream()
                .filter(ApplicableBenefitResponse::isConditionMet)
                .collect(Collectors.toList());
    }

    /**
     * Point-check for a specific benefit type. Used by downstream services
     * (e.g. delivery service checking FREE_DELIVERY before waiving delivery fee).
     */
    @Transactional(readOnly = true)
    public boolean isBenefitApplicable(Long userId, BenefitType benefitType, BigDecimal orderAmount) {
        Optional<UserMembership> membershipOpt =
                membershipRepository.findByUser_IdAndStatus(userId, MembershipStatus.ACTIVE);

        return membershipOpt.map(m -> m.getTier().getBenefits().stream()
                .filter(b -> b.getBenefitType() == benefitType)
                .findFirst()
                .map(b -> resolveStrategy(benefitType).isApplicable(b, orderAmount))
                .orElse(false)
        ).orElse(false);
    }

    // Falls back to DefaultBenefitStrategy for benefit types without a registered @Component
    private BenefitEvaluationStrategy resolveStrategy(BenefitType benefitType) {
        return strategyMap.getOrDefault(benefitType, new DefaultBenefitStrategy(benefitType));
    }

    private UserMembership getActiveMembership(Long userId) {
        return membershipRepository.findByUser_IdAndStatus(userId, MembershipStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active membership found for user: " + userId));
    }
}

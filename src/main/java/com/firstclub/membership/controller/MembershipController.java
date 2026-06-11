package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.BenefitCheckRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.request.TierChangeRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.ApplicableBenefitResponse;
import com.firstclub.membership.dto.response.MembershipPlanResponse;
import com.firstclub.membership.dto.response.MembershipTierResponse;
import com.firstclub.membership.dto.response.UserMembershipResponse;
import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.service.BenefitApplicationService;
import com.firstclub.membership.service.MembershipPlanService;
import com.firstclub.membership.service.MembershipTierService;
import com.firstclub.membership.service.UserMembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipPlanService planService;
    private final MembershipTierService tierService;
    private final UserMembershipService membershipService;
    private final BenefitApplicationService benefitApplicationService;

    // ── Plans ──────────────────────────────────────────────────────────────────

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<MembershipPlanResponse>>> getPlans() {
        List<MembershipPlanResponse> plans = planService.getActivePlans()
                .stream().map(MembershipPlanResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(plans));
    }

    // ── Tiers ──────────────────────────────────────────────────────────────────

    @GetMapping("/tiers")
    public ResponseEntity<ApiResponse<List<MembershipTierResponse>>> getTiers() {
        List<MembershipTierResponse> tiers = tierService.getActiveTiersWithDetails()
                .stream().map(MembershipTierResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(tiers));
    }

    // ── Subscribe ──────────────────────────────────────────────────────────────

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> subscribe(
            @Valid @RequestBody SubscribeRequest request) {
        UserMembership membership = membershipService.subscribe(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Subscribed successfully", UserMembershipResponse.from(membership)));
    }

    // ── Tier changes ──────────────────────────────────────────────────────────

    @PutMapping("/upgrade")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> upgrade(
            @Valid @RequestBody TierChangeRequest request) {
        UserMembership membership = membershipService.upgradeTier(request);
        return ResponseEntity.ok(ApiResponse.ok("Tier upgraded successfully", UserMembershipResponse.from(membership)));
    }

    @PutMapping("/downgrade")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> downgrade(
            @Valid @RequestBody TierChangeRequest request) {
        UserMembership membership = membershipService.downgradeTier(request);
        return ResponseEntity.ok(ApiResponse.ok("Tier downgraded successfully", UserMembershipResponse.from(membership)));
    }

    // ── Cancel ────────────────────────────────────────────────────────────────

    @PutMapping("/cancel/{userId}")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> cancel(@PathVariable Long userId) {
        UserMembership membership = membershipService.cancel(userId);
        return ResponseEntity.ok(ApiResponse.ok("Membership cancelled", UserMembershipResponse.from(membership)));
    }

    // ── Status & history ──────────────────────────────────────────────────────

    @GetMapping("/status/{userId}")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> getStatus(@PathVariable Long userId) {
        UserMembership membership = membershipService.getActiveMembership(userId);
        return ResponseEntity.ok(ApiResponse.ok(UserMembershipResponse.from(membership)));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<List<UserMembershipResponse>>> getHistory(@PathVariable Long userId) {
        List<UserMembershipResponse> history = membershipService.getMembershipHistory(userId)
                .stream().map(UserMembershipResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(history));
    }

    // ── Auto-evaluate tier ────────────────────────────────────────────────────

    @PostMapping("/evaluate-tier/{userId}")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> evaluateTier(@PathVariable Long userId) {
        UserMembership membership = membershipService.evaluateAndAutoUpgrade(userId);
        return ResponseEntity.ok(ApiResponse.ok(
                "Tier evaluation complete. Current tier: " + membership.getTier().getName(),
                UserMembershipResponse.from(membership)));
    }

    // ── Benefit condition checking ────────────────────────────────────────────

    /**
     * Returns ALL tier benefits annotated with conditionMet=true/false for the given order amount.
     * Use this to show the user what they unlock (and what they don't) for a specific order.
     */
    @PostMapping("/benefits/check")
    public ResponseEntity<ApiResponse<List<ApplicableBenefitResponse>>> checkBenefits(
            @Valid @RequestBody BenefitCheckRequest request) {
        List<ApplicableBenefitResponse> benefits =
                benefitApplicationService.getBenefitsForOrder(request.getUserId(), request.getOrderAmount());
        return ResponseEntity.ok(ApiResponse.ok(benefits));
    }

    /**
     * Returns ONLY the benefits that actually apply for this order amount.
     * This is what the checkout/delivery pipeline should call before applying perks.
     */
    @PostMapping("/benefits/applicable")
    public ResponseEntity<ApiResponse<List<ApplicableBenefitResponse>>> applicableBenefits(
            @Valid @RequestBody BenefitCheckRequest request) {
        List<ApplicableBenefitResponse> benefits =
                benefitApplicationService.getApplicableBenefitsForOrder(request.getUserId(), request.getOrderAmount());
        return ResponseEntity.ok(ApiResponse.ok(benefits));
    }
}

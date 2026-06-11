package com.firstclub.membership.config;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.*;
import com.firstclub.membership.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Seeds the database with plans, tiers, benefits, and criteria on startup.
 * All values are configurable — change them here or load from application.yml/DB.
 *
 * Benefit design principles:
 *   - minOrderValue on TierBenefit is enforced at checkout time by BenefitApplicationService.
 *   - Higher tiers explicitly list ALL their benefits (no implicit inheritance), which allows
 *     each tier to carry an enhanced version of a lower-tier benefit (e.g. Silver FREE_DELIVERY
 *     requires ₹499 minimum; Gold/Platinum FREE_DELIVERY has no minimum).
 *   - EXCLUSIVE_DEALS is present on all tiers — higher tiers get a richer version of it.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedPlans();
        seedTiers();
        log.info("Data initialization complete.");
    }

    private void seedPlans() {
        if (planRepository.count() > 0) return;

        planRepository.save(MembershipPlan.builder()
                .name("Monthly Plan")
                .planType(PlanType.MONTHLY)
                .durationMonths(1)
                .price(new BigDecimal("199.00"))
                .description("Flexible month-to-month membership")
                .build());

        planRepository.save(MembershipPlan.builder()
                .name("Quarterly Plan")
                .planType(PlanType.QUARTERLY)
                .durationMonths(3)
                .price(new BigDecimal("499.00"))
                .description("3-month plan — save 16% vs monthly")
                .build());

        planRepository.save(MembershipPlan.builder()
                .name("Yearly Plan")
                .planType(PlanType.YEARLY)
                .durationMonths(12)
                .price(new BigDecimal("1499.00"))
                .description("Annual plan — save 37% vs monthly")
                .build());

        log.info("Seeded 3 membership plans");
    }

    private void seedTiers() {
        if (tierRepository.count() > 0) return;

        // ── Silver ────────────────────────────────────────────────────────────
        MembershipTier silver = MembershipTier.builder()
                .name("Silver")
                .tierLevel(TierLevel.SILVER)
                .description("Entry-level tier with essential benefits")
                .criteriaLogic(CriteriaLogic.ANY)
                .build();

        // minOrderValue=499 is enforced by BenefitApplicationService — not just text
        silver.getBenefits().add(TierBenefit.builder().tier(silver)
                .benefitType(BenefitType.FREE_DELIVERY)
                .minOrderValue(new BigDecimal("499.00"))
                .description("Free delivery on orders above ₹499")
                .build());

        silver.getBenefits().add(TierBenefit.builder().tier(silver)
                .benefitType(BenefitType.EXTRA_DISCOUNT)
                .value(5)
                .description("5% extra discount on selected categories")
                .build());

        silver.getBenefits().add(TierBenefit.builder().tier(silver)
                .benefitType(BenefitType.EXCLUSIVE_DEALS)
                .description("Access to member-only deals")
                .build());

        // Silver has no criteria — open to all new subscribers
        tierRepository.save(silver);

        // ── Gold ──────────────────────────────────────────────────────────────
        MembershipTier gold = MembershipTier.builder()
                .name("Gold")
                .tierLevel(TierLevel.GOLD)
                .description("Mid-tier with enhanced benefits")
                .criteriaLogic(CriteriaLogic.ANY)
                .build();

        // No minOrderValue — Gold FREE_DELIVERY applies to ALL orders (enhancement over Silver)
        gold.getBenefits().add(TierBenefit.builder().tier(gold)
                .benefitType(BenefitType.FREE_DELIVERY)
                .description("Free delivery on all orders (no minimum)")
                .build());

        gold.getBenefits().add(TierBenefit.builder().tier(gold)
                .benefitType(BenefitType.EXTRA_DISCOUNT)
                .value(10)
                .description("10% extra discount on selected categories")
                .build());

        // EXCLUSIVE_DEALS carried forward from Silver — enhanced description for Gold
        gold.getBenefits().add(TierBenefit.builder().tier(gold)
                .benefitType(BenefitType.EXCLUSIVE_DEALS)
                .description("Priority access to member-only deals + flash sales")
                .build());

        gold.getBenefits().add(TierBenefit.builder().tier(gold)
                .benefitType(BenefitType.EARLY_SALE_ACCESS)
                .description("24-hour early access to all sales")
                .build());

        gold.getBenefits().add(TierBenefit.builder().tier(gold)
                .benefitType(BenefitType.EXCLUSIVE_COUPONS)
                .description("Monthly exclusive coupon bundle")
                .build());

        gold.getCriteria().add(TierCriteria.builder().tier(gold)
                .criteriaType(CriteriaType.ORDER_COUNT)
                .threshold(5.0)
                .description("Place at least 5 orders total")
                .build());

        gold.getCriteria().add(TierCriteria.builder().tier(gold)
                .criteriaType(CriteriaType.ORDER_VALUE)
                .threshold(2000.0)
                .evaluationWindowDays(30)
                .description("Spend ₹2000+ in the last 30 days")
                .build());

        gold.getCriteria().add(TierCriteria.builder().tier(gold)
                .criteriaType(CriteriaType.COHORT)
                .cohortName("PREMIUM_SHOPPER")
                .description("User belongs to PREMIUM_SHOPPER cohort")
                .build());

        tierRepository.save(gold);

        // ── Platinum ──────────────────────────────────────────────────────────
        MembershipTier platinum = MembershipTier.builder()
                .name("Platinum")
                .tierLevel(TierLevel.PLATINUM)
                .description("Premium tier with all exclusive benefits")
                .criteriaLogic(CriteriaLogic.ANY)
                .build();

        // No minOrderValue — Platinum FREE_DELIVERY on all orders, same-day guarantee
        platinum.getBenefits().add(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.FREE_DELIVERY)
                .description("Free same-day delivery on all orders (no minimum)")
                .build());

        platinum.getBenefits().add(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.EXTRA_DISCOUNT)
                .value(20)
                .description("20% extra discount on all categories")
                .build());

        // EXCLUSIVE_DEALS carried forward — highest tier gets most exclusive version
        platinum.getBenefits().add(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.EXCLUSIVE_DEALS)
                .description("VIP access to member-only deals, flash sales, and curated drops")
                .build());

        platinum.getBenefits().add(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.EARLY_SALE_ACCESS)
                .description("48-hour early access to all sales")
                .build());

        platinum.getBenefits().add(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.PRIORITY_SUPPORT)
                .description("Dedicated priority customer support")
                .build());

        platinum.getBenefits().add(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.FASTER_DELIVERY)
                .description("Same-day delivery guarantee")
                .build());

        platinum.getBenefits().add(TierBenefit.builder().tier(platinum)
                .benefitType(BenefitType.EXCLUSIVE_COUPONS)
                .description("Weekly exclusive coupon bundle")
                .build());

        platinum.getCriteria().add(TierCriteria.builder().tier(platinum)
                .criteriaType(CriteriaType.ORDER_COUNT)
                .threshold(20.0)
                .description("Place at least 20 orders total")
                .build());

        platinum.getCriteria().add(TierCriteria.builder().tier(platinum)
                .criteriaType(CriteriaType.ORDER_VALUE)
                .threshold(10000.0)
                .evaluationWindowDays(30)
                .description("Spend ₹10000+ in the last 30 days")
                .build());

        platinum.getCriteria().add(TierCriteria.builder().tier(platinum)
                .criteriaType(CriteriaType.COHORT)
                .cohortName("EARLY_ADOPTER")
                .description("User belongs to EARLY_ADOPTER cohort")
                .build());

        tierRepository.save(platinum);

        log.info("Seeded 3 membership tiers (Silver, Gold, Platinum) with benefits and criteria");
    }
}

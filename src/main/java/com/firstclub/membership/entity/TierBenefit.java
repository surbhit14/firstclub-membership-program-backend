package com.firstclub.membership.entity;

import com.firstclub.membership.enums.BenefitType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * A single perk attached to a MembershipTier.
 *
 * minOrderValue is the key enforcement field:
 *   - null  → benefit applies to every order (Gold/Platinum FREE_DELIVERY)
 *   - set   → benefit only applies when orderAmount >= minOrderValue (Silver FREE_DELIVERY requires ₹499)
 *
 * This condition is evaluated at runtime by BenefitApplicationService via BenefitEvaluationStrategy.
 * It is stored as data so thresholds can be changed without a code deployment.
 *
 * value is numeric (e.g. 10.0 = 10% for EXTRA_DISCOUNT); 0.0 means not applicable for that type.
 */
@Entity
@Table(name = "tier_benefits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BenefitType benefitType;

    // Numeric value for parameterized benefits (e.g. 10.0 for 10% discount, 0.0 means N/A)
    @Column(nullable = false)
    @Builder.Default
    private double value = 0.0;

    // Minimum order amount for this benefit to apply (null = no minimum, always applies)
    @Column(precision = 10, scale = 2)
    private BigDecimal minOrderValue;

    // Human-readable description of this specific benefit value
    @Column(nullable = false)
    private String description;
}

package com.firstclub.membership.entity;

import com.firstclub.membership.enums.CriteriaType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tier_criteria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CriteriaType criteriaType;

    // For ORDER_COUNT: minimum number of orders; for ORDER_VALUE: minimum total spend
    @Column
    private Double threshold;

    // For COHORT criteria: the cohort name this tier is restricted to
    @Column
    private String cohortName;

    // Evaluation window in days for ORDER_VALUE criteria (default 30 days)
    @Column
    @Builder.Default
    private int evaluationWindowDays = 30;

    @Column
    private String description;
}

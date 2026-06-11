package com.firstclub.membership.entity;

import com.firstclub.membership.enums.CriteriaLogic;
import com.firstclub.membership.enums.TierLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membership_tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TierLevel tierLevel;

    @Column
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    // Determines how multiple criteria are combined: ANY (OR) or ALL (AND)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CriteriaLogic criteriaLogic = CriteriaLogic.ANY;

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<TierBenefit> benefits = new ArrayList<>();

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<TierCriteria> criteria = new ArrayList<>();
}

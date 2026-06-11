package com.firstclub.membership.entity;

import com.firstclub.membership.enums.MembershipStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The join between a User, their MembershipPlan (pricing), and their MembershipTier (privilege level).
 *
 * Only one ACTIVE record is allowed per user at a time — enforced in UserMembershipService.subscribe().
 *
 * @Version enables optimistic locking: concurrent upgrade/downgrade requests will race on the
 * version column — the loser gets ObjectOptimisticLockingFailureException → HTTP 409.
 *
 * status transitions:
 *   ACTIVE → CANCELLED  (user cancels)
 *   ACTIVE → EXPIRED    (scheduled job marks it when endDate passes)
 */
@Entity
@Table(name = "user_memberships",
        indexes = {
                @Index(name = "idx_user_membership_user", columnList = "user_id"),
                @Index(name = "idx_user_membership_status", columnList = "status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private MembershipPlan plan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MembershipStatus status = MembershipStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Optimistic locking to handle concurrent membership updates
    @Version
    private Long version;
}

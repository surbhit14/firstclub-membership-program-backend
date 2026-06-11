package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders",
        indexes = {
                @Index(name = "idx_order_user", columnList = "user_id"),
                @Index(name = "idx_order_created", columnList = "createdAt")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column
    private String description;

    // Snapshot of the tier active at order time — enables auditing which benefits were applicable.
    // Nullable: non-members can still place orders.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applied_tier_id")
    private MembershipTier appliedTier;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}

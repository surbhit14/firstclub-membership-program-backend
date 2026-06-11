package com.firstclub.membership.entity;

import com.firstclub.membership.enums.Cohort;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    // Typed enum — prevents silent typo mismatches in cohort-based tier criteria
    @Enumerated(EnumType.STRING)
    @Column
    private Cohort cohort;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}

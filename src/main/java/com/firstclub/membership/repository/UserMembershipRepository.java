package com.firstclub.membership.repository;

import com.firstclub.membership.entity.User;
import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {

    Optional<UserMembership> findByUserAndStatus(User user, MembershipStatus status);

    Optional<UserMembership> findByUser_IdAndStatus(Long userId, MembershipStatus status);

    List<UserMembership> findByUser_IdOrderByCreatedAtDesc(Long userId);

    // Find all memberships that have expired but are still marked ACTIVE (for scheduled cleanup)
    @Query("SELECT m FROM UserMembership m WHERE m.status = 'ACTIVE' AND m.endDate < :today")
    List<UserMembership> findExpiredActiveMemberships(@Param("today") LocalDate today);
}

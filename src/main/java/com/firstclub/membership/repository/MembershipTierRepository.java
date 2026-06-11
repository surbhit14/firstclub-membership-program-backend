package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.enums.TierLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
    List<MembershipTier> findByActiveTrueOrderByTierLevel();
    Optional<MembershipTier> findByTierLevel(TierLevel tierLevel);

    // Fetch benefits eagerly in one query; criteria are loaded lazily (only used during tier evaluation)
    @Query("SELECT DISTINCT t FROM MembershipTier t LEFT JOIN FETCH t.benefits WHERE t.active = true")
    List<MembershipTier> findAllActiveWithDetails();
}

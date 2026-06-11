package com.firstclub.membership.service;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipTierService {

    private final MembershipTierRepository tierRepository;

    @Transactional(readOnly = true)
    public List<MembershipTier> getActiveTiersWithDetails() {
        return tierRepository.findAllActiveWithDetails().stream()
                .sorted(Comparator.comparingInt(t -> t.getTierLevel().getRank()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MembershipTier getTier(Long tierId) {
        return tierRepository.findById(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipTier", tierId));
    }
}

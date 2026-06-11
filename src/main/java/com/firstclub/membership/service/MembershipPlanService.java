package com.firstclub.membership.service;

import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipPlanService {

    private final MembershipPlanRepository planRepository;

    @Transactional(readOnly = true)
    public List<MembershipPlan> getActivePlans() {
        return planRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public MembershipPlan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", planId));
    }
}

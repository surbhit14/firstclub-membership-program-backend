package com.firstclub.membership.service.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCountEvaluationStrategy implements TierEvaluationStrategy {

    private final OrderRepository orderRepository;

    @Override
    public CriteriaType getSupportedCriteriaType() {
        return CriteriaType.ORDER_COUNT;
    }

    @Override
    public boolean evaluate(User user, TierCriteria criteria) {
        long orderCount = orderRepository.countByUser_Id(user.getId());
        boolean qualifies = orderCount >= criteria.getThreshold();
        log.debug("User {} order count: {} / threshold: {} -> qualifies: {}",
                user.getId(), orderCount, criteria.getThreshold(), qualifies);
        return qualifies;
    }
}

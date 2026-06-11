package com.firstclub.membership.service.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderValueEvaluationStrategy implements TierEvaluationStrategy {

    private final OrderRepository orderRepository;

    @Override
    public CriteriaType getSupportedCriteriaType() {
        return CriteriaType.ORDER_VALUE;
    }

    @Override
    public boolean evaluate(User user, TierCriteria criteria) {
        int windowDays = criteria.getEvaluationWindowDays() > 0 ? criteria.getEvaluationWindowDays() : 30;
        LocalDateTime since = LocalDateTime.now().minusDays(windowDays);

        BigDecimal totalSpend = orderRepository.sumTotalAmountByUser_IdAndCreatedAtAfter(user.getId(), since);
        boolean qualifies = totalSpend.compareTo(BigDecimal.valueOf(criteria.getThreshold())) >= 0;
        log.debug("User {} spend in last {}d: {} / threshold: {} -> qualifies: {}",
                user.getId(), windowDays, totalSpend, criteria.getThreshold(), qualifies);
        return qualifies;
    }
}

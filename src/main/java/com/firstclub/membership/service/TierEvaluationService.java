package com.firstclub.membership.service;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CriteriaLogic;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.service.strategy.TierEvaluationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TierEvaluationService {

    private final Map<CriteriaType, TierEvaluationStrategy> strategyMap;

    // Spring injects all TierEvaluationStrategy beans; we index them by CriteriaType for O(1) dispatch
    public TierEvaluationService(List<TierEvaluationStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(TierEvaluationStrategy::getSupportedCriteriaType, Function.identity()));
        log.info("Loaded {} tier evaluation strategies: {}", strategyMap.size(), strategyMap.keySet());
    }

    /**
     * Returns true if the user qualifies for the given tier, respecting the tier's CriteriaLogic.
     * A tier with no criteria is open to all subscribers (no qualification required).
     */
    public boolean qualifiesForTier(User user, MembershipTier tier) {
        List<TierCriteria> criteriaList = tier.getCriteria();

        if (criteriaList == null || criteriaList.isEmpty()) {
            return true;
        }

        if (tier.getCriteriaLogic() == CriteriaLogic.ALL) {
            return criteriaList.stream().allMatch(c -> evaluate(user, c));
        } else {
            return criteriaList.stream().anyMatch(c -> evaluate(user, c));
        }
    }

    private boolean evaluate(User user, TierCriteria criteria) {
        TierEvaluationStrategy strategy = strategyMap.get(criteria.getCriteriaType());
        if (strategy == null) {
            log.warn("No strategy found for CriteriaType: {}. Treating as not qualified.", criteria.getCriteriaType());
            return false;
        }
        return strategy.evaluate(user, criteria);
    }
}

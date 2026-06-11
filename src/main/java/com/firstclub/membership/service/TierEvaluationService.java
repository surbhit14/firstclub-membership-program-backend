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

/**
 * Evaluates whether a user qualifies for a given MembershipTier.
 *
 * Uses the Strategy Pattern: each CriteriaType (ORDER_COUNT, ORDER_VALUE, COHORT) has its own
 * @Component implementing TierEvaluationStrategy. Spring injects all of them as a List, and this
 * service indexes them into a Map<CriteriaType, Strategy> for O(1) dispatch.
 *
 * Adding a new CriteriaType requires only a new @Component — zero changes here.
 */
@Slf4j
@Service
public class TierEvaluationService {

    private final Map<CriteriaType, TierEvaluationStrategy> strategyMap;

    // Spring collects all TierEvaluationStrategy beans into a list automatically.
    // We convert it to a map so evaluation is a direct lookup, not a loop.
    public TierEvaluationService(List<TierEvaluationStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(TierEvaluationStrategy::getSupportedCriteriaType, Function.identity()));
        log.info("Loaded {} tier evaluation strategies: {}", strategyMap.size(), strategyMap.keySet());
    }

    /**
     * Returns true if the user qualifies for the given tier.
     * A tier with no criteria rows is the entry tier — open to all (Silver).
     * CriteriaLogic.ANY = pass if at least one criteria passes (anyMatch).
     * CriteriaLogic.ALL = pass only if every criteria passes (allMatch).
     */
    public boolean qualifiesForTier(User user, MembershipTier tier) {
        List<TierCriteria> criteriaList = tier.getCriteria();

        // No criteria = open entry tier (Silver)
        if (criteriaList == null || criteriaList.isEmpty()) {
            return true;
        }

        if (tier.getCriteriaLogic() == CriteriaLogic.ALL) {
            return criteriaList.stream().allMatch(c -> evaluate(user, c));
        } else {
            // ANY is the default — qualifies if even one criterion passes
            return criteriaList.stream().anyMatch(c -> evaluate(user, c));
        }
    }

    private boolean evaluate(User user, TierCriteria criteria) {
        TierEvaluationStrategy strategy = strategyMap.get(criteria.getCriteriaType());
        if (strategy == null) {
            // Unknown criteria type — fail safe: don't grant access
            log.warn("No strategy found for CriteriaType: {}. Treating as not qualified.", criteria.getCriteriaType());
            return false;
        }
        return strategy.evaluate(user, criteria);
    }
}

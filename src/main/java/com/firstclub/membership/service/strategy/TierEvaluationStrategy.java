package com.firstclub.membership.service.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CriteriaType;

/**
 * Strategy interface for evaluating whether a user meets a specific tier criterion.
 * Implementations handle one CriteriaType each, keeping tier-upgrade logic extensible.
 */
public interface TierEvaluationStrategy {
    CriteriaType getSupportedCriteriaType();
    boolean evaluate(User user, TierCriteria criteria);
}

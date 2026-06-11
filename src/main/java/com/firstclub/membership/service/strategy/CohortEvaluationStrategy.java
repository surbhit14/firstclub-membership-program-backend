package com.firstclub.membership.service.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.Cohort;
import com.firstclub.membership.enums.CriteriaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CohortEvaluationStrategy implements TierEvaluationStrategy {

    @Override
    public CriteriaType getSupportedCriteriaType() {
        return CriteriaType.COHORT;
    }

    @Override
    public boolean evaluate(User user, TierCriteria criteria) {
        String requiredCohortName = criteria.getCohortName();
        Cohort userCohort = user.getCohort();

        // Both must be non-null; compare enum name to stored string
        boolean qualifies = requiredCohortName != null
                && userCohort != null
                && requiredCohortName.equalsIgnoreCase(userCohort.name());

        log.debug("User {} cohort: {} / required: '{}' -> qualifies: {}",
                user.getId(), userCohort, requiredCohortName, qualifies);
        return qualifies;
    }
}

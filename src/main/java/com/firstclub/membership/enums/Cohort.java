package com.firstclub.membership.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumerated cohort identifiers. Stored as a string on User.cohort.
 * Using an enum prevents silent typo-mismatches when evaluating COHORT criteria.
 * To add a new cohort: add a constant here and seed the corresponding TierCriteria.
 */
@Getter
@RequiredArgsConstructor
public enum Cohort {
    PREMIUM_SHOPPER("High-value shoppers identified by purchase history"),
    EARLY_ADOPTER("Users who joined during the platform's early access phase"),
    INFLUENCER("Social influencer / brand ambassador"),
    CORPORATE("Corporate / B2B account");

    private final String description;
}

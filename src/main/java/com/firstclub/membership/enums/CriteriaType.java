package com.firstclub.membership.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CriteriaType {
    ORDER_COUNT("Minimum number of orders placed"),
    ORDER_VALUE("Minimum total order value in a rolling period"),
    COHORT("User belonging to a specific cohort/segment");

    private final String description;
}

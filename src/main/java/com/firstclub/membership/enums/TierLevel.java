package com.firstclub.membership.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TierLevel {
    // rank drives comparison — never use ordinal() since reordering constants would silently break comparisons
    SILVER(1, "Silver"),
    GOLD(2, "Gold"),
    PLATINUM(3, "Platinum");

    private final int rank;
    private final String displayName;

    // Used by upgrade/downgrade validation and auto-upgrade to find the best qualifying tier
    public boolean isHigherThan(TierLevel other) {
        return this.rank > other.rank;
    }

    public boolean isLowerThan(TierLevel other) {
        return this.rank < other.rank;
    }
}

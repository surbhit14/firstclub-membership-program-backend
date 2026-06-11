package com.firstclub.membership.enums;

// Stored per tier in DB — controls how multiple TierCriteria rows are combined during evaluation.
// ANY = OR logic (stop at first pass), ALL = AND logic (every criterion must pass).
// Current limitation: flat — cannot express mixed logic like "A AND (B OR C)".
public enum CriteriaLogic {
    ANY,  // qualifies if at least one criterion is met (stream().anyMatch)
    ALL   // qualifies only if every criterion is met (stream().allMatch)
}

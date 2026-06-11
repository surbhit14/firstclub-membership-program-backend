package com.firstclub.membership.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BenefitType {
    FREE_DELIVERY("Free Delivery on eligible orders"),
    EXTRA_DISCOUNT("Extra discount on selected items/categories"),
    EXCLUSIVE_DEALS("Access to exclusive deals"),
    EARLY_SALE_ACCESS("Early access to sales"),
    PRIORITY_SUPPORT("Priority customer support"),
    EXCLUSIVE_COUPONS("Exclusive discount coupons"),
    FASTER_DELIVERY("Faster delivery (same/next day)");

    private final String description;
}

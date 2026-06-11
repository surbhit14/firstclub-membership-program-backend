package com.firstclub.membership.service.strategy;

import com.firstclub.membership.enums.BenefitType;
import org.springframework.stereotype.Component;

/**
 * FREE_DELIVERY applies when orderAmount >= minOrderValue (or no minimum).
 * Silver: minOrderValue = ₹499. Gold/Platinum: no minimum.
 *
 * To add further logic (e.g., exclude certain pin codes), override isApplicable here.
 */
@Component
public class FreeDeliveryBenefitStrategy extends MinOrderValueBenefitStrategy {
    @Override
    public BenefitType getSupportedBenefitType() {
        return BenefitType.FREE_DELIVERY;
    }
}

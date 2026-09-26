package com.hostelmind.domain.model;

public enum SosAlertStatus {
    TRIGGERED,
    ACKNOWLEDGED,
    RESOLVED,
    FALSE_ALARM;

    public boolean canTransitionTo(SosAlertStatus target) {
        if (this == target) return true;
        return switch (this) {
            case TRIGGERED -> target == ACKNOWLEDGED || target == RESOLVED || target == FALSE_ALARM;
            case ACKNOWLEDGED -> target == RESOLVED || target == FALSE_ALARM;
            case RESOLVED, FALSE_ALARM -> false;
        };
    }
}

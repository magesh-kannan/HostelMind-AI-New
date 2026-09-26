package com.hostelmind.domain.model;

public enum InvoiceStatus {
    DRAFT,
    ISSUED,
    PARTIALLY_PAID,
    PAID,
    OVERDUE,
    CANCELLED;

    public boolean canTransitionTo(InvoiceStatus target) {
        if (this == target) return true;
        return switch (this) {
            case DRAFT -> target == ISSUED || target == CANCELLED;
            case ISSUED -> target == PARTIALLY_PAID || target == PAID || target == OVERDUE || target == CANCELLED;
            case OVERDUE -> target == PARTIALLY_PAID || target == PAID || target == CANCELLED;
            case PARTIALLY_PAID -> target == PAID || target == OVERDUE || target == CANCELLED;
            case PAID, CANCELLED -> false;
        };
    }
}

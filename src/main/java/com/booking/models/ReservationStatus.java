package com.booking.models;

public enum ReservationStatus {
    HOLD,
    CONFIRMED,
    CANCELLED;

    public boolean isHold() {
        return this == HOLD;
    }
}

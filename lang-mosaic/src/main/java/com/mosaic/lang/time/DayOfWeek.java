package com.mosaic.lang.time;

/**
 *
 */
public enum DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY,
    SATURDAY, SUNDAY;


    public boolean isWeekend() {
        return this == SATURDAY || this == SUNDAY;
    }

    public boolean isMonday() {
        return this == MONDAY;
    }

    public boolean isTuesday() {
        return this == TUESDAY;
    }

    public boolean isWednesday() {
        return this == WEDNESDAY;
    }

    public boolean isThursday() {
        return this == THURSDAY;
    }

    public boolean isFriday() {
        return this == FRIDAY;
    }

    public boolean isSaturday() {
        return this == SATURDAY;
    }

    public boolean isSunday() {
        return this == SUNDAY;
    }
}

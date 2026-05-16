package com.tracker.academictracker.domain.model;

public enum CourseStatus {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    DROPPED("Dropped");

    private final String displayName;
    CourseStatus(final String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
    @Override
    public String toString() {
        return displayName;
    }
}

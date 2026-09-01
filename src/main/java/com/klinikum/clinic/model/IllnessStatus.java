package com.klinikum.clinic.model;

public enum IllnessStatus {
    ACTIVE("Active", "badge bg-danger"),
    CHRONIC("Chronic", "badge bg-warning text-dark"),
    IN_REMISSION("In Remission", "badge bg-info text-dark"),
    CURED("Cured / Resolved", "badge bg-success");

    private final String displayName;
    private final String badgeClass;

    IllnessStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}

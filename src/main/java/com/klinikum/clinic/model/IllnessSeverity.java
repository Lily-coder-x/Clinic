package com.klinikum.clinic.model;

public enum IllnessSeverity {
    MILD("Mild", "badge bg-success"),
    MODERATE("Moderate", "badge bg-info text-dark"),
    SEVERE("Severe", "badge bg-warning text-dark"),
    CRITICAL("Critical", "badge bg-danger");

    private final String displayName;
    private final String badgeClass;

    IllnessSeverity(String displayName, String badgeClass) {
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

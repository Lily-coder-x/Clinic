package com.klinikum.clinic.model;

public enum MedicationStatus {
    ACTIVE("Active", "badge bg-success"),
    COMPLETED("Completed", "badge bg-secondary"),
    SUSPENDED("Suspended", "badge bg-warning text-dark"),
    DISCONTINUED("Discontinued", "badge bg-danger");

    private final String displayName;
    private final String badgeClass;

    MedicationStatus(String displayName, String badgeClass) {
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

package com.klinikum.clinic.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Gender gender = Gender.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false)
    @Builder.Default
    private BloodGroup bloodGroup = BloodGroup.UNKNOWN;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false)
    private String phone;

    @Email(message = "Please provide a valid email address")
    private String email;

    @Column(length = 500)
    private String address;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Column(name = "medical_history_notes", length = 3000)
    private String medicalHistoryNotes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("diagnosedDate DESC")
    @JsonManagedReference
    @Builder.Default
    private List<Illness> illnesses = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("startDate DESC")
    @JsonManagedReference
    @Builder.Default
    private List<Medication> medications = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public void addIllness(Illness illness) {
        if (illnesses == null) {
            illnesses = new ArrayList<>();
        }
        illnesses.add(illness);
        illness.setPatient(this);
    }

    public void removeIllness(Illness illness) {
        if (illnesses != null) {
            illnesses.remove(illness);
            illness.setPatient(null);
        }
    }

    public void addMedication(Medication medication) {
        if (medications == null) {
            medications = new ArrayList<>();
        }
        medications.add(medication);
        medication.setPatient(this);
    }

    public void removeMedication(Medication medication) {
        if (medications != null) {
            medications.remove(medication);
            medication.setPatient(null);
        }
    }

    public long getActiveIllnessCount() {
        if (illnesses == null) return 0;
        return illnesses.stream()
                .filter(i -> i.getStatus() == IllnessStatus.ACTIVE || i.getStatus() == IllnessStatus.CHRONIC)
                .count();
    }

    public long getActiveMedicationCount() {
        if (medications == null) return 0;
        return medications.stream()
                .filter(m -> m.getStatus() == MedicationStatus.ACTIVE)
                .count();
    }
}

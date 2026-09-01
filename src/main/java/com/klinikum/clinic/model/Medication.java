package com.klinikum.clinic.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "medications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Medication name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Dosage is required (e.g. 500mg)")
    @Column(nullable = false)
    private String dosage;

    @NotBlank(message = "Frequency is required (e.g. Twice daily)")
    @Column(nullable = false)
    private String frequency;

    @Column(name = "administration_route")
    private String route; // e.g. Oral, Intravenous, Topical, Inhalation

    @NotNull(message = "Start date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MedicationStatus status = MedicationStatus.ACTIVE;

    @Column(length = 1000)
    private String instructions;

    @Column(name = "prescribed_by")
    private String prescribedBy;

    @Column(length = 1000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;
}

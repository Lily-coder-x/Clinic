package com.klinikum.clinic.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "illnesses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Illness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Illness name is required")
    @Column(nullable = false)
    private String name;

    @Column(name = "icd_code")
    private String icdCode;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String symptoms;

    @NotNull(message = "Diagnosis date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "diagnosed_date", nullable = false)
    private LocalDate diagnosedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IllnessStatus status = IllnessStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IllnessSeverity severity = IllnessSeverity.MODERATE;

    @Column(length = 2000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;
}

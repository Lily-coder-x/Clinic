package com.klinikum.clinic.dto;

import com.klinikum.clinic.model.Patient;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ClinicStatsDto {
    private long totalPatients;
    private long totalIllnesses;
    private long activeIllnesses;
    private long chronicIllnesses;
    private long curedIllnesses;
    private long totalMedications;
    private long activeMedications;
    private List<Patient> recentPatients;
    private Map<String, Long> illnessDistribution;
}

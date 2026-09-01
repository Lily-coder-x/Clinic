package com.klinikum.clinic.service;

import com.klinikum.clinic.dto.ClinicStatsDto;
import com.klinikum.clinic.model.IllnessStatus;
import com.klinikum.clinic.model.MedicationStatus;
import com.klinikum.clinic.model.Patient;
import com.klinikum.clinic.repository.IllnessRepository;
import com.klinikum.clinic.repository.MedicationRepository;
import com.klinikum.clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final IllnessRepository illnessRepository;
    private final MedicationRepository medicationRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAllByOrderByLastNameAscFirstNameAsc();
    }

    public List<Patient> searchPatients(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPatients();
        }
        return patientRepository.searchPatients(keyword.trim());
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Patient getPatientOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + id));
    }

    @Transactional
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Transactional
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    public ClinicStatsDto getClinicStatistics() {
        long totalPatients = patientRepository.count();
        long totalIllnesses = illnessRepository.count();
        long activeIllnesses = illnessRepository.countByStatus(IllnessStatus.ACTIVE);
        long chronicIllnesses = illnessRepository.countByStatus(IllnessStatus.CHRONIC);
        long curedIllnesses = illnessRepository.countByStatus(IllnessStatus.CURED);
        long totalMedications = medicationRepository.count();
        long activeMedications = medicationRepository.countByStatus(MedicationStatus.ACTIVE);
        List<Patient> recentPatients = patientRepository.findTop5ByOrderByCreatedAtDesc();

        Map<String, Long> illnessDist = new LinkedHashMap<>();
        List<Object[]> commonIllnesses = illnessRepository.findMostCommonIllnesses();
        for (int i = 0; i < Math.min(commonIllnesses.size(), 6); i++) {
            Object[] row = commonIllnesses.get(i);
            illnessDist.put((String) row[0], (Long) row[1]);
        }

        return ClinicStatsDto.builder()
                .totalPatients(totalPatients)
                .totalIllnesses(totalIllnesses)
                .activeIllnesses(activeIllnesses)
                .chronicIllnesses(chronicIllnesses)
                .curedIllnesses(curedIllnesses)
                .totalMedications(totalMedications)
                .activeMedications(activeMedications)
                .recentPatients(recentPatients)
                .illnessDistribution(illnessDist)
                .build();
    }
}

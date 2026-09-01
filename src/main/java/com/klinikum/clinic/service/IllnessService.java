package com.klinikum.clinic.service;

import com.klinikum.clinic.model.Illness;
import com.klinikum.clinic.model.Patient;
import com.klinikum.clinic.repository.IllnessRepository;
import com.klinikum.clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IllnessService {

    private final IllnessRepository illnessRepository;
    private final PatientRepository patientRepository;

    public List<Illness> getIllnessesByPatientId(Long patientId) {
        return illnessRepository.findByPatientIdOrderByDiagnosedDateDesc(patientId);
    }

    public Optional<Illness> getIllnessById(Long id) {
        return illnessRepository.findById(id);
    }

    public Illness getIllnessOrThrow(Long id) {
        return illnessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Illness not found with ID: " + id));
    }

    @Transactional
    public Illness saveIllnessForPatient(Long patientId, Illness illness) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));
        patient.addIllness(illness);
        return illnessRepository.save(illness);
    }

    @Transactional
    public Illness updateIllness(Long id, Illness updated) {
        Illness existing = getIllnessOrThrow(id);
        existing.setName(updated.getName());
        existing.setIcdCode(updated.getIcdCode());
        existing.setDescription(updated.getDescription());
        existing.setSymptoms(updated.getSymptoms());
        existing.setDiagnosedDate(updated.getDiagnosedDate());
        existing.setStatus(updated.getStatus());
        existing.setSeverity(updated.getSeverity());
        existing.setNotes(updated.getNotes());
        return illnessRepository.save(existing);
    }

    @Transactional
    public void deleteIllness(Long id) {
        Illness illness = getIllnessOrThrow(id);
        if (illness.getPatient() != null) {
            illness.getPatient().removeIllness(illness);
        }
        illnessRepository.deleteById(id);
    }
}

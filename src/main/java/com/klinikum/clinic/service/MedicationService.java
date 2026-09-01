package com.klinikum.clinic.service;

import com.klinikum.clinic.model.Medication;
import com.klinikum.clinic.model.Patient;
import com.klinikum.clinic.repository.MedicationRepository;
import com.klinikum.clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final PatientRepository patientRepository;

    public List<Medication> getMedicationsByPatientId(Long patientId) {
        return medicationRepository.findByPatientIdOrderByStartDateDesc(patientId);
    }

    public Optional<Medication> getMedicationById(Long id) {
        return medicationRepository.findById(id);
    }

    public Medication getMedicationOrThrow(Long id) {
        return medicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medication not found with ID: " + id));
    }

    @Transactional
    public Medication saveMedicationForPatient(Long patientId, Medication medication) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));
        patient.addMedication(medication);
        return medicationRepository.save(medication);
    }

    @Transactional
    public Medication updateMedication(Long id, Medication updated) {
        Medication existing = getMedicationOrThrow(id);
        existing.setName(updated.getName());
        existing.setDosage(updated.getDosage());
        existing.setFrequency(updated.getFrequency());
        existing.setRoute(updated.getRoute());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        existing.setStatus(updated.getStatus());
        existing.setInstructions(updated.getInstructions());
        existing.setPrescribedBy(updated.getPrescribedBy());
        existing.setNotes(updated.getNotes());
        return medicationRepository.save(existing);
    }

    @Transactional
    public void deleteMedication(Long id) {
        Medication medication = getMedicationOrThrow(id);
        if (medication.getPatient() != null) {
            medication.getPatient().removeMedication(medication);
        }
        medicationRepository.deleteById(id);
    }
}

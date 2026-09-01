package com.klinikum.clinic.repository;

import com.klinikum.clinic.model.Medication;
import com.klinikum.clinic.model.MedicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    List<Medication> findByPatientIdOrderByStartDateDesc(Long patientId);

    List<Medication> findByStatus(MedicationStatus status);

    long countByStatus(MedicationStatus status);
}

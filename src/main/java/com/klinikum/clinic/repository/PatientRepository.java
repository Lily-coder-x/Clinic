package com.klinikum.clinic.repository;

import com.klinikum.clinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findAllByOrderByLastNameAscFirstNameAsc();

    @Query("SELECT DISTINCT p FROM Patient p LEFT JOIN p.illnesses i LEFT JOIN p.medications m WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
           "LOWER(p.phone) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "ORDER BY p.lastName ASC, p.firstName ASC")
    List<Patient> searchPatients(@Param("kw") String keyword);

    List<Patient> findTop5ByOrderByCreatedAtDesc();
}

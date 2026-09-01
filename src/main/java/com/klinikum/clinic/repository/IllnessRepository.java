package com.klinikum.clinic.repository;

import com.klinikum.clinic.model.Illness;
import com.klinikum.clinic.model.IllnessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IllnessRepository extends JpaRepository<Illness, Long> {

    List<Illness> findByPatientIdOrderByDiagnosedDateDesc(Long patientId);

    List<Illness> findByStatus(IllnessStatus status);

    long countByStatus(IllnessStatus status);

    @Query("SELECT i.name, COUNT(i) as total FROM Illness i GROUP BY i.name ORDER BY total DESC")
    List<Object[]> findMostCommonIllnesses();
}

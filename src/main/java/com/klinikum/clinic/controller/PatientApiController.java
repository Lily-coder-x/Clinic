package com.klinikum.clinic.controller;

import com.klinikum.clinic.dto.ClinicStatsDto;
import com.klinikum.clinic.model.Illness;
import com.klinikum.clinic.model.Medication;
import com.klinikum.clinic.model.Patient;
import com.klinikum.clinic.service.ExcelExportService;
import com.klinikum.clinic.service.IllnessService;
import com.klinikum.clinic.service.MedicationService;
import com.klinikum.clinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientApiController {

    private final PatientService patientService;
    private final IllnessService illnessService;
    private final MedicationService medicationService;
    private final ExcelExportService excelExportService;

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients(@RequestParam(value = "search", required = false) String search) {
        return ResponseEntity.ok(patientService.searchPatients(search));
    }

    @GetMapping("/stats")
    public ResponseEntity<ClinicStatsDto> getStats() {
        return ResponseEntity.ok(patientService.getClinicStatistics());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable("id") Long id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        Patient saved = patientService.savePatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("id") Long id, @Valid @RequestBody Patient patient) {
        return patientService.getPatientById(id).map(existing -> {
            existing.setFirstName(patient.getFirstName());
            existing.setLastName(patient.getLastName());
            existing.setDateOfBirth(patient.getDateOfBirth());
            existing.setGender(patient.getGender());
            existing.setBloodGroup(patient.getBloodGroup());
            existing.setPhone(patient.getPhone());
            existing.setEmail(patient.getEmail());
            existing.setAddress(patient.getAddress());
            existing.setEmergencyContactName(patient.getEmergencyContactName());
            existing.setEmergencyContactPhone(patient.getEmergencyContactPhone());
            existing.setMedicalHistoryNotes(patient.getMedicalHistoryNotes());
            return ResponseEntity.ok(patientService.savePatient(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable("id") Long id) {
        if (patientService.getPatientById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{patientId}/illnesses")
    public ResponseEntity<Illness> addIllness(@PathVariable("patientId") Long patientId, @Valid @RequestBody Illness illness) {
        return ResponseEntity.status(HttpStatus.CREATED).body(illnessService.saveIllnessForPatient(patientId, illness));
    }

    @PostMapping("/{patientId}/medications")
    public ResponseEntity<Medication> addMedication(@PathVariable("patientId") Long patientId, @Valid @RequestBody Medication medication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicationService.saveMedicationForPatient(patientId, medication));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportExcel() throws IOException {
        List<Patient> patients = patientService.getAllPatients();
        ByteArrayInputStream in = excelExportService.exportAllPatientsToExcel(patients);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=patients_export.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}

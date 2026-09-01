package com.klinikum.clinic.controller;

import com.klinikum.clinic.model.*;
import com.klinikum.clinic.service.ExcelExportService;
import com.klinikum.clinic.service.PdfReportService;
import com.klinikum.clinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final ExcelExportService excelExportService;
    private final PdfReportService pdfReportService;

    @GetMapping
    public String listPatients(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Patient> patients = patientService.searchPatients(search);
        model.addAttribute("patients", patients);
        model.addAttribute("search", search);
        return "patients/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("patient")) {
            model.addAttribute("patient", new Patient());
        }
        model.addAttribute("genders", Gender.values());
        model.addAttribute("bloodGroups", BloodGroup.values());
        return "patients/create";
    }

    @PostMapping
    public String createPatient(@Valid @ModelAttribute("patient") Patient patient,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values());
            model.addAttribute("bloodGroups", BloodGroup.values());
            return "patients/create";
        }

        Patient saved = patientService.savePatient(patient);
        redirectAttributes.addFlashAttribute("successMessage", "Patient " + saved.getFullName() + " has been registered successfully!");
        return "redirect:/patients/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String viewPatient(@PathVariable("id") Long id, Model model) {
        Patient patient = patientService.getPatientOrThrow(id);
        model.addAttribute("patient", patient);
        model.addAttribute("illnessStatuses", IllnessStatus.values());
        model.addAttribute("illnessSeverities", IllnessSeverity.values());
        model.addAttribute("medicationStatuses", MedicationStatus.values());
        model.addAttribute("newIllness", new Illness());
        model.addAttribute("newMedication", new Medication());
        return "patients/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Patient patient = patientService.getPatientOrThrow(id);
        model.addAttribute("patient", patient);
        model.addAttribute("genders", Gender.values());
        model.addAttribute("bloodGroups", BloodGroup.values());
        return "patients/edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePatient(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("patient") Patient patient,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values());
            model.addAttribute("bloodGroups", BloodGroup.values());
            return "patients/edit";
        }

        Patient existing = patientService.getPatientOrThrow(id);
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

        patientService.savePatient(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Patient details updated successfully!");
        return "redirect:/patients/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePatient(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Patient patient = patientService.getPatientOrThrow(id);
        String name = patient.getFullName();
        patientService.deletePatient(id);
        redirectAttributes.addFlashAttribute("successMessage", "Patient " + name + " and all medical records deleted successfully.");
        return "redirect:/patients";
    }

    @GetMapping("/{id}/report")
    public String viewPatientReport(@PathVariable("id") Long id, Model model) {
        Patient patient = patientService.getPatientOrThrow(id);
        model.addAttribute("patient", patient);
        model.addAttribute("reportGeneratedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm")));
        return "patients/report";
    }

    @GetMapping("/{id}/report/pdf")
    public ResponseEntity<InputStreamResource> downloadPatientPdfReport(@PathVariable("id") Long id) {
        Patient patient = patientService.getPatientOrThrow(id);
        ByteArrayInputStream pdfStream = pdfReportService.generatePatientReportPdf(patient);

        String filename = "patient_report_" + patient.getLastName() + "_" + patient.getFirstName() + "_" + id + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportAllPatientsToExcel() throws IOException {
        List<Patient> patients = patientService.getAllPatients();
        ByteArrayInputStream in = excelExportService.exportAllPatientsToExcel(patients);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String filename = "clinic_patients_database_" + timestamp + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/{id}/export/excel")
    public ResponseEntity<InputStreamResource> exportSinglePatientToExcel(@PathVariable("id") Long id) throws IOException {
        Patient patient = patientService.getPatientOrThrow(id);
        ByteArrayInputStream in = excelExportService.exportSinglePatientToExcel(patient);

        String filename = "patient_medical_record_" + patient.getLastName() + "_" + patient.getId() + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}

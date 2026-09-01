package com.klinikum.clinic;

import com.klinikum.clinic.model.*;
import com.klinikum.clinic.repository.PatientRepository;
import com.klinikum.clinic.service.ExcelExportService;
import com.klinikum.clinic.service.PatientService;
import com.klinikum.clinic.service.PdfReportService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class ClinicApplicationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private PdfReportService pdfReportService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Context loads and sample patients are initialized")
    void contextLoads() {
        List<Patient> patients = patientService.getAllPatients();
        assertThat(patients).isNotEmpty();
        assertThat(patients.size()).isGreaterThanOrEqualTo(4);
    }

    @Test
    @DisplayName("Dashboard endpoint returns 200 OK with statistics")
    void testDashboardPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("stats"));
    }

    @Test
    @DisplayName("Patient Directory endpoint returns 200 OK and lists patients")
    void testPatientsListPage() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/list"))
                .andExpect(model().attributeExists("patients"));
    }

    @Test
    @DisplayName("Search patients by illness name or patient name")
    void testSearchPatients() throws Exception {
        mockMvc.perform(get("/patients").param("search", "Diabetes"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/list"))
                .andExpect(model().attribute("patients", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Create new patient via POST and redirect to profile")
    void testCreatePatient() throws Exception {
        mockMvc.perform(post("/patients")
                        .param("firstName", "Alexander")
                        .param("lastName", "Fleming")
                        .param("dateOfBirth", "1975-08-06")
                        .param("gender", "MALE")
                        .param("bloodGroup", "O_POSITIVE")
                        .param("phone", "+1-555-9876")
                        .param("email", "alex.fleming@clinic.org")
                        .param("address", "100 Medical Center Way")
                        .param("emergencyContactName", "Sarah Fleming")
                        .param("emergencyContactPhone", "+1-555-9877")
                        .param("medicalHistoryNotes", "No allergies."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/patients/*"));

        List<Patient> found = patientService.searchPatients("Fleming");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getFirstName()).isEqualTo("Alexander");
    }

    @Test
    @DisplayName("Add and update illness for a patient")
    void testAddAndEditIllness() throws Exception {
        List<Patient> patients = patientService.getAllPatients();
        Patient patient = patients.get(0);

        // Add illness
        mockMvc.perform(post("/patients/{patientId}/illnesses", patient.getId())
                        .param("name", "Migraine Headache")
                        .param("icdCode", "G43.909")
                        .param("diagnosedDate", "2023-04-12")
                        .param("status", "ACTIVE")
                        .param("severity", "MODERATE")
                        .param("symptoms", "Throbbing pain, photosensitivity")
                        .param("notes", "Prescribed triptans."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/" + patient.getId()));

        Patient updated = patientService.getPatientOrThrow(patient.getId());
        assertThat(updated.getIllnesses()).anyMatch(i -> i.getName().equals("Migraine Headache"));
    }

    @Test
    @DisplayName("Add and delete medication for a patient")
    void testAddAndEditMedication() throws Exception {
        List<Patient> patients = patientService.getAllPatients();
        Patient patient = patients.get(0);

        // Add medication
        mockMvc.perform(post("/patients/{patientId}/medications", patient.getId())
                        .param("name", "Amoxicillin")
                        .param("dosage", "500 mg")
                        .param("frequency", "Three times daily")
                        .param("route", "Oral")
                        .param("startDate", "2024-02-01")
                        .param("status", "ACTIVE")
                        .param("instructions", "Take with food")
                        .param("prescribedBy", "Dr. House"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/" + patient.getId()));

        Patient updated = patientService.getPatientOrThrow(patient.getId());
        Medication addedMed = updated.getMedications().stream()
                .filter(m -> m.getName().equals("Amoxicillin"))
                .findFirst()
                .orElse(null);
        assertThat(addedMed).isNotNull();

        // Delete medication
        mockMvc.perform(post("/patients/{patientId}/medications/{id}/delete", patient.getId(), addedMed.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/" + patient.getId()));
    }

    @Test
    @DisplayName("Excel Export: Generates valid multi-sheet Excel file for all patients")
    void testExcelExportAllPatients() throws Exception {
        List<Patient> patients = patientService.getAllPatients();
        ByteArrayInputStream in = excelExportService.exportAllPatientsToExcel(patients);
        assertThat(in).isNotNull();

        // Validate workbook structure
        try (Workbook workbook = new XSSFWorkbook(in)) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(3);
            assertThat(workbook.getSheetName(0)).isEqualTo("Patients Overview");
            assertThat(workbook.getSheetName(1)).isEqualTo("Diagnosed Illnesses");
            assertThat(workbook.getSheetName(2)).isEqualTo("Prescribed Medications");
            assertThat(workbook.getSheetAt(0).getPhysicalNumberOfRows()).isGreaterThan(1);
        }

        // Validate HTTP endpoint
        mockMvc.perform(get("/patients/export/excel"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("clinic_patients_database_")))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Test
    @DisplayName("Excel Export: Generates valid single patient Excel dossier")
    void testExcelExportSinglePatient() throws Exception {
        List<Patient> patients = patientService.getAllPatients();
        Patient patient = patients.get(0);

        ByteArrayInputStream in = excelExportService.exportSinglePatientToExcel(patient);
        assertThat(in).isNotNull();

        try (Workbook workbook = new XSSFWorkbook(in)) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
            assertThat(workbook.getSheetName(0)).isEqualTo("Patient Medical Dossier");
            assertThat(workbook.getSheetAt(0).getPhysicalNumberOfRows()).isGreaterThan(5);
        }

        mockMvc.perform(get("/patients/{id}/export/excel", patient.getId()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("patient_medical_record_")));
    }

    @Test
    @DisplayName("PDF Report: Generates valid binary PDF document for individual patient")
    void testPdfReportGeneration() throws Exception {
        List<Patient> patients = patientService.getAllPatients();
        Patient patient = patients.get(0);

        ByteArrayInputStream pdfStream = pdfReportService.generatePatientReportPdf(patient);
        assertThat(pdfStream).isNotNull();
        byte[] pdfBytes = pdfStream.readAllBytes();
        assertThat(pdfBytes.length).isGreaterThan(100);
        // PDF header magic bytes "%PDF-"
        assertThat(new String(pdfBytes, 0, 5)).isEqualTo("%PDF-");

        mockMvc.perform(get("/patients/{id}/report/pdf", patient.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("Web Patient Report View returns 200 OK")
    void testWebPatientReport() throws Exception {
        List<Patient> patients = patientService.getAllPatients();
        Patient patient = patients.get(0);

        mockMvc.perform(get("/patients/{id}/report", patient.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/report"))
                .andExpect(model().attributeExists("patient", "reportGeneratedAt"));
    }

    @Test
    @DisplayName("REST API: CRUD and JSON endpoints work properly")
    void testPatientRestApi() throws Exception {
        // GET all
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(4))));

        // GET stats
        mockMvc.perform(get("/api/patients/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPatients", greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$.activeIllnesses", greaterThanOrEqualTo(1)));
    }
}

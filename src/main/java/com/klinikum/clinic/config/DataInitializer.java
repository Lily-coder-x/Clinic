package com.klinikum.clinic.config;

import com.klinikum.clinic.model.*;
import com.klinikum.clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;

    @Override
    public void run(String... args) {
        if (patientRepository.count() > 0) {
            log.info("Database already contains data, skipping initialization.");
            return;
        }

        log.info("Initializing sample clinic patient, illness, and medication records...");

        // Patient 1: Eleanor Vance
        Patient p1 = Patient.builder()
                .firstName("Eleanor")
                .lastName("Vance")
                .dateOfBirth(LocalDate.of(1982, 5, 14))
                .gender(Gender.FEMALE)
                .bloodGroup(BloodGroup.O_POSITIVE)
                .phone("+1-555-0192")
                .email("eleanor.vance@example.com")
                .address("742 Evergreen Terrace, Springfield, OR")
                .emergencyContactName("Hugh Vance (Spouse)")
                .emergencyContactPhone("+1-555-0193")
                .medicalHistoryNotes("Penicillin allergy. History of mild seasonal allergies. Appendectomy in 2012.")
                .build();

        p1.addIllness(Illness.builder()
                .name("Type 2 Diabetes Mellitus")
                .icdCode("E11.9")
                .description("Non-insulin-dependent diabetes mellitus without complications.")
                .symptoms("Polydipsia, polyuria, mild fatigue.")
                .diagnosedDate(LocalDate.of(2021, 3, 10))
                .status(IllnessStatus.CHRONIC)
                .severity(IllnessSeverity.MODERATE)
                .notes("HbA1c level 7.1% at last check. Diet modification recommended.")
                .build());

        p1.addIllness(Illness.builder()
                .name("Essential Hypertension")
                .icdCode("I10")
                .description("Primary systemic arterial hypertension.")
                .symptoms("Occasional morning headaches, dizziness.")
                .diagnosedDate(LocalDate.of(2020, 8, 15))
                .status(IllnessStatus.CHRONIC)
                .severity(IllnessSeverity.MILD)
                .notes("BP currently well-controlled around 125/80 mmHg.")
                .build());

        p1.addMedication(Medication.builder()
                .name("Metformin Hydrochloride")
                .dosage("500 mg")
                .frequency("Twice daily with meals")
                .route("Oral")
                .startDate(LocalDate.of(2021, 3, 15))
                .status(MedicationStatus.ACTIVE)
                .instructions("Take with breakfast and dinner to avoid gastrointestinal upset.")
                .prescribedBy("Dr. Sarah Jenkins")
                .notes("Standard first-line glycemic control.")
                .build());

        p1.addMedication(Medication.builder()
                .name("Lisinopril")
                .dosage("10 mg")
                .frequency("Once daily in the morning")
                .route("Oral")
                .startDate(LocalDate.of(2020, 8, 20))
                .status(MedicationStatus.ACTIVE)
                .instructions("Monitor blood pressure twice a week.")
                .prescribedBy("Dr. Sarah Jenkins")
                .notes("ACE inhibitor for blood pressure regulation.")
                .build());

        // Patient 2: James Rodriguez
        Patient p2 = Patient.builder()
                .firstName("James")
                .lastName("Rodriguez")
                .dateOfBirth(LocalDate.of(1995, 11, 23))
                .gender(Gender.MALE)
                .bloodGroup(BloodGroup.A_POSITIVE)
                .phone("+1-555-0348")
                .email("james.rodriguez@example.com")
                .address("1042 Elm Street, Seattle, WA")
                .emergencyContactName("Maria Rodriguez (Mother)")
                .emergencyContactPhone("+1-555-0349")
                .medicalHistoryNotes("No known drug allergies. Active marathon runner.")
                .build();

        p2.addIllness(Illness.builder()
                .name("Bronchial Asthma")
                .icdCode("J45.909")
                .description("Unspecified asthma, uncomplicated.")
                .symptoms("Wheezing after intense cardio workout, shortness of breath in cold weather.")
                .diagnosedDate(LocalDate.of(2018, 6, 4))
                .status(IllnessStatus.ACTIVE)
                .severity(IllnessSeverity.MODERATE)
                .notes("Exercise-induced bronchospasm triggered during winter training.")
                .build());

        p2.addMedication(Medication.builder()
                .name("Albuterol Sulfate Inhaler")
                .dosage("90 mcg/actuation")
                .frequency("1-2 puffs every 4-6 hours as needed")
                .route("Inhalation")
                .startDate(LocalDate.of(2018, 6, 5))
                .status(MedicationStatus.ACTIVE)
                .instructions("Inhale 15 minutes before physical exertion or at onset of symptoms.")
                .prescribedBy("Dr. David Zhang")
                .notes("Rescue bronchodilator.")
                .build());

        p2.addMedication(Medication.builder()
                .name("Fluticasone Propionate")
                .dosage("110 mcg")
                .frequency("1 inhalation twice daily")
                .route("Inhalation")
                .startDate(LocalDate.of(2022, 1, 10))
                .status(MedicationStatus.ACTIVE)
                .instructions("Rinse mouth with water after use.")
                .prescribedBy("Dr. David Zhang")
                .notes("Maintenance corticosteroid.")
                .build());

        // Patient 3: Clara Schmidt
        Patient p3 = Patient.builder()
                .firstName("Clara")
                .lastName("Schmidt")
                .dateOfBirth(LocalDate.of(1968, 2, 8))
                .gender(Gender.FEMALE)
                .bloodGroup(BloodGroup.B_NEGATIVE)
                .phone("+1-555-0781")
                .email("clara.schmidt@example.com")
                .address("58 Beethoven Avenue, Austin, TX")
                .emergencyContactName("Robert Schmidt (Brother)")
                .emergencyContactPhone("+1-555-0782")
                .medicalHistoryNotes("Sulfa drug intolerance. Knee arthroscopy in 2019.")
                .build();

        p3.addIllness(Illness.builder()
                .name("Primary Osteoarthritis of Knee")
                .icdCode("M17.11")
                .description("Bilateral knee joint degenerative wear.")
                .symptoms("Joint stiffness upon waking, localized swelling after walking.")
                .diagnosedDate(LocalDate.of(2019, 10, 18))
                .status(IllnessStatus.CHRONIC)
                .severity(IllnessSeverity.SEVERE)
                .notes("Physiotherapy sessions twice monthly.")
                .build());

        p3.addIllness(Illness.builder()
                .name("Acute Bronchitis")
                .icdCode("J20.9")
                .description("Viral infection of bronchial tubes.")
                .symptoms("Productive cough, low-grade fever.")
                .diagnosedDate(LocalDate.of(2024, 1, 15))
                .status(IllnessStatus.CURED)
                .severity(IllnessSeverity.MILD)
                .notes("Fully resolved after 10 days.")
                .build());

        p3.addMedication(Medication.builder()
                .name("Celecoxib")
                .dosage("200 mg")
                .frequency("Once daily after meals")
                .route("Oral")
                .startDate(LocalDate.of(2023, 5, 1))
                .status(MedicationStatus.ACTIVE)
                .instructions("Take with food. Do not exceed prescribed dosage.")
                .prescribedBy("Dr. Michael Lawson")
                .notes("NSAID for osteoarthritis pain relief.")
                .build());

        p3.addMedication(Medication.builder()
                .name("Azithromycin")
                .dosage("500 mg")
                .frequency("Once daily for 5 days")
                .route("Oral")
                .startDate(LocalDate.of(2024, 1, 15))
                .endDate(LocalDate.of(2024, 1, 20))
                .status(MedicationStatus.COMPLETED)
                .instructions("Complete the entire 5-day course.")
                .prescribedBy("Dr. Sarah Jenkins")
                .notes("Antibiotic course for secondary bacterial infection.")
                .build());

        // Patient 4: Arthur Pendelton
        Patient p4 = Patient.builder()
                .firstName("Arthur")
                .lastName("Pendelton")
                .dateOfBirth(LocalDate.of(1954, 9, 30))
                .gender(Gender.MALE)
                .bloodGroup(BloodGroup.AB_POSITIVE)
                .phone("+1-555-0914")
                .email("arthur.pendelton@example.com")
                .address("312 River Road, Chicago, IL")
                .emergencyContactName("Linda Pendelton (Daughter)")
                .emergencyContactPhone("+1-555-0915")
                .medicalHistoryNotes("Coronary artery bypass graft (CABG) in 2015. Pacemaker installed in 2021.")
                .build();

        p4.addIllness(Illness.builder()
                .name("Coronary Artery Disease")
                .icdCode("I25.10")
                .description("Atherosclerotic heart disease of native coronary artery.")
                .symptoms("Occasional chest tightness on exertion.")
                .diagnosedDate(LocalDate.of(2015, 4, 12))
                .status(IllnessStatus.CHRONIC)
                .severity(IllnessSeverity.SEVERE)
                .notes("Monitored with annual echocardiogram and stress test.")
                .build());

        p4.addIllness(Illness.builder()
                .name("Hyperlipidemia")
                .icdCode("E78.5")
                .description("Elevated serum cholesterol and triglycerides.")
                .symptoms("Asymptomatic.")
                .diagnosedDate(LocalDate.of(2016, 2, 22))
                .status(IllnessStatus.CHRONIC)
                .severity(IllnessSeverity.MODERATE)
                .notes("LDL target < 70 mg/dL.")
                .build());

        p4.addMedication(Medication.builder()
                .name("Atorvastatin Calcium")
                .dosage("40 mg")
                .frequency("Once daily at bedtime")
                .route("Oral")
                .startDate(LocalDate.of(2016, 2, 25))
                .status(MedicationStatus.ACTIVE)
                .instructions("Take at night with or without food.")
                .prescribedBy("Dr. Emily Thorne")
                .notes("High-intensity statin therapy.")
                .build());

        p4.addMedication(Medication.builder()
                .name("Aspirin (Enteric-Coated)")
                .dosage("81 mg")
                .frequency("Once daily with food")
                .route("Oral")
                .startDate(LocalDate.of(2015, 5, 1))
                .status(MedicationStatus.ACTIVE)
                .instructions("Do not crush or chew tablet.")
                .prescribedBy("Dr. Emily Thorne")
                .notes("Antiplatelet therapy.")
                .build());

        patientRepository.saveAll(List.of(p1, p2, p3, p4));
        log.info("Initialized {} patient records successfully.", patientRepository.count());
    }
}

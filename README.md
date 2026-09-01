# 🏥 KLINIKUM Care — Clinic & Patient Management System

A comprehensive, modern clinic management web application built with **Spring Boot 3**, **Spring Data JPA**, **Thymeleaf**, and **Bootstrap 5**. Designed for clinics and healthcare providers to manage patient health records, diagnose medical conditions, prescribe medication regimens, generate professional PDF medical reports, and export clinical data to multi-sheet Excel spreadsheets.

---

## 🌟 Key Features

### 👤 Patient Management
- **Full Demographic Records**: Track name, date of birth, gender, blood group, contact information, emergency contacts, and medical history/allergies.
- **Fast Search & Filtering**: Instant search across patient names, contact numbers, email addresses, diagnosed illness names, and prescribed medications.
- **Detailed Medical Charts**: Unified view of active medical conditions, ongoing prescriptions, and patient background.

### 🩺 Diagnostic & Illness Tracking
- **Diagnosis Management**: Record illnesses with ICD-10 codes, diagnosis dates, symptoms, severity levels (*Mild, Moderate, Severe, Critical*), and clinical notes.
- **Condition Statuses**: Track active, chronic, in-remission, and cured/resolved conditions.

### 💊 Medication & Prescription Regimens
- **Prescription System**: Record medications, dosage, administration routes (Oral, IV, Topical, etc.), frequency (e.g., Twice Daily), duration dates, and prescribing physician details.
- **Prescription Statuses**: Track active, completed, suspended, or discontinued medications.

### 📊 Interactive Analytics Dashboard
- Key performance metrics: Total registered patients, active illness cases, ongoing prescriptions, and cured cases.
- Breakdown of frequent diagnoses and quick-access recent registrations.

### 📄 Export & Reporting
- **Multi-Sheet Excel Export (Apache POI)**:
  - Export entire clinic database (*Patients, Diagnoses, Medications, Clinic Summary*) in a formatted `.xlsx` workbook.
  - Export individual patient dossier to Excel.
- **Professional PDF Medical Reports (OpenPDF)**:
  - One-click downloadable and printable official clinical reports complete with patient demographics, diagnostic timeline, active prescriptions, and physician sign-off sections.

### 🌐 Internationalization (i18n) & Language Switch
- Seamless live switching between **English (EN)** and **German (DE)**.
- Persistent language preference saved via browser cookies.

### 🗄️ Embedded Database & H2 Web Console
- Zero-config in-memory H2 database with automatic seed data for testing.
- Direct quick-access button to the interactive **H2 Database Console** from the top navigation bar.

### 🔌 RESTful Web API
- Full JSON REST API endpoints supporting programmatic integration (`/api/patients`).

---

## 🛠️ Technology Stack

| Component | Technology |
|---|---|
| **Backend Framework** | Java 17, Spring Boot 3.4.1 |
| **Data & Persistence** | Spring Data JPA, Hibernate, H2 Database |
| **Template Engine** | Thymeleaf, Thymeleaf Layout / Spring Security integration |
| **UI Framework** | Bootstrap 5.3.3, Bootstrap Icons |
| **Excel Generation** | Apache POI 5.2.5 (`poi-ooxml`) |
| **PDF Generation** | OpenPDF (LibrePDF) 1.3.30 |
| **Build & Testing** | Maven Wrapper, JUnit 5, Mockito, Spring MVC MockMvc |

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK)**: Version 17 or higher installed (`java -version`)
- *(Optional)* Maven (the project includes `./mvnw` / `.\mvnw.cmd` wrapper)

### Running the Application

1. **Clone or open the project folder** in your terminal.
2. **Start the application** using the Maven wrapper:

   **Windows (PowerShell / Command Prompt):**
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   **Linux / macOS:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

---

## 🗄️ H2 Database Web Console

To inspect or query the database directly in your browser:

1. Click the **H2-Konsole** button in the top navigation bar or go to:
   `http://localhost:8080/h2-console`
2. Enter the connection settings:
   - **Saved Settings**: `Generic H2 (Embedded)`
   - **Driver Class**: `org.h2.Driver`
   - **JDBC URL**: `jdbc:h2:mem:clinicdb`
   - **User Name**: `sa`
   - **Password**: *(leave blank)*
3. Click **Connect**.

> **Note on Data Persistence**: By default, data is stored in-memory (`jdbc:h2:mem:clinicdb`). To switch to file-based persistent storage across restarts, change `spring.datasource.url` in `src/main/resources/application.properties` to:
> ```properties
> spring.datasource.url=jdbc:h2:file:./data/clinicdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
> ```

---

## 🧪 Running Tests & Building

### Run Automated Tests
```powershell
.\mvnw.cmd test
```

### Build Runnable JAR
```powershell
.\mvnw.cmd package
```
The executable JAR file will be generated in the `target/` directory:
```powershell
java -jar target/Clinic-0.0.1-SNAPSHOT.jar
```

---

## 📡 REST API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/patients` | Retrieve all patients (supports `?search=` query parameter) |
| `GET` | `/api/patients/{id}` | Retrieve single patient with illnesses & medications |
| `POST` | `/api/patients` | Register a new patient (JSON body) |
| `PUT` | `/api/patients/{id}` | Update existing patient details |
| `DELETE` | `/api/patients/{id}` | Delete a patient and associated records |
| `POST` | `/api/patients/{id}/illnesses` | Add a diagnosis / illness to a patient |
| `POST` | `/api/patients/{id}/medications` | Prescribe a medication to a patient |

---

## 📁 Project Structure

```
Clinic/
├── src/
│   ├── main/
│   │   ├── java/com/klinikum/clinic/
│   │   │   ├── config/              # WebMvcConfig (i18n & Locale settings)
│   │   │   ├── controller/          # MVC Controllers & REST API Controllers
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── model/               # JPA Entities (Patient, Illness, Medication) & Enums
│   │   │   ├── repository/         # Spring Data JPA Repositories
│   │   │   ├── service/             # Business Logic, Excel & PDF Generation
│   │   │   └── ClinicApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── messages.properties       # Default (EN) localization bundle
│   │       ├── messages_en.properties    # English localization bundle
│   │       ├── messages_de.properties    # German localization bundle
│   │       └── templates/                # Thymeleaf HTML views & fragments
│   └── test/java/com/klinikum/clinic/   # Automated Unit & Integration Tests
├── pom.xml
└── README.md
```

---

## 📄 License
This project is developed for clinic management and electronic medical record management. All rights reserved.

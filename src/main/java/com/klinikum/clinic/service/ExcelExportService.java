package com.klinikum.clinic.service;

import com.klinikum.clinic.model.Illness;
import com.klinikum.clinic.model.Medication;
import com.klinikum.clinic.model.Patient;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ByteArrayInputStream exportAllPatientsToExcel(List<Patient> patients) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Styles
            CellStyle headerStyle = createHeaderStyle(workbook, IndexedColors.DARK_BLUE.getIndex());
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle boldStyle = createBoldStyle(workbook);

            // 1. Patients Sheet
            Sheet patientSheet = workbook.createSheet("Patients Overview");
            patientSheet.createFreezePane(0, 1);
            String[] patientHeaders = {
                    "Patient ID", "First Name", "Last Name", "Gender", "Date of Birth",
                    "Age", "Blood Group", "Phone", "Email", "Address",
                    "Emergency Contact", "Emergency Phone", "Active Illnesses", "Active Medications", "Registered Date"
            };

            Row pHeaderRow = patientSheet.createRow(0);
            for (int i = 0; i < patientHeaders.length; i++) {
                Cell cell = pHeaderRow.createCell(i);
                cell.setCellValue(patientHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int pRowIdx = 1;
            for (Patient p : patients) {
                Row row = patientSheet.createRow(pRowIdx++);
                row.createCell(0).setCellValue(p.getId() != null ? p.getId() : 0);
                row.createCell(1).setCellValue(p.getFirstName() != null ? p.getFirstName() : "");
                row.createCell(2).setCellValue(p.getLastName() != null ? p.getLastName() : "");
                row.createCell(3).setCellValue(p.getGender() != null ? p.getGender().getDisplayName() : "");
                row.createCell(4).setCellValue(p.getDateOfBirth() != null ? p.getDateOfBirth().format(DATE_FORMATTER) : "");
                row.createCell(5).setCellValue(p.getAge());
                row.createCell(6).setCellValue(p.getBloodGroup() != null ? p.getBloodGroup().getDisplayName() : "");
                row.createCell(7).setCellValue(p.getPhone() != null ? p.getPhone() : "");
                row.createCell(8).setCellValue(p.getEmail() != null ? p.getEmail() : "");
                row.createCell(9).setCellValue(p.getAddress() != null ? p.getAddress() : "");
                row.createCell(10).setCellValue(p.getEmergencyContactName() != null ? p.getEmergencyContactName() : "");
                row.createCell(11).setCellValue(p.getEmergencyContactPhone() != null ? p.getEmergencyContactPhone() : "");
                row.createCell(12).setCellValue(p.getActiveIllnessCount());
                row.createCell(13).setCellValue(p.getActiveMedicationCount());
                row.createCell(14).setCellValue(p.getCreatedAt() != null ? p.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "");

                for (int col = 0; col < patientHeaders.length; col++) {
                    if (col != 4 && col != 14) {
                        row.getCell(col).setCellStyle(dataStyle);
                    } else {
                        row.getCell(col).setCellStyle(dateStyle);
                    }
                }
            }

            for (int i = 0; i < patientHeaders.length; i++) {
                patientSheet.autoSizeColumn(i);
            }

            // 2. Illnesses Sheet
            Sheet illnessSheet = workbook.createSheet("Diagnosed Illnesses");
            illnessSheet.createFreezePane(0, 1);
            String[] illnessHeaders = {
                    "Illness ID", "Patient ID", "Patient Name", "Illness Name", "ICD Code",
                    "Status", "Severity", "Diagnosed Date", "Description", "Symptoms", "Notes"
            };

            Row iHeaderRow = illnessSheet.createRow(0);
            for (int i = 0; i < illnessHeaders.length; i++) {
                Cell cell = iHeaderRow.createCell(i);
                cell.setCellValue(illnessHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int iRowIdx = 1;
            for (Patient p : patients) {
                if (p.getIllnesses() != null) {
                    for (Illness ill : p.getIllnesses()) {
                        Row row = illnessSheet.createRow(iRowIdx++);
                        row.createCell(0).setCellValue(ill.getId() != null ? ill.getId() : 0);
                        row.createCell(1).setCellValue(p.getId() != null ? p.getId() : 0);
                        row.createCell(2).setCellValue(p.getFullName());
                        row.createCell(3).setCellValue(ill.getName() != null ? ill.getName() : "");
                        row.createCell(4).setCellValue(ill.getIcdCode() != null ? ill.getIcdCode() : "");
                        row.createCell(5).setCellValue(ill.getStatus() != null ? ill.getStatus().getDisplayName() : "");
                        row.createCell(6).setCellValue(ill.getSeverity() != null ? ill.getSeverity().getDisplayName() : "");
                        row.createCell(7).setCellValue(ill.getDiagnosedDate() != null ? ill.getDiagnosedDate().format(DATE_FORMATTER) : "");
                        row.createCell(8).setCellValue(ill.getDescription() != null ? ill.getDescription() : "");
                        row.createCell(9).setCellValue(ill.getSymptoms() != null ? ill.getSymptoms() : "");
                        row.createCell(10).setCellValue(ill.getNotes() != null ? ill.getNotes() : "");

                        for (int col = 0; col < illnessHeaders.length; col++) {
                            row.getCell(col).setCellStyle(dataStyle);
                        }
                    }
                }
            }

            for (int i = 0; i < illnessHeaders.length; i++) {
                illnessSheet.autoSizeColumn(i);
            }

            // 3. Medications Sheet
            Sheet medSheet = workbook.createSheet("Prescribed Medications");
            medSheet.createFreezePane(0, 1);
            String[] medHeaders = {
                    "Medication ID", "Patient ID", "Patient Name", "Medication Name",
                    "Dosage", "Frequency", "Route", "Status", "Start Date", "End Date",
                    "Prescribed By", "Instructions", "Notes"
            };

            Row mHeaderRow = medSheet.createRow(0);
            for (int i = 0; i < medHeaders.length; i++) {
                Cell cell = mHeaderRow.createCell(i);
                cell.setCellValue(medHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int mRowIdx = 1;
            for (Patient p : patients) {
                if (p.getMedications() != null) {
                    for (Medication med : p.getMedications()) {
                        Row row = medSheet.createRow(mRowIdx++);
                        row.createCell(0).setCellValue(med.getId() != null ? med.getId() : 0);
                        row.createCell(1).setCellValue(p.getId() != null ? p.getId() : 0);
                        row.createCell(2).setCellValue(p.getFullName());
                        row.createCell(3).setCellValue(med.getName() != null ? med.getName() : "");
                        row.createCell(4).setCellValue(med.getDosage() != null ? med.getDosage() : "");
                        row.createCell(5).setCellValue(med.getFrequency() != null ? med.getFrequency() : "");
                        row.createCell(6).setCellValue(med.getRoute() != null ? med.getRoute() : "");
                        row.createCell(7).setCellValue(med.getStatus() != null ? med.getStatus().getDisplayName() : "");
                        row.createCell(8).setCellValue(med.getStartDate() != null ? med.getStartDate().format(DATE_FORMATTER) : "");
                        row.createCell(9).setCellValue(med.getEndDate() != null ? med.getEndDate().format(DATE_FORMATTER) : "-");
                        row.createCell(10).setCellValue(med.getPrescribedBy() != null ? med.getPrescribedBy() : "");
                        row.createCell(11).setCellValue(med.getInstructions() != null ? med.getInstructions() : "");
                        row.createCell(12).setCellValue(med.getNotes() != null ? med.getNotes() : "");

                        for (int col = 0; col < medHeaders.length; col++) {
                            row.getCell(col).setCellStyle(dataStyle);
                        }
                    }
                }
            }

            for (int i = 0; i < medHeaders.length; i++) {
                medSheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream exportSinglePatientToExcel(Patient patient) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle sectionHeaderStyle = createSectionHeaderStyle(workbook);
            CellStyle tableHeaderStyle = createHeaderStyle(workbook, IndexedColors.GREY_50_PERCENT.getIndex());
            CellStyle labelStyle = createBoldStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            Sheet sheet = workbook.createSheet("Patient Medical Dossier");

            // Title Banner
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("CLINIC MEDICAL RECORD - " + patient.getFullName().toUpperCase());
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            // Patient Demographics Section
            int r = 2;
            Row sec1 = sheet.createRow(r++);
            Cell sec1Cell = sec1.createCell(0);
            sec1Cell.setCellValue("1. PATIENT DEMOGRAPHICS & CONTACT INFO");
            sec1Cell.setCellStyle(sectionHeaderStyle);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 7));

            r = addPatientInfoRow(sheet, r, "Patient ID:", String.valueOf(patient.getId()), "Blood Group:", patient.getBloodGroup().getDisplayName(), labelStyle, dataStyle);
            r = addPatientInfoRow(sheet, r, "Full Name:", patient.getFullName(), "Date of Birth:", patient.getDateOfBirth() != null ? patient.getDateOfBirth().format(DATE_FORMATTER) + " (" + patient.getAge() + " yrs)" : "-", labelStyle, dataStyle);
            r = addPatientInfoRow(sheet, r, "Gender:", patient.getGender().getDisplayName(), "Phone:", patient.getPhone(), labelStyle, dataStyle);
            r = addPatientInfoRow(sheet, r, "Email:", patient.getEmail() != null ? patient.getEmail() : "-", "Address:", patient.getAddress() != null ? patient.getAddress() : "-", labelStyle, dataStyle);
            r = addPatientInfoRow(sheet, r, "Emergency Contact:", patient.getEmergencyContactName() != null ? patient.getEmergencyContactName() : "-", "Emergency Phone:", patient.getEmergencyContactPhone() != null ? patient.getEmergencyContactPhone() : "-", labelStyle, dataStyle);
            r = addPatientInfoRow(sheet, r, "Medical History:", patient.getMedicalHistoryNotes() != null ? patient.getMedicalHistoryNotes() : "None noted", "", "", labelStyle, dataStyle);

            // Illnesses Section
            r++;
            Row sec2 = sheet.createRow(r++);
            Cell sec2Cell = sec2.createCell(0);
            sec2Cell.setCellValue("2. DIAGNOSED ILLNESSES & MEDICAL CONDITIONS");
            sec2Cell.setCellStyle(sectionHeaderStyle);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 7));

            String[] illCols = {"#", "Illness Name", "ICD Code", "Status", "Severity", "Diagnosed Date", "Symptoms / Notes"};
            Row illHeaderRow = sheet.createRow(r++);
            for (int i = 0; i < illCols.length; i++) {
                Cell c = illHeaderRow.createCell(i);
                c.setCellValue(illCols[i]);
                c.setCellStyle(tableHeaderStyle);
            }

            if (patient.getIllnesses() == null || patient.getIllnesses().isEmpty()) {
                Row emptyRow = sheet.createRow(r++);
                Cell c = emptyRow.createCell(0);
                c.setCellValue("No illness records on file.");
                c.setCellStyle(dataStyle);
            } else {
                int count = 1;
                for (Illness ill : patient.getIllnesses()) {
                    Row row = sheet.createRow(r++);
                    row.createCell(0).setCellValue(count++);
                    row.createCell(1).setCellValue(ill.getName() != null ? ill.getName() : "");
                    row.createCell(2).setCellValue(ill.getIcdCode() != null ? ill.getIcdCode() : "-");
                    row.createCell(3).setCellValue(ill.getStatus() != null ? ill.getStatus().getDisplayName() : "");
                    row.createCell(4).setCellValue(ill.getSeverity() != null ? ill.getSeverity().getDisplayName() : "");
                    row.createCell(5).setCellValue(ill.getDiagnosedDate() != null ? ill.getDiagnosedDate().format(DATE_FORMATTER) : "");
                    row.createCell(6).setCellValue((ill.getSymptoms() != null ? ill.getSymptoms() : "") + (ill.getNotes() != null ? " | " + ill.getNotes() : ""));

                    for (int i = 0; i < illCols.length; i++) {
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }

            // Medications Section
            r++;
            Row sec3 = sheet.createRow(r++);
            Cell sec3Cell = sec3.createCell(0);
            sec3Cell.setCellValue("3. MEDICATIONS & PRESCRIPTION SCHEDULE");
            sec3Cell.setCellStyle(sectionHeaderStyle);
            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 7));

            String[] medCols = {"#", "Medication Name", "Dosage", "Frequency", "Route", "Status", "Duration", "Instructions & Doctor"};
            Row medHeaderRow = sheet.createRow(r++);
            for (int i = 0; i < medCols.length; i++) {
                Cell c = medHeaderRow.createCell(i);
                c.setCellValue(medCols[i]);
                c.setCellStyle(tableHeaderStyle);
            }

            if (patient.getMedications() == null || patient.getMedications().isEmpty()) {
                Row emptyRow = sheet.createRow(r++);
                Cell c = emptyRow.createCell(0);
                c.setCellValue("No medication records on file.");
                c.setCellStyle(dataStyle);
            } else {
                int count = 1;
                for (Medication med : patient.getMedications()) {
                    Row row = sheet.createRow(r++);
                    row.createCell(0).setCellValue(count++);
                    row.createCell(1).setCellValue(med.getName() != null ? med.getName() : "");
                    row.createCell(2).setCellValue(med.getDosage() != null ? med.getDosage() : "");
                    row.createCell(3).setCellValue(med.getFrequency() != null ? med.getFrequency() : "");
                    row.createCell(4).setCellValue(med.getRoute() != null ? med.getRoute() : "-");
                    row.createCell(5).setCellValue(med.getStatus() != null ? med.getStatus().getDisplayName() : "");
                    String duration = (med.getStartDate() != null ? med.getStartDate().format(DATE_FORMATTER) : "") + " to " + (med.getEndDate() != null ? med.getEndDate().format(DATE_FORMATTER) : "Ongoing");
                    row.createCell(6).setCellValue(duration);
                    String details = (med.getInstructions() != null ? med.getInstructions() : "") + (med.getPrescribedBy() != null ? " [Dr: " + med.getPrescribedBy() + "]" : "");
                    row.createCell(7).setCellValue(details);

                    for (int i = 0; i < medCols.length; i++) {
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }

            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private int addPatientInfoRow(Sheet sheet, int rowIdx, String label1, String val1, String label2, String val2, CellStyle labelStyle, CellStyle dataStyle) {
        Row row = sheet.createRow(rowIdx);
        Cell l1 = row.createCell(0);
        l1.setCellValue(label1);
        l1.setCellStyle(labelStyle);

        Cell v1 = row.createCell(1);
        v1.setCellValue(val1);
        v1.setCellStyle(dataStyle);

        if (!label2.isEmpty()) {
            Cell l2 = row.createCell(3);
            l2.setCellValue(label2);
            l2.setCellStyle(labelStyle);

            Cell v2 = row.createCell(4);
            v2.setCellValue(val2);
            v2.setCellStyle(dataStyle);
        }
        return rowIdx + 1;
    }

    private CellStyle createHeaderStyle(Workbook workbook, short bgColor) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(bgColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(style);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createSectionHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        setBorders(style);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        setBorders(style);
        return style;
    }

    private void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}

package com.klinikum.clinic.service;

import com.klinikum.clinic.model.Illness;
import com.klinikum.clinic.model.Medication;
import com.klinikum.clinic.model.Patient;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ByteArrayInputStream generatePatientReportPdf(Patient patient) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font clinicTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(13, 71, 161));
            Font clinicSubtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(13, 71, 161));
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font smallItalicFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);

            // 1. Clinic Header
            Paragraph clinicTitle = new Paragraph("CLINIC CARE HEALTHCARE CENTER", clinicTitleFont);
            clinicTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(clinicTitle);

            Paragraph clinicSub = new Paragraph("Comprehensive Patient Medical Dossier & Diagnostic Report\nGenerated on: " + LocalDateTime.now().format(DATETIME_FORMATTER), clinicSubtitleFont);
            clinicSub.setAlignment(Element.ALIGN_CENTER);
            clinicSub.setSpacingAfter(15);
            document.add(clinicSub);

            // Separator Line
            PdfPTable separator = new PdfPTable(1);
            separator.setWidthPercentage(100);
            PdfPCell sepCell = new PdfPCell();
            sepCell.setBackgroundColor(new Color(13, 71, 161));
            sepCell.setFixedHeight(2);
            sepCell.setBorder(Rectangle.NO_BORDER);
            separator.addCell(sepCell);
            document.add(separator);

            document.add(new Paragraph(" "));

            // 2. Patient Demographics Box
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{22, 28, 22, 28});

            PdfPCell infoHeader = new PdfPCell(new Phrase("PATIENT DEMOGRAPHICS & IDENTIFICATION", sectionTitleFont));
            infoHeader.setColspan(4);
            infoHeader.setBackgroundColor(new Color(235, 243, 255));
            infoHeader.setPadding(6);
            infoTable.addCell(infoHeader);

            addInfoCell(infoTable, "Patient ID:", String.valueOf(patient.getId()), boldFont, regularFont);
            addInfoCell(infoTable, "Blood Group:", patient.getBloodGroup().getDisplayName(), boldFont, regularFont);

            addInfoCell(infoTable, "Full Name:", patient.getFullName(), boldFont, regularFont);
            addInfoCell(infoTable, "Gender / Age:", patient.getGender().getDisplayName() + " / " + patient.getAge() + " yrs", boldFont, regularFont);

            addInfoCell(infoTable, "Date of Birth:", patient.getDateOfBirth() != null ? patient.getDateOfBirth().format(DATE_FORMATTER) : "-", boldFont, regularFont);
            addInfoCell(infoTable, "Phone:", patient.getPhone(), boldFont, regularFont);

            addInfoCell(infoTable, "Email:", patient.getEmail() != null ? patient.getEmail() : "-", boldFont, regularFont);
            addInfoCell(infoTable, "Address:", patient.getAddress() != null ? patient.getAddress() : "-", boldFont, regularFont);

            addInfoCell(infoTable, "Emergency Contact:", patient.getEmergencyContactName() != null ? patient.getEmergencyContactName() : "-", boldFont, regularFont);
            addInfoCell(infoTable, "Emergency Phone:", patient.getEmergencyContactPhone() != null ? patient.getEmergencyContactPhone() : "-", boldFont, regularFont);

            // Medical History row
            PdfPCell medHistLabel = new PdfPCell(new Phrase("Medical History / Allergies:", boldFont));
            medHistLabel.setBackgroundColor(new Color(248, 249, 250));
            medHistLabel.setPadding(5);
            infoTable.addCell(medHistLabel);

            PdfPCell medHistVal = new PdfPCell(new Phrase(patient.getMedicalHistoryNotes() != null && !patient.getMedicalHistoryNotes().trim().isEmpty() ? patient.getMedicalHistoryNotes() : "No significant medical history or allergies recorded.", regularFont));
            medHistVal.setColspan(3);
            medHistVal.setPadding(5);
            infoTable.addCell(medHistVal);

            document.add(infoTable);
            document.add(new Paragraph(" "));

            // 3. Illnesses Table
            PdfPTable illTable = new PdfPTable(6);
            illTable.setWidthPercentage(100);
            illTable.setWidths(new float[]{25, 12, 15, 15, 15, 18});

            PdfPCell illHeader = new PdfPCell(new Phrase("DIAGNOSED ILLNESSES & MEDICAL CONDITIONS", sectionTitleFont));
            illHeader.setColspan(6);
            illHeader.setBackgroundColor(new Color(235, 243, 255));
            illHeader.setPadding(6);
            illTable.addCell(illHeader);

            String[] illHeaders = {"Illness Name", "ICD Code", "Status", "Severity", "Diagnosed Date", "Symptoms / Notes"};
            for (String h : illHeaders) {
                PdfPCell c = new PdfPCell(new Phrase(h, tableHeaderFont));
                c.setBackgroundColor(new Color(33, 150, 243));
                c.setPadding(5);
                illTable.addCell(c);
            }

            if (patient.getIllnesses() == null || patient.getIllnesses().isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No illness records recorded for this patient.", smallItalicFont));
                emptyCell.setColspan(6);
                emptyCell.setPadding(6);
                illTable.addCell(emptyCell);
            } else {
                for (Illness ill : patient.getIllnesses()) {
                    illTable.addCell(createBodyCell(ill.getName(), regularFont));
                    illTable.addCell(createBodyCell(ill.getIcdCode() != null ? ill.getIcdCode() : "-", regularFont));
                    illTable.addCell(createBodyCell(ill.getStatus().getDisplayName(), regularFont));
                    illTable.addCell(createBodyCell(ill.getSeverity().getDisplayName(), regularFont));
                    illTable.addCell(createBodyCell(ill.getDiagnosedDate() != null ? ill.getDiagnosedDate().format(DATE_FORMATTER) : "-", regularFont));
                    String notes = (ill.getSymptoms() != null ? ill.getSymptoms() : "") + (ill.getNotes() != null ? " " + ill.getNotes() : "");
                    illTable.addCell(createBodyCell(notes.isEmpty() ? "-" : notes, regularFont));
                }
            }

            document.add(illTable);
            document.add(new Paragraph(" "));

            // 4. Medications Table
            PdfPTable medTable = new PdfPTable(7);
            medTable.setWidthPercentage(100);
            medTable.setWidths(new float[]{22, 12, 15, 12, 15, 12, 12});

            PdfPCell medHeader = new PdfPCell(new Phrase("PRESCRIBED MEDICATIONS & TREATMENT PLAN", sectionTitleFont));
            medHeader.setColspan(7);
            medHeader.setBackgroundColor(new Color(235, 243, 255));
            medHeader.setPadding(6);
            medTable.addCell(medHeader);

            String[] medHeaders = {"Medication", "Dosage", "Frequency", "Status", "Duration", "Doctor", "Route"};
            for (String h : medHeaders) {
                PdfPCell c = new PdfPCell(new Phrase(h, tableHeaderFont));
                c.setBackgroundColor(new Color(46, 125, 50));
                c.setPadding(5);
                medTable.addCell(c);
            }

            if (patient.getMedications() == null || patient.getMedications().isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No medication records recorded for this patient.", smallItalicFont));
                emptyCell.setColspan(7);
                emptyCell.setPadding(6);
                medTable.addCell(emptyCell);
            } else {
                for (Medication med : patient.getMedications()) {
                    medTable.addCell(createBodyCell(med.getName(), regularFont));
                    medTable.addCell(createBodyCell(med.getDosage(), regularFont));
                    medTable.addCell(createBodyCell(med.getFrequency(), regularFont));
                    medTable.addCell(createBodyCell(med.getStatus().getDisplayName(), regularFont));
                    String duration = (med.getStartDate() != null ? med.getStartDate().format(DATE_FORMATTER) : "") + "\nto " + (med.getEndDate() != null ? med.getEndDate().format(DATE_FORMATTER) : "Ongoing");
                    medTable.addCell(createBodyCell(duration, regularFont));
                    medTable.addCell(createBodyCell(med.getPrescribedBy() != null ? med.getPrescribedBy() : "-", regularFont));
                    medTable.addCell(createBodyCell(med.getRoute() != null ? med.getRoute() : "Oral", regularFont));
                }
            }

            document.add(medTable);
            document.add(new Paragraph(" "));

            // 5. Physician Sign-off & Stamp Section
            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);
            signTable.setWidths(new float[]{50, 50});

            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.addElement(new Paragraph("Physician Remarks:", boldFont));
            leftCell.addElement(new Paragraph("Patient is being managed according to standard clinical protocols. Regular follow-up appointments recommended.", smallItalicFont));
            signTable.addCell(leftCell);

            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph signBlock = new Paragraph("\n\n___________________________________\nAttending Physician Signature / Stamp", boldFont);
            signBlock.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(signBlock);
            signTable.addCell(rightCell);

            document.add(signTable);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF report", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addInfoCell(PdfPTable table, String label, String value, Font labelFont, Font valFont) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, labelFont));
        lCell.setBackgroundColor(new Color(248, 249, 250));
        lCell.setPadding(4);
        table.addCell(lCell);

        PdfPCell vCell = new PdfPCell(new Phrase(value != null ? value : "-", valFont));
        vCell.setPadding(4);
        table.addCell(vCell);
    }

    private PdfPCell createBodyCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content != null ? content : "-", font));
        cell.setPadding(4);
        return cell;
    }
}

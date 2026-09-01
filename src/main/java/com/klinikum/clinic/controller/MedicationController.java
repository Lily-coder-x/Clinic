package com.klinikum.clinic.controller;

import com.klinikum.clinic.model.Medication;
import com.klinikum.clinic.model.MedicationStatus;
import com.klinikum.clinic.model.Patient;
import com.klinikum.clinic.service.MedicationService;
import com.klinikum.clinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients/{patientId}/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;
    private final PatientService patientService;

    @GetMapping("/new")
    public String showAddForm(@PathVariable("patientId") Long patientId, Model model) {
        Patient patient = patientService.getPatientOrThrow(patientId);
        model.addAttribute("patient", patient);
        model.addAttribute("medication", new Medication());
        model.addAttribute("statuses", MedicationStatus.values());
        return "medications/form";
    }

    @PostMapping
    public String addMedication(@PathVariable("patientId") Long patientId,
                                @Valid @ModelAttribute("medication") Medication medication,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            Patient patient = patientService.getPatientOrThrow(patientId);
            model.addAttribute("patient", patient);
            model.addAttribute("statuses", MedicationStatus.values());
            return "medications/form";
        }

        medicationService.saveMedicationForPatient(patientId, medication);
        redirectAttributes.addFlashAttribute("successMessage", "Prescription for '" + medication.getName() + "' added successfully!");
        return "redirect:/patients/" + patientId;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("patientId") Long patientId,
                               @PathVariable("id") Long id,
                               Model model) {
        Patient patient = patientService.getPatientOrThrow(patientId);
        Medication medication = medicationService.getMedicationOrThrow(id);
        model.addAttribute("patient", patient);
        model.addAttribute("medication", medication);
        model.addAttribute("statuses", MedicationStatus.values());
        return "medications/form";
    }

    @PostMapping("/{id}/edit")
    public String updateMedication(@PathVariable("patientId") Long patientId,
                                   @PathVariable("id") Long id,
                                   @Valid @ModelAttribute("medication") Medication medication,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            Patient patient = patientService.getPatientOrThrow(patientId);
            model.addAttribute("patient", patient);
            model.addAttribute("statuses", MedicationStatus.values());
            return "medications/form";
        }

        medicationService.updateMedication(id, medication);
        redirectAttributes.addFlashAttribute("successMessage", "Medication record updated successfully!");
        return "redirect:/patients/" + patientId;
    }

    @PostMapping("/{id}/delete")
    public String deleteMedication(@PathVariable("patientId") Long patientId,
                                   @PathVariable("id") Long id,
                                   RedirectAttributes redirectAttributes) {
        medicationService.deleteMedication(id);
        redirectAttributes.addFlashAttribute("successMessage", "Medication record removed.");
        return "redirect:/patients/" + patientId;
    }
}

package com.klinikum.clinic.controller;

import com.klinikum.clinic.model.Illness;
import com.klinikum.clinic.model.IllnessSeverity;
import com.klinikum.clinic.model.IllnessStatus;
import com.klinikum.clinic.model.Patient;
import com.klinikum.clinic.service.IllnessService;
import com.klinikum.clinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients/{patientId}/illnesses")
@RequiredArgsConstructor
public class IllnessController {

    private final IllnessService illnessService;
    private final PatientService patientService;

    @GetMapping("/new")
    public String showAddForm(@PathVariable("patientId") Long patientId, Model model) {
        Patient patient = patientService.getPatientOrThrow(patientId);
        model.addAttribute("patient", patient);
        model.addAttribute("illness", new Illness());
        model.addAttribute("statuses", IllnessStatus.values());
        model.addAttribute("severities", IllnessSeverity.values());
        return "illnesses/form";
    }

    @PostMapping
    public String addIllness(@PathVariable("patientId") Long patientId,
                             @Valid @ModelAttribute("illness") Illness illness,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            Patient patient = patientService.getPatientOrThrow(patientId);
            model.addAttribute("patient", patient);
            model.addAttribute("statuses", IllnessStatus.values());
            model.addAttribute("severities", IllnessSeverity.values());
            return "illnesses/form";
        }

        illnessService.saveIllnessForPatient(patientId, illness);
        redirectAttributes.addFlashAttribute("successMessage", "Diagnosis for '" + illness.getName() + "' recorded successfully!");
        return "redirect:/patients/" + patientId;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("patientId") Long patientId,
                               @PathVariable("id") Long id,
                               Model model) {
        Patient patient = patientService.getPatientOrThrow(patientId);
        Illness illness = illnessService.getIllnessOrThrow(id);
        model.addAttribute("patient", patient);
        model.addAttribute("illness", illness);
        model.addAttribute("statuses", IllnessStatus.values());
        model.addAttribute("severities", IllnessSeverity.values());
        return "illnesses/form";
    }

    @PostMapping("/{id}/edit")
    public String updateIllness(@PathVariable("patientId") Long patientId,
                                @PathVariable("id") Long id,
                                @Valid @ModelAttribute("illness") Illness illness,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            Patient patient = patientService.getPatientOrThrow(patientId);
            model.addAttribute("patient", patient);
            model.addAttribute("statuses", IllnessStatus.values());
            model.addAttribute("severities", IllnessSeverity.values());
            return "illnesses/form";
        }

        illnessService.updateIllness(id, illness);
        redirectAttributes.addFlashAttribute("successMessage", "Diagnosis record updated successfully!");
        return "redirect:/patients/" + patientId;
    }

    @PostMapping("/{id}/delete")
    public String deleteIllness(@PathVariable("patientId") Long patientId,
                                @PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        illnessService.deleteIllness(id);
        redirectAttributes.addFlashAttribute("successMessage", "Diagnosis record removed.");
        return "redirect:/patients/" + patientId;
    }
}

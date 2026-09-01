package com.klinikum.clinic.controller;

import com.klinikum.clinic.dto.ClinicStatsDto;
import com.klinikum.clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final PatientService patientService;

    @GetMapping("/")
    public String dashboard(Model model) {
        ClinicStatsDto stats = patientService.getClinicStatistics();
        model.addAttribute("stats", stats);
        return "dashboard";
    }
}

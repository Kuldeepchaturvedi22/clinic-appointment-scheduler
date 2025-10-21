package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.dto.PatientProfileResponse;
import com.example.clinic_appointment_schedulerm.dto.UpdateProfileRequest;
import com.example.clinic_appointment_schedulerm.entity.Appointment;
import com.example.clinic_appointment_schedulerm.entity.Patient;
import com.example.clinic_appointment_schedulerm.service.AppointmentService;
import com.example.clinic_appointment_schedulerm.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService service;
    private final AppointmentService appointmentService;

    @GetMapping
    public List<Patient> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Patient get(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/me")
    public PatientProfileResponse me(Principal principal) {
        return service.findProfileByUserEmail(principal.getName());
    }

    @PutMapping("/me")
    public PatientProfileResponse updateProfile(Principal principal, @Valid @RequestBody UpdateProfileRequest request) {
        return service.updateProfile(principal.getName(), request);
    }

    @GetMapping("/me/appointments/history")
    public List<AppointmentService.AppointmentDto> getAppointmentHistory(Principal principal) {
        return appointmentService.getPatientAppointmentHistoryDto(principal.getName());
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    public Patient create(@Valid @RequestBody Patient p) {
        return service.save(p);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','PATIENT')")
    @PutMapping("/{id}")
    public Patient update(@PathVariable Long id, @Valid @RequestBody Patient p) {
        p.setId(id);
        return service.save(p);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

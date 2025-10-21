package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.dto.AppointmentRequest;
import com.example.clinic_appointment_schedulerm.entity.Appointment;
import com.example.clinic_appointment_schedulerm.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService service;

    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> listByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        return service.listByDoctor(doctorId, from, to);
    }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> listByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        return service.listByPatient(patientId, from, to);
    }

    @GetMapping("/{id}")
    public Appointment get(@PathVariable Long id) { return service.get(id); }

    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    @PostMapping("/book")
    public Appointment book(@Valid @RequestBody AppointmentRequest req) {
        return service.book(req.getPatientId(), req.getDoctorId(), req.getStartTime(), req.getEndTime(), req.getNotes());
    }

    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    @PostMapping("/{id}/cancel")
    public Appointment cancel(@PathVariable Long id) {
        return service.cancel(id);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.dto.DoctorDashboardResponse;
import com.example.clinic_appointment_schedulerm.dto.DoctorListResponse;
import com.example.clinic_appointment_schedulerm.dto.DoctorProfileResponse;
import com.example.clinic_appointment_schedulerm.dto.UpdateProfileRequest;
import com.example.clinic_appointment_schedulerm.entity.Doctor;
import com.example.clinic_appointment_schedulerm.service.AppointmentService;
import com.example.clinic_appointment_schedulerm.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService service;
    private final AppointmentService appointmentService;

    @GetMapping
    public List<DoctorListResponse> list(@RequestParam(required = false) String search) { 
        return service.findAllForList(search); 
    }

    @GetMapping("/{id}")
    public Doctor get(@PathVariable Long id) { return service.findById(id); }

    @GetMapping("/{id}/available-slots")
    public List<AppointmentService.TimeSlot> getAvailableSlots(@PathVariable Long id) {
        return appointmentService.getAvailableSlots(id);
    }

    @GetMapping("/me")
    public DoctorProfileResponse me(Principal principal) {
        return service.findProfileByUserEmail(principal.getName());
    }

    @GetMapping("/me/dashboard")
    public DoctorDashboardResponse dashboard(Principal principal) {
        return service.getDashboard(principal.getName());
    }

    @PutMapping("/me")
    public DoctorProfileResponse updateProfile(Principal principal, @Valid @RequestBody UpdateProfileRequest request) {
        return service.updateProfileAndReturn(principal.getName(), request);
    }

    @GetMapping("/me/appointments/today")
    public List<AppointmentService.AppointmentDto> getTodaysAppointments(Principal principal) {
        return appointmentService.getTodaysAppointmentsByDoctorDto(principal.getName());
    }

    @GetMapping("/me/appointments/pending")
    public List<AppointmentService.AppointmentDto> getPendingAppointments(Principal principal) {
        return appointmentService.getPendingAppointmentsByDoctorDto(principal.getName());
    }

    @GetMapping("/me/appointments/history")
    public List<AppointmentService.AppointmentDto> getAppointmentHistory(Principal principal) {
        return appointmentService.getCompletedAppointmentsByDoctorDto(principal.getName());
    }

    @GetMapping("/me/appointments/all")
    public List<AppointmentService.AppointmentDto> getAllAppointmentHistory(Principal principal) {
        return appointmentService.getAllAppointmentsByDoctorDto(principal.getName());
    }

    @PutMapping("/appointments/{id}/accept")
    public AppointmentService.AppointmentDto acceptAppointment(@PathVariable Long id, Principal principal) {
        var appointment = appointmentService.acceptAppointment(id, principal.getName());
        return appointmentService.toDto(appointment);
    }

    @PutMapping("/appointments/{id}/reject")
    public AppointmentService.AppointmentDto rejectAppointment(@PathVariable Long id, Principal principal) {
        var appointment = appointmentService.rejectAppointment(id, principal.getName());
        return appointmentService.toDto(appointment);
    }

    @PutMapping("/appointments/{id}/complete")
    public AppointmentService.AppointmentDto completeAppointment(@PathVariable Long id, Principal principal) {
        var appointment = appointmentService.completeAppointment(id, principal.getName());
        return appointmentService.toDto(appointment);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    public Doctor create(@Valid @RequestBody Doctor d) { return service.save(d); }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{id}")
    public Doctor update(@PathVariable Long id, @Valid @RequestBody Doctor d) {
        d.setId(id);
        return service.save(d);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
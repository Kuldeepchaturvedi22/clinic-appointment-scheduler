package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.entity.*;
import com.example.clinic_appointment_schedulerm.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    // Helper to get today's window in server offset
    public java.time.OffsetDateTime[] getTodayWindow() {
        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
        java.time.LocalDate today = now.toLocalDate();
        java.time.OffsetDateTime startOfDay = today.atStartOfDay().atOffset(now.getOffset());
        java.time.OffsetDateTime endOfDay = today.plusDays(1).atStartOfDay().atOffset(now.getOffset());
        return new java.time.OffsetDateTime[]{ startOfDay, endOfDay };
    }

    // Projection DTO to avoid lazy proxy serialization
    public record AppointmentDto(
            Long id,
            java.time.OffsetDateTime startTime,
            java.time.OffsetDateTime endTime,
            String status,
            Long doctorId,
            String doctorName,
            Long patientId,
            String patientName,
            String notes
    ) {}

    public record TimeSlot(
            String date,
            String time,
            java.time.OffsetDateTime startTime,
            java.time.OffsetDateTime endTime,
            boolean available
    ) {}

    public AppointmentDto toDto(com.example.clinic_appointment_schedulerm.entity.Appointment a) {
        var doctor = a.getDoctor();
        var patient = a.getPatient();
        // Ensure user is initialized; repository queries use join fetch, but fallback to load names safely
        String doctorName = null;
        try { doctorName = (doctor != null && doctor.getUser() != null) ? doctor.getUser().getFullName() : null; } catch (Exception ignored) {}
        String patientName = null;
        try { patientName = (patient != null && patient.getUser() != null) ? patient.getUser().getFullName() : null; } catch (Exception ignored) {}
        return new AppointmentDto(
                a.getId(),
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus().name(),
                doctor != null ? doctor.getId() : null,
                doctorName,
                patient != null ? patient.getId() : null,
                patientName,
                a.getNotes()
        );
    }

    public java.util.List<Appointment> listByDoctor(Long doctorId, java.time.OffsetDateTime from, java.time.OffsetDateTime to) {
        if (from != null && to != null) return appointmentRepository.findByDoctor_IdAndStartTimeBetween(doctorId, from, to);
        return appointmentRepository.findByDoctor_Id(doctorId);
    }
    public java.util.List<Appointment> listByPatient(Long patientId, java.time.OffsetDateTime from, java.time.OffsetDateTime to) {
        if (from != null && to != null) return appointmentRepository.findByPatient_IdAndStartTimeBetween(patientId, from, to);
        return appointmentRepository.findByPatient_Id(patientId);
    }

    public com.example.clinic_appointment_schedulerm.entity.Appointment get(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new java.util.NoSuchElementException("Appointment not found"));
    }

    public java.util.List<AppointmentDto> getTodaysAppointmentsByDoctorDto(String doctorEmail) {
        com.example.clinic_appointment_schedulerm.entity.Doctor doctor = doctorRepository.findByUser_Email(doctorEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        log.info("Getting all scheduled appointments for doctor {}", doctor.getId());
        var appointments = appointmentRepository.findWithDoctorAndPatientAndUsersByDoctorAndStatus(
                doctor.getId(), com.example.clinic_appointment_schedulerm.entity.Appointment.Status.SCHEDULED);
        log.info("Found {} scheduled appointments", appointments.size());
        return appointments.stream().map(this::toDto).toList();
    }

    public java.util.List<AppointmentDto> getPendingAppointmentsByDoctorDto(String doctorEmail) {
        com.example.clinic_appointment_schedulerm.entity.Doctor doctor = doctorRepository.findByUser_Email(doctorEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        log.info("Getting pending appointments for doctor {}", doctor.getId());
        var appointments = appointmentRepository.findWithDoctorAndPatientAndUsersByDoctorAndStatus(
                doctor.getId(), com.example.clinic_appointment_schedulerm.entity.Appointment.Status.PENDING);
        log.info("Found {} pending appointments", appointments.size());
        return appointments.stream().map(this::toDto).toList();
    }

    public java.util.List<AppointmentDto> getCompletedAppointmentsByDoctorDto(String doctorEmail) {
        com.example.clinic_appointment_schedulerm.entity.Doctor doctor = doctorRepository.findByUser_Email(doctorEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        var appointments = appointmentRepository.findWithDoctorAndPatientAndUsersByDoctorAndStatus(
                doctor.getId(), com.example.clinic_appointment_schedulerm.entity.Appointment.Status.COMPLETED);
        return appointments.stream().map(this::toDto).toList();
    }

    @jakarta.transaction.Transactional
    public com.example.clinic_appointment_schedulerm.entity.Appointment book(Long patientId, Long doctorId, java.time.OffsetDateTime start, java.time.OffsetDateTime end, String notes) {
        com.example.clinic_appointment_schedulerm.entity.Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Patient not found"));
        com.example.clinic_appointment_schedulerm.entity.Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        if (start == null || end == null) throw new IllegalArgumentException("Start and end time are required");
        if (!end.isAfter(start)) throw new IllegalArgumentException("End time must be after start time");

        boolean doctorOverlap = appointmentRepository.existsByDoctorAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(doctor, end, start);
        boolean patientOverlap = appointmentRepository.existsByPatientAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(patient, end, start);
        if (doctorOverlap) throw new IllegalStateException("Doctor has overlapping appointment");
        if (patientOverlap) throw new IllegalStateException("Patient has overlapping appointment");

        var appt = com.example.clinic_appointment_schedulerm.entity.Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .startTime(start)
                .endTime(end)
                .notes(notes)
                .status(com.example.clinic_appointment_schedulerm.entity.Appointment.Status.PENDING)
                .build();

        try {
            appt = appointmentRepository.save(appt);
            log.info("Booked appointment id={} doctorId={} patientId={}", appt.getId(), doctorId, patientId);
            return appt;
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new org.springframework.dao.DataIntegrityViolationException("Could not create appointment. Ensure status is allowed by DB constraint and times are valid.", ex);
        }
    }

    @jakarta.transaction.Transactional
    public com.example.clinic_appointment_schedulerm.entity.Appointment acceptAppointment(Long appointmentId, String doctorEmail) {
        var appointment = get(appointmentId);
        var doctor = doctorRepository.findByUser_Email(doctorEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        if (!appointment.getDoctor().getId().equals(doctor.getId())) throw new IllegalStateException("Appointment does not belong to this doctor");
        appointment.setStatus(com.example.clinic_appointment_schedulerm.entity.Appointment.Status.SCHEDULED);
        return appointmentRepository.save(appointment);
    }

    @jakarta.transaction.Transactional
    public com.example.clinic_appointment_schedulerm.entity.Appointment rejectAppointment(Long appointmentId, String doctorEmail) {
        var appointment = get(appointmentId);
        var doctor = doctorRepository.findByUser_Email(doctorEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        if (!appointment.getDoctor().getId().equals(doctor.getId())) throw new IllegalStateException("Appointment does not belong to this doctor");
        appointment.setStatus(com.example.clinic_appointment_schedulerm.entity.Appointment.Status.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    @jakarta.transaction.Transactional
    public com.example.clinic_appointment_schedulerm.entity.Appointment completeAppointment(Long appointmentId, String doctorEmail) {
        var appointment = get(appointmentId);
        var doctor = doctorRepository.findByUser_Email(doctorEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        if (!appointment.getDoctor().getId().equals(doctor.getId())) throw new IllegalStateException("Appointment does not belong to this doctor");
        appointment.setStatus(com.example.clinic_appointment_schedulerm.entity.Appointment.Status.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    public com.example.clinic_appointment_schedulerm.entity.Appointment cancel(Long id) {
        var appt = get(id);
        appt.setStatus(com.example.clinic_appointment_schedulerm.entity.Appointment.Status.CANCELLED);
        return appointmentRepository.save(appt);
    }

    public com.example.clinic_appointment_schedulerm.entity.Appointment update(com.example.clinic_appointment_schedulerm.entity.Appointment a) { return appointmentRepository.save(a); }

    public void delete(Long id) { appointmentRepository.deleteById(id); }

    public java.util.List<com.example.clinic_appointment_schedulerm.entity.Appointment> getTodaysAppointmentsByDoctor(String doctorEmail) {
        com.example.clinic_appointment_schedulerm.entity.Doctor doctor = doctorRepository.findByUser_Email(doctorEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.OffsetDateTime startOfDay = today.atStartOfDay().atOffset(java.time.OffsetDateTime.now().getOffset());
        java.time.OffsetDateTime endOfDay = today.plusDays(1).atStartOfDay().atOffset(java.time.OffsetDateTime.now().getOffset());
        log.info("Getting today's scheduled appointments for doctor {} between {} and {}", doctor.getId(), startOfDay, endOfDay);
        var appointments = appointmentRepository.findWithDoctorAndPatientAndUsersByDoctorAndStartBetweenAndStatus(
                doctor.getId(), startOfDay, endOfDay, com.example.clinic_appointment_schedulerm.entity.Appointment.Status.SCHEDULED);
        log.info("Found {} scheduled appointments for today", appointments.size());
        return appointments;
    }

    public java.util.List<com.example.clinic_appointment_schedulerm.entity.Appointment> getCompletedAppointmentsByDoctor(String doctorEmail) {
        var doctor = doctorRepository.findByUser_Email(doctorEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        return appointmentRepository.findWithDoctorAndPatientAndUsersByDoctorAndStatus(doctor.getId(), com.example.clinic_appointment_schedulerm.entity.Appointment.Status.COMPLETED);
    }

    public java.util.List<com.example.clinic_appointment_schedulerm.entity.Appointment> getPendingAppointmentsByDoctor(String doctorEmail) {
        var doctor = doctorRepository.findByUser_Email(doctorEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        log.info("Getting pending appointments for doctor {}", doctor.getId());
        var appointments = appointmentRepository.findWithDoctorAndPatientAndUsersByDoctorAndStatus(doctor.getId(), com.example.clinic_appointment_schedulerm.entity.Appointment.Status.PENDING);
        log.info("Found {} pending appointments", appointments.size());
        return appointments;
    }

    public java.util.List<com.example.clinic_appointment_schedulerm.entity.Appointment> getScheduledAppointmentsByDoctor(String doctorEmail) {
        var doctor = doctorRepository.findByUser_Email(doctorEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        log.info("Getting scheduled appointments for doctor {}", doctor.getId());
        var appointments = appointmentRepository.findWithDoctorAndPatientAndUsersByDoctorAndStatus(doctor.getId(), com.example.clinic_appointment_schedulerm.entity.Appointment.Status.SCHEDULED);
        log.info("Found {} scheduled appointments", appointments.size());
        return appointments;
    }

    public java.util.List<AppointmentDto> getAllAppointmentsByDoctorDto(String doctorEmail) {
        com.example.clinic_appointment_schedulerm.entity.Doctor doctor = doctorRepository.findByUser_Email(doctorEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        var appointments = appointmentRepository.findAllWithDoctorAndPatientAndUsersByDoctor(doctor.getId());
        return appointments.stream().map(this::toDto).toList();
    }

    public java.util.List<AppointmentDto> getPatientAppointmentHistoryDto(String patientEmail) {
        var patient = patientRepository.findByUser_Email(patientEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Patient not found"));
        var appointments = appointmentRepository.findAllWithDoctorAndPatientAndUsersByPatient(patient.getId());
        return appointments.stream().map(this::toDto).toList();
    }

    public java.util.List<com.example.clinic_appointment_schedulerm.entity.Appointment> getPatientAppointmentHistory(String patientEmail) {
        var patient = patientRepository.findByUser_Email(patientEmail)
                .orElseThrow(() -> new java.util.NoSuchElementException("Patient not found"));
        return appointmentRepository.findByPatient_Id(patient.getId());
    }

    public java.util.List<TimeSlot> getAvailableSlots(Long doctorId) {
        var doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Doctor not found"));
        
        java.util.List<TimeSlot> slots = new java.util.ArrayList<>();
        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
        
        // Generate slots for today and tomorrow
        for (int day = 0; day < 2; day++) {
            java.time.LocalDate date = now.toLocalDate().plusDays(day);
            String dayLabel = day == 0 ? "Today" : "Tomorrow";
            
            // Generate 2-hour slots from 9 AM to 8 PM
            int[] startHours = {9, 11, 13, 15, 17, 19};
            int[] endHours = {11, 13, 15, 17, 19, 20};
            
            for (int i = 0; i < startHours.length; i++) {
                java.time.OffsetDateTime slotStart = date.atTime(startHours[i], 0).atOffset(now.getOffset());
                java.time.OffsetDateTime slotEnd = date.atTime(endHours[i], 0).atOffset(now.getOffset());
                
                // Skip past slots only for today
                if (day == 0 && slotEnd.isBefore(now)) continue;
                
                // Check if slot is booked
                boolean isBooked = appointmentRepository.existsByDoctorAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        doctor, slotEnd, slotStart);
                
                slots.add(new TimeSlot(
                        dayLabel + " (" + date.toString() + ")",
                        String.format("%02d:00 - %02d:00", startHours[i], endHours[i]),
                        slotStart,
                        slotEnd,
                        !isBooked
                ));
            }
        }
        
        log.info("Generated {} slots for doctor {}", slots.size(), doctorId);
        return slots;
    }
}

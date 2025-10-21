package com.example.clinic_appointment_schedulerm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    private Appointment appointment;
    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        UserAccount doctorUser = UserAccount.builder()
                .id(1L)
                .email("doctor@example.com")
                .fullName("Dr. Smith")
                .role(UserAccount.UserRole.DOCTOR)
                .build();

        UserAccount patientUser = UserAccount.builder()
                .id(2L)
                .email("patient@example.com")
                .fullName("John Doe")
                .role(UserAccount.UserRole.PATIENT)
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .user(doctorUser)
                .specialization("Cardiology")
                .build();

        patient = Patient.builder()
                .id(1L)
                .user(patientUser)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("Male")
                .build();

        appointment = new Appointment();
    }

    @Test
    void testAppointmentCreation() {
        OffsetDateTime startTime = OffsetDateTime.now();
        OffsetDateTime endTime = startTime.plusHours(2);

        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setStatus(Appointment.Status.PENDING);
        appointment.setNotes("Test appointment");

        assertEquals(1L, appointment.getId());
        assertEquals(doctor, appointment.getDoctor());
        assertEquals(patient, appointment.getPatient());
        assertEquals(startTime, appointment.getStartTime());
        assertEquals(endTime, appointment.getEndTime());
        assertEquals(Appointment.Status.PENDING, appointment.getStatus());
        assertEquals("Test appointment", appointment.getNotes());
    }

    @Test
    void testStatusEnum() {
        assertEquals("PENDING", Appointment.Status.PENDING.name());
        assertEquals("SCHEDULED", Appointment.Status.SCHEDULED.name());
        assertEquals("COMPLETED", Appointment.Status.COMPLETED.name());
        assertEquals("CANCELLED", Appointment.Status.CANCELLED.name());
    }

    @Test
    void testAppointmentRelationships() {
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        assertNotNull(appointment.getDoctor());
        assertNotNull(appointment.getPatient());
        assertEquals("Dr. Smith", appointment.getDoctor().getUser().getFullName());
        assertEquals("John Doe", appointment.getPatient().getUser().getFullName());
    }


}
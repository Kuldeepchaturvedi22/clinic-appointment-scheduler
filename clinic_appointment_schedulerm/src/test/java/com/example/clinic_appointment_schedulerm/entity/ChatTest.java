package com.example.clinic_appointment_schedulerm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {

    private Chat chat;
    private Appointment appointment;

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

        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(doctorUser)
                .specialization("Cardiology")
                .build();

        Patient patient = Patient.builder()
                .id(1L)
                .user(patientUser)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("Male")
                .build();

        appointment = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(patient)
                .status(Appointment.Status.SCHEDULED)
                .build();

        chat = new Chat();
    }

    @Test
    void testChatCreation() {
        OffsetDateTime sentAt = OffsetDateTime.now();

        chat.setId(1L);
        chat.setAppointment(appointment);
        chat.setMessage("Hello Doctor");
        chat.setSenderType("PATIENT");
        chat.setSentAt(sentAt);

        assertEquals(1L, chat.getId());
        assertEquals(appointment, chat.getAppointment());
        assertEquals("Hello Doctor", chat.getMessage());
        assertEquals("PATIENT", chat.getSenderType());
        assertEquals(sentAt, chat.getSentAt());
    }

    @Test
    void testSenderTypeValues() {
        chat.setSenderType("PATIENT");
        assertEquals("PATIENT", chat.getSenderType());
        
        chat.setSenderType("DOCTOR");
        assertEquals("DOCTOR", chat.getSenderType());
    }

    @Test
    void testChatAppointmentRelationship() {
        chat.setAppointment(appointment);

        assertNotNull(chat.getAppointment());
        assertEquals(1L, chat.getAppointment().getId());
        assertEquals("Dr. Smith", chat.getAppointment().getDoctor().getUser().getFullName());
        assertEquals("John Doe", chat.getAppointment().getPatient().getUser().getFullName());
    }

    @Test
    void testEqualsAndHashCode() {
        chat.setId(1L);
        
        Chat chat2 = new Chat();
        chat2.setId(1L);

        Chat chat3 = new Chat();
        chat3.setId(2L);

        assertEquals(chat, chat2);
        assertNotEquals(chat, chat3);
        assertEquals(chat.hashCode(), chat2.hashCode());
    }

    @Test
    void testToString() {
        chat.setId(1L);
        chat.setMessage("Hello Doctor");
        chat.setSenderType("PATIENT");

        String toString = chat.toString();
        assertTrue(toString.contains("Hello Doctor"));
        assertTrue(toString.contains("PATIENT"));
    }
}
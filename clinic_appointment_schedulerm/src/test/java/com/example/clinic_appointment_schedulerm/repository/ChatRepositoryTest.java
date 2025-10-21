package com.example.clinic_appointment_schedulerm.repository;

import com.example.clinic_appointment_schedulerm.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ChatRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatRepository chatRepository;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        UserAccount doctorUser = UserAccount.builder()
                .email("doctor@example.com")
                .password("password")
                .fullName("Dr. Smith")
                .phone("1234567890")
                .role(UserAccount.UserRole.DOCTOR)
                .build();
        entityManager.persist(doctorUser);

        UserAccount patientUser = UserAccount.builder()
                .email("patient@example.com")
                .password("password")
                .fullName("John Doe")
                .phone("9876543210")
                .role(UserAccount.UserRole.PATIENT)
                .build();
        entityManager.persist(patientUser);

        Doctor doctor = Doctor.builder()
                .user(doctorUser)
                .specialization("Cardiology")
                .build();
        entityManager.persist(doctor);

        Patient patient = Patient.builder()
                .user(patientUser)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("Male")
                .build();
        entityManager.persist(patient);

        appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plusHours(2))
                .status(Appointment.Status.SCHEDULED)
                .build();
        entityManager.persist(appointment);

        entityManager.flush();
    }

    @Test
    void testFindByAppointmentIdOrderBySentAt() {
        Chat chat1 = Chat.builder()
                .appointment(appointment)
                .message("Hello Doctor")
                .senderType("PATIENT")
                .sentAt(OffsetDateTime.now().minusMinutes(10))
                .build();
        entityManager.persist(chat1);

        Chat chat2 = Chat.builder()
                .appointment(appointment)
                .message("Hello Patient")
                .senderType("DOCTOR")
                .sentAt(OffsetDateTime.now().minusMinutes(5))
                .build();
        entityManager.persist(chat2);

        Chat chat3 = Chat.builder()
                .appointment(appointment)
                .message("How are you?")
                .senderType("PATIENT")
                .sentAt(OffsetDateTime.now())
                .build();
        entityManager.persistAndFlush(chat3);

        List<Chat> chats = chatRepository.findByAppointment_IdOrderBySentAt(appointment.getId());

        assertNotNull(chats);
        assertEquals(3, chats.size());
        assertEquals("Hello Doctor", chats.get(0).getMessage());
        assertEquals("Hello Patient", chats.get(1).getMessage());
        assertEquals("How are you?", chats.get(2).getMessage());
    }

    @Test
    void testFindByAppointmentIdOrderBySentAt_EmptyResult() {
        List<Chat> chats = chatRepository.findByAppointment_IdOrderBySentAt(999L);

        assertNotNull(chats);
        assertTrue(chats.isEmpty());
    }
}
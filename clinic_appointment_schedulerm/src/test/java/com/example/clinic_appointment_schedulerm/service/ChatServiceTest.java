package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.entity.Appointment;
import com.example.clinic_appointment_schedulerm.entity.Chat;
import com.example.clinic_appointment_schedulerm.entity.Doctor;
import com.example.clinic_appointment_schedulerm.entity.Patient;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import com.example.clinic_appointment_schedulerm.repository.AppointmentRepository;
import com.example.clinic_appointment_schedulerm.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private ChatService chatService;

    private Appointment appointment;
    private Patient patient;
    private Doctor doctor;
    private UserAccount patientUser;
    private UserAccount doctorUser;

    @BeforeEach
    void setUp() {
        patientUser = UserAccount.builder()
                .email("patient@example.com")
                .fullName("John Doe")
                .role(UserAccount.UserRole.PATIENT)
                .build();

        doctorUser = UserAccount.builder()
                .email("doctor@example.com")
                .fullName("Dr. Smith")
                .role(UserAccount.UserRole.DOCTOR)
                .build();

        patient = Patient.builder()
                .id(1L)
                .user(patientUser)
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .user(doctorUser)
                .build();

        appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .status(Appointment.Status.SCHEDULED)
                .build();
    }

    @Test
    void testGetMessages_Success() {
        Chat chat = Chat.builder()
                .id(1L)
                .appointment(appointment)
                .message("Hello")
                .senderType("PATIENT")
                .sentAt(OffsetDateTime.now())
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(chatRepository.findByAppointment_IdOrderBySentAt(1L)).thenReturn(Arrays.asList(chat));

        List<ChatService.ChatDto> result = chatService.getMessages(1L, "patient@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hello", result.get(0).message());
        assertEquals("PATIENT", result.get(0).senderType());
    }

    @Test
    void testGetMessages_UnauthorizedAccess() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThrows(RuntimeException.class, () -> chatService.getMessages(1L, "unauthorized@example.com"));
    }

    @Test
    void testSendMessage_PatientSender() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
            Chat savedChat = invocation.getArgument(0);
            return Chat.builder()
                    .id(1L)
                    .appointment(savedChat.getAppointment())
                    .message(savedChat.getMessage())
                    .senderType(savedChat.getSenderType())
                    .sentAt(OffsetDateTime.now())
                    .build();
        });

        ChatService.ChatDto result = chatService.sendMessage(1L, "Hello Doctor", "patient@example.com");

        assertNotNull(result);
        assertEquals("Hello Doctor", result.message());
        assertEquals("PATIENT", result.senderType());
        assertEquals("John Doe", result.senderName());
        verify(chatRepository).save(any(Chat.class));
    }

    @Test
    void testSendMessage_DoctorSender() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
            Chat savedChat = invocation.getArgument(0);
            return Chat.builder()
                    .id(1L)
                    .appointment(savedChat.getAppointment())
                    .message(savedChat.getMessage())
                    .senderType(savedChat.getSenderType())
                    .sentAt(OffsetDateTime.now())
                    .build();
        });

        ChatService.ChatDto result = chatService.sendMessage(1L, "Hello Patient", "doctor@example.com");

        assertNotNull(result);
        assertEquals("Hello Patient", result.message());
        assertEquals("DOCTOR", result.senderType());
        assertEquals("Dr. Smith", result.senderName());
        verify(chatRepository).save(any(Chat.class));
    }

    @Test
    void testSendMessage_AppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chatService.sendMessage(1L, "Hello", "patient@example.com"));
    }
}
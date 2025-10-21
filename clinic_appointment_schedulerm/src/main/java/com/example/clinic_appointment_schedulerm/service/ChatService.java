package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.entity.*;
import com.example.clinic_appointment_schedulerm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public record ChatDto(
            Long id,
            String senderType,
            String senderName,
            String message,
            OffsetDateTime sentAt
    ) {}

    @Transactional(readOnly = true)
    public List<ChatDto> getMessages(Long appointmentId, String userEmail) {
        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        // Verify user has access to this appointment
        verifyAccess(appointment, userEmail);
        
        return chatRepository.findByAppointment_IdOrderBySentAt(appointmentId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ChatDto sendMessage(Long appointmentId, String message, String userEmail) {
        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        // Only allow chat for scheduled appointments
        if (appointment.getStatus() != Appointment.Status.SCHEDULED) {
            throw new RuntimeException("Chat is only available for scheduled appointments");
        }
        
        // Verify user has access and determine sender type
        String senderType = getSenderType(appointment, userEmail);
        
        var chat = Chat.builder()
                .appointment(appointment)
                .senderType(senderType)
                .message(message)
                .build();
        
        chat = chatRepository.save(chat);
        return toDto(chat);
    }

    private void verifyAccess(Appointment appointment, String userEmail) {
        boolean hasAccess = appointment.getDoctor().getUser().getEmail().equals(userEmail) ||
                           appointment.getPatient().getUser().getEmail().equals(userEmail);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
    }

    private String getSenderType(Appointment appointment, String userEmail) {
        if (appointment.getDoctor().getUser().getEmail().equals(userEmail)) {
            return "DOCTOR";
        } else if (appointment.getPatient().getUser().getEmail().equals(userEmail)) {
            return "PATIENT";
        } else {
            throw new RuntimeException("Access denied");
        }
    }

    private ChatDto toDto(Chat chat) {
        String senderName = chat.getSenderType().equals("DOCTOR") 
            ? chat.getAppointment().getDoctor().getUser().getFullName()
            : chat.getAppointment().getPatient().getUser().getFullName();
            
        return new ChatDto(
                chat.getId(),
                chat.getSenderType(),
                senderName,
                chat.getMessage(),
                chat.getSentAt()
        );
    }
}
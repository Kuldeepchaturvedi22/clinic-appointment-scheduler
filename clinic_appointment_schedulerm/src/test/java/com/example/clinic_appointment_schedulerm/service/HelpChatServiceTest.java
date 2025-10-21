package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.entity.HelpChat;
import com.example.clinic_appointment_schedulerm.repository.HelpChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelpChatServiceTest {

    @Mock
    private HelpChatRepository helpChatRepository;

    @InjectMocks
    private HelpChatService helpChatService;

    private HelpChat patientMessage;
    private HelpChat adminMessage;

    @BeforeEach
    void setUp() {
        patientMessage = HelpChat.builder()
                .id(1L)
                .senderEmail("patient@test.com")
                .senderType("PATIENT")
                .message("Need help")
                .sentAt(OffsetDateTime.now())
                .build();

        adminMessage = HelpChat.builder()
                .id(2L)
                .senderEmail("admin@gmail.com")
                .senderType("ADMIN")
                .message("How can I help?")
                .recipientEmail("patient@test.com")
                .sentAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void getMessages_ForAdmin_ShouldReturnAllMessages() {
        List<HelpChat> messages = Arrays.asList(patientMessage, adminMessage);
        when(helpChatRepository.findAllByOrderBySentAtAsc()).thenReturn(messages);

        List<Map<String, Object>> result = helpChatService.getMessages("admin@gmail.com");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).get("senderEmail")).isEqualTo("patient@test.com");
        assertThat(result.get(1).get("senderEmail")).isEqualTo("admin@gmail.com");
    }

    @Test
    void getMessages_ForPatient_ShouldReturnFilteredMessages() {
        List<HelpChat> messages = Arrays.asList(patientMessage, adminMessage);
        when(helpChatRepository.findBySenderEmailOrAdminOrderBySentAtAsc("patient@test.com")).thenReturn(messages);

        List<Map<String, Object>> result = helpChatService.getMessages("patient@test.com");

        assertThat(result).hasSize(2);
        verify(helpChatRepository).findBySenderEmailOrAdminOrderBySentAtAsc("patient@test.com");
    }

    @Test
    void getConversationWithUser_ShouldReturnUserConversation() {
        List<HelpChat> messages = Arrays.asList(patientMessage, adminMessage);
        when(helpChatRepository.findBySenderEmailOrAdminOrderBySentAtAsc("patient@test.com")).thenReturn(messages);

        List<Map<String, Object>> result = helpChatService.getConversationWithUser("patient@test.com");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).get("message")).isEqualTo("Need help");
    }

    @Test
    void getAllUserEmails_ShouldReturnDistinctUserEmails() {
        List<HelpChat> messages = Arrays.asList(patientMessage, adminMessage);
        when(helpChatRepository.findAllByOrderBySentAtAsc()).thenReturn(messages);

        List<String> result = helpChatService.getAllUserEmails();

        assertThat(result).containsExactly("patient@test.com");
    }

    @Test
    void sendMessage_AsPatient_ShouldSaveMessage() {
        helpChatService.sendMessage("Help needed", "patient@test.com", null);

        verify(helpChatRepository).save(any(HelpChat.class));
    }

    @Test
    void sendMessage_AsAdmin_ShouldSaveMessageWithRecipient() {
        helpChatService.sendMessage("Admin reply", "admin@gmail.com", "patient@test.com");

        verify(helpChatRepository).save(argThat(helpChat -> 
            "ADMIN".equals(helpChat.getSenderType()) && 
            "patient@test.com".equals(helpChat.getRecipientEmail())
        ));
    }
}
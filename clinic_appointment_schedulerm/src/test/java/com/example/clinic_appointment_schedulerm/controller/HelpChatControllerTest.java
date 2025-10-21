package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.security.JwtService;
import com.example.clinic_appointment_schedulerm.service.HelpChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelpChatController.class)
class HelpChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HelpChatService helpChatService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "patient@test.com")
    void getMessages_ShouldReturnMessages() throws Exception {
        List<Map<String, Object>> messages = Arrays.asList(
            createMessage(1L, "patient@test.com", "PATIENT", "Help needed")
        );
        when(helpChatService.getMessages("patient@test.com")).thenReturn(messages);

        mockMvc.perform(get("/api/help-chat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].message").value("Help needed"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com")
    void getConversation_AsAdmin_ShouldReturnConversation() throws Exception {
        List<Map<String, Object>> messages = Arrays.asList(
            createMessage(1L, "patient@test.com", "PATIENT", "Help needed")
        );
        when(helpChatService.getConversationWithUser("patient@test.com")).thenReturn(messages);

        mockMvc.perform(get("/api/help-chat/conversation/patient@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Help needed"));
    }

    @Test
    @WithMockUser(username = "patient@test.com")
    void getConversation_AsNonAdmin_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/help-chat/conversation/patient@test.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com")
    void getAllUsers_AsAdmin_ShouldReturnUsers() throws Exception {
        List<String> users = Arrays.asList("patient@test.com", "doctor@test.com");
        when(helpChatService.getAllUserEmails()).thenReturn(users);

        mockMvc.perform(get("/api/help-chat/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("patient@test.com"))
                .andExpect(jsonPath("$[1]").value("doctor@test.com"));
    }

    @Test
    @WithMockUser(username = "patient@test.com")
    void getAllUsers_AsNonAdmin_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/help-chat/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "patient@test.com")
    void sendMessage_ShouldSendMessage() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("message", "Need help");

        mockMvc.perform(post("/api/help-chat")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(helpChatService).sendMessage("Need help", "patient@test.com", null);
    }

    private Map<String, Object> createMessage(Long id, String senderEmail, String senderType, String message) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("id", id);
        msg.put("senderEmail", senderEmail);
        msg.put("senderType", senderType);
        msg.put("message", message);
        msg.put("sentAt", "2024-01-01T10:00:00Z");
        return msg;
    }
}
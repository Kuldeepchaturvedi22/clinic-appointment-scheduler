package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.dto.SendMessageRequest;
import com.example.clinic_appointment_schedulerm.security.JwtService;
import com.example.clinic_appointment_schedulerm.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testGetMessages_Success() throws Exception {
        List<ChatService.ChatDto> messages = Arrays.asList(
            new ChatService.ChatDto(1L, "PATIENT", "John Doe", "Hello", OffsetDateTime.now())
        );
        when(chatService.getMessages(anyLong(), anyString())).thenReturn(messages);

        mockMvc.perform(get("/api/chat/appointment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].message").value("Hello"));
    }

    @Test
    @WithMockUser
    void testSendMessage_Success() throws Exception {
        SendMessageRequest request = new SendMessageRequest();
        request.setMessage("Hello Doctor");
        ChatService.ChatDto response = new ChatService.ChatDto(1L, "PATIENT", "John Doe", "Hello Doctor", OffsetDateTime.now());

        when(chatService.sendMessage(anyLong(), anyString(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/chat/appointment/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello Doctor"));
    }
}
package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.dto.RegisterRequest;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import com.example.clinic_appointment_schedulerm.security.JwtService;
import com.example.clinic_appointment_schedulerm.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;
    
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testRegisterPatient_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");
        request.setPassword("password");
        request.setFullName("John Doe");
        request.setPhone("1234567890");
        request.setDateOfBirth("1990-01-01");
        request.setGender("MALE");
        
        UserAccount user = UserAccount.builder()
                .id(1L)
                .email("john@example.com")
                .fullName("John Doe")
                .role(UserAccount.UserRole.PATIENT)
                .build();

        when(authService.registerPatient(any(RegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register-patient")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You are registered with user id: 1"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @WithMockUser
    void testRegisterDoctor_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("doctor@example.com");
        request.setPassword("password");
        request.setFullName("Dr. Smith");
        request.setPhone("1234567890");
        request.setSpecialization("Cardiology");
        
        UserAccount user = UserAccount.builder()
                .id(2L)
                .email("doctor@example.com")
                .fullName("Dr. Smith")
                .role(UserAccount.UserRole.DOCTOR)
                .build();

        when(authService.registerDoctor(any(RegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register-doctor")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You are registered with user id: 2"))
                .andExpect(jsonPath("$.userId").value(2));
    }
}
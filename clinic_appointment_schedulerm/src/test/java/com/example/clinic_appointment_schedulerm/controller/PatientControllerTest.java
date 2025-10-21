package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.dto.PatientProfileResponse;
import com.example.clinic_appointment_schedulerm.dto.UpdateProfileRequest;
import com.example.clinic_appointment_schedulerm.security.JwtService;
import com.example.clinic_appointment_schedulerm.service.AppointmentService;
import com.example.clinic_appointment_schedulerm.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testGetPatientProfile_Success() throws Exception {
        PatientProfileResponse profile = new PatientProfileResponse(1L, LocalDate.of(1990, 1, 1), "Male", 1L, "john@example.com", "John Doe", "1234567890", "PATIENT");
        when(patientService.findProfileByUserEmail(anyString())).thenReturn(profile);

        mockMvc.perform(get("/api/patients/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @WithMockUser
    void testUpdatePatientProfile_Success() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("John Updated");
        request.setEmail("john@example.com");
        request.setPhone("1234567890");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        
        PatientProfileResponse response = new PatientProfileResponse(1L, LocalDate.of(1990, 1, 1), "Male", 1L, "john@example.com", "John Updated", "1234567890", "PATIENT");

        when(patientService.updateProfile(anyString(), any(UpdateProfileRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/patients/me")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Updated"));
    }

    @Test
    @WithMockUser
    void testGetAppointmentHistory_Success() throws Exception {
        List<AppointmentService.AppointmentDto> appointments = Arrays.asList();
        when(appointmentService.getPatientAppointmentHistoryDto(anyString())).thenReturn(appointments);

        mockMvc.perform(get("/api/patients/me/appointments/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
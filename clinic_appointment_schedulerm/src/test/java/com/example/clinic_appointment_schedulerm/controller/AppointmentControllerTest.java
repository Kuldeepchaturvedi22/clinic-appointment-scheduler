package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.dto.AppointmentRequest;
import com.example.clinic_appointment_schedulerm.entity.Appointment;
import com.example.clinic_appointment_schedulerm.security.JwtService;
import com.example.clinic_appointment_schedulerm.service.AppointmentService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void testBookAppointment_Success() throws Exception {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setStartTime(OffsetDateTime.now());
        request.setEndTime(OffsetDateTime.now().plusHours(2));
        request.setNotes("Test notes");
        Appointment response = Appointment.builder()
                .id(1L)
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plusHours(2))
                .status(Appointment.Status.PENDING)
                .notes("Test notes")
                .build();

        when(appointmentService.book(anyLong(), anyLong(), any(OffsetDateTime.class), any(OffsetDateTime.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/appointments/book")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser
    void testGetAppointmentById_Success() throws Exception {
        Appointment appointment = Appointment.builder()
                .id(1L)
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plusHours(2))
                .status(Appointment.Status.PENDING)
                .notes("Test notes")
                .build();

        when(appointmentService.get(1L)).thenReturn(appointment);

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
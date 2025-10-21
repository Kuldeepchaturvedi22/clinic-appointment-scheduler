package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.dto.DoctorDashboardResponse;
import com.example.clinic_appointment_schedulerm.dto.DoctorListResponse;
import com.example.clinic_appointment_schedulerm.dto.UpdateProfileRequest;
import com.example.clinic_appointment_schedulerm.security.JwtService;
import com.example.clinic_appointment_schedulerm.service.AppointmentService;
import com.example.clinic_appointment_schedulerm.service.DoctorService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

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
    void testGetAllDoctors_Success() throws Exception {
        List<DoctorListResponse> doctors = Arrays.asList(
            new DoctorListResponse(1L, "Dr. Smith", "Cardiology", "smith@example.com", "1234567890", 4.5)
        );
        when(doctorService.findAllForList(anyString())).thenReturn(doctors);
        when(doctorService.findAllForList(isNull())).thenReturn(doctors);

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].fullName").value("Dr. Smith"));
    }

    @Test
    @WithMockUser
    void testGetDoctorDashboard_Success() throws Exception {
        DoctorDashboardResponse dashboard = new DoctorDashboardResponse(1L, "Dr. Smith", "Cardiology", "ACTIVE", 5, 3, 2);
        when(doctorService.getDashboard(anyString())).thenReturn(dashboard);

        mockMvc.perform(get("/api/doctors/me/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todaysAppointments").value(5))
                .andExpect(jsonPath("$.pendingAppointments").value(3))
                .andExpect(jsonPath("$.completedAppointments").value(2));
    }

    @Test
    @WithMockUser
    void testAcceptAppointment_Success() throws Exception {
        AppointmentService.AppointmentDto appointmentDto = new AppointmentService.AppointmentDto(
                1L, null, null, "SCHEDULED", 1L, "Dr. Smith", 1L, "John Doe", "Test notes"
        );
        when(appointmentService.toDto(any())).thenReturn(appointmentDto);

        mockMvc.perform(put("/api/doctors/appointments/1/accept")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }
}
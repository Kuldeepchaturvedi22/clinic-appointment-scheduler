package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.dto.DoctorDashboardResponse;
import com.example.clinic_appointment_schedulerm.dto.DoctorListResponse;
import com.example.clinic_appointment_schedulerm.dto.DoctorProfileResponse;
import com.example.clinic_appointment_schedulerm.entity.Appointment;
import com.example.clinic_appointment_schedulerm.entity.Doctor;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import com.example.clinic_appointment_schedulerm.repository.DoctorRepository;
import com.example.clinic_appointment_schedulerm.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentService appointmentService;
    
    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;
    private UserAccount userAccount;

    @BeforeEach
    void setUp() {
        userAccount = UserAccount.builder()
                .id(1L)
                .email("doctor@example.com")
                .fullName("Dr. Smith")
                .phone("1234567890")
                .role(UserAccount.UserRole.DOCTOR)
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .user(userAccount)
                .specialization("Cardiology")
                .build();
    }

    @Test
    void testFindAllForList_Success() {
        List<Doctor> doctors = Arrays.asList(doctor);
        when(doctorRepository.findAllWithUser()).thenReturn(doctors);
        when(ratingRepository.getAverageRatingByDoctorId(1L)).thenReturn(4.5);

        List<DoctorListResponse> result = doctorService.findAllForList("");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dr. Smith", result.get(0).getFullName());
        assertEquals("Cardiology", result.get(0).getSpecialization());
    }

    @Test
    void testFindAllForList_WithSearch() {
        List<Doctor> doctors = Arrays.asList(doctor);
        when(doctorRepository.findByNameOrSpecialization("cardio")).thenReturn(doctors);
        when(ratingRepository.getAverageRatingByDoctorId(1L)).thenReturn(4.5);

        List<DoctorListResponse> result = doctorService.findAllForList("cardio");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dr. Smith", result.get(0).getFullName());
    }

    @Test
    void testFindProfileByUserEmail_Success() {
        when(doctorRepository.findByUser_Email("doctor@example.com")).thenReturn(Optional.of(doctor));

        DoctorProfileResponse result = doctorService.findProfileByUserEmail("doctor@example.com");

        assertNotNull(result);
        assertEquals("Dr. Smith", result.getFullName());
        assertEquals("Cardiology", result.getSpecialization());
        assertEquals("doctor@example.com", result.getEmail());
    }

    @Test
    void testFindProfileByUserEmail_NotFound() {
        when(doctorRepository.findByUser_Email("doctor@example.com")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> doctorService.findProfileByUserEmail("doctor@example.com"));
    }

    @Test
    void testGetDashboard_Success() {
        when(doctorRepository.findByUser_Email("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(appointmentService.getTodayWindow()).thenReturn(new OffsetDateTime[]{OffsetDateTime.now().minusHours(12), OffsetDateTime.now().plusHours(12)});
        when(appointmentService.listByDoctor(eq(1L), any(OffsetDateTime.class), any(OffsetDateTime.class))).thenReturn(Arrays.asList(new Appointment(), new Appointment()));
        when(appointmentService.getPendingAppointmentsByDoctor("doctor@example.com")).thenReturn(Arrays.asList(new Appointment()));
        when(appointmentService.getCompletedAppointmentsByDoctor("doctor@example.com")).thenReturn(Arrays.asList(new Appointment(), new Appointment(), new Appointment()));

        DoctorDashboardResponse result = doctorService.getDashboard("doctor@example.com");

        assertNotNull(result);
        assertEquals(2, result.getTodaysAppointments());
        assertEquals(1, result.getPendingAppointments());
        assertEquals(3, result.getCompletedAppointments());
    }
}
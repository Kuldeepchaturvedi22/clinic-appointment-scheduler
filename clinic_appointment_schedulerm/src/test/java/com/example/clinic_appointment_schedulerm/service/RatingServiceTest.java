package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.entity.Appointment;
import com.example.clinic_appointment_schedulerm.entity.Doctor;
import com.example.clinic_appointment_schedulerm.entity.Patient;
import com.example.clinic_appointment_schedulerm.entity.Rating;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import com.example.clinic_appointment_schedulerm.repository.AppointmentRepository;
import com.example.clinic_appointment_schedulerm.repository.PatientRepository;
import com.example.clinic_appointment_schedulerm.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;
    
    @Mock
    private AppointmentRepository appointmentRepository;
    
    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private RatingService ratingService;

    private Patient patient;
    private Doctor doctor;
    private Appointment appointment;
    private UserAccount patientUser;

    @BeforeEach
    void setUp() {
        patientUser = new UserAccount();
        patientUser.setEmail("patient@test.com");
        
        patient = new Patient();
        patient.setId(1L);
        patient.setUser(patientUser);
        
        doctor = new Doctor();
        doctor.setId(1L);
        
        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(Appointment.Status.COMPLETED);
    }

    @Test
    void rateDoctor_WithValidAppointment_ShouldCreateRating() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(patientRepository.findByUser_Email("patient@test.com")).thenReturn(Optional.of(patient));
        when(ratingRepository.findByAppointmentId(1L)).thenReturn(Optional.empty());

        ratingService.rateDoctor(1L, 5, "Great doctor", "patient@test.com");

        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void rateDoctor_WithExistingRating_ShouldUpdateRating() {
        Rating existingRating = new Rating();
        existingRating.setStars(3);
        existingRating.setComment("Good");
        
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(patientRepository.findByUser_Email("patient@test.com")).thenReturn(Optional.of(patient));
        when(ratingRepository.findByAppointmentId(1L)).thenReturn(Optional.of(existingRating));

        ratingService.rateDoctor(1L, 5, "Excellent", "patient@test.com");

        verify(ratingRepository).save(argThat(rating -> 
            rating.getStars() == 5 && "Excellent".equals(rating.getComment())));
    }

    @Test
    void rateDoctor_WithPendingAppointment_ShouldThrowException() {
        appointment.setStatus(Appointment.Status.PENDING);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(patientRepository.findByUser_Email("patient@test.com")).thenReturn(Optional.of(patient));

        assertThatThrownBy(() -> ratingService.rateDoctor(1L, 5, "Great", "patient@test.com"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Can only rate scheduled or completed appointments");
    }

    @Test
    void rateDoctor_WithWrongPatient_ShouldThrowException() {
        Patient otherPatient = new Patient();
        otherPatient.setId(2L);
        
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(patientRepository.findByUser_Email("other@test.com")).thenReturn(Optional.of(otherPatient));

        assertThatThrownBy(() -> ratingService.rateDoctor(1L, 5, "Great", "other@test.com"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Can only rate your own appointments");
    }

    @Test
    void deleteRating_AsOwner_ShouldDeleteRating() {
        Rating rating = new Rating();
        rating.setId(1L);
        rating.setPatient(patient);
        
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        ratingService.deleteRating(1L, "patient@test.com");

        verify(ratingRepository).delete(rating);
    }

    @Test
    void deleteRating_AsAdmin_ShouldDeleteRating() {
        Rating rating = new Rating();
        rating.setId(1L);
        rating.setPatient(patient);
        
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating));

        ratingService.deleteRating(1L, "admin@gmail.com");

        verify(ratingRepository).delete(rating);
    }
}
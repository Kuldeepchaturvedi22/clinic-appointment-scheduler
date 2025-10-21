package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.entity.Appointment;
import com.example.clinic_appointment_schedulerm.entity.Doctor;
import com.example.clinic_appointment_schedulerm.entity.Patient;
import com.example.clinic_appointment_schedulerm.repository.AppointmentRepository;
import com.example.clinic_appointment_schedulerm.repository.DoctorRepository;
import com.example.clinic_appointment_schedulerm.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    
    @Mock
    private PatientRepository patientRepository;
    
    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        
        doctor = new Doctor();
        doctor.setId(1L);
        
        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(Appointment.Status.PENDING);
        appointment.setStartTime(OffsetDateTime.now().plusHours(1));
        appointment.setEndTime(OffsetDateTime.now().plusHours(3));
    }

    @Test
    void bookAppointment_ShouldCreateAppointment() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.book(1L, 1L, 
            OffsetDateTime.now().plusHours(1), 
            OffsetDateTime.now().plusHours(3), 
            "Test notes");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Appointment.Status.PENDING);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_WithInvalidPatient_ShouldThrowException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.book(1L, 1L, 
            OffsetDateTime.now().plusHours(1), 
            OffsetDateTime.now().plusHours(3), 
            "Test notes"))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Patient not found");
    }

    @Test
    void acceptAppointment_ShouldChangeStatusToScheduled() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findByUser_Email("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        appointmentService.acceptAppointment(1L, "doctor@example.com");

        verify(appointmentRepository).save(argThat(apt -> 
            apt.getStatus() == Appointment.Status.SCHEDULED));
    }

    @Test
    void rejectAppointment_ShouldChangeStatusToCancelled() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findByUser_Email("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        appointmentService.rejectAppointment(1L, "doctor@example.com");

        verify(appointmentRepository).save(argThat(apt -> 
            apt.getStatus() == Appointment.Status.CANCELLED));
    }

    @Test
    void completeAppointment_ShouldChangeStatusToCompleted() {
        appointment.setStatus(Appointment.Status.SCHEDULED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findByUser_Email("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        appointmentService.completeAppointment(1L, "doctor@example.com");

        verify(appointmentRepository).save(argThat(apt -> 
            apt.getStatus() == Appointment.Status.COMPLETED));
    }
}
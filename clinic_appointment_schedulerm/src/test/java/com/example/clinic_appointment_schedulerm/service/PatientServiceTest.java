package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.dto.PatientProfileResponse;
import com.example.clinic_appointment_schedulerm.dto.UpdateProfileRequest;
import com.example.clinic_appointment_schedulerm.entity.Patient;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import com.example.clinic_appointment_schedulerm.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private UserAccount userAccount;

    @BeforeEach
    void setUp() {
        userAccount = UserAccount.builder()
                .id(1L)
                .email("patient@example.com")
                .fullName("John Doe")
                .phone("1234567890")
                .role(UserAccount.UserRole.PATIENT)
                .build();

        patient = Patient.builder()
                .id(1L)
                .user(userAccount)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("Male")
                .build();
    }

    @Test
    void testFindProfileByUserEmail_Success() {
        when(patientRepository.findByUser_Email("patient@example.com")).thenReturn(Optional.of(patient));

        PatientProfileResponse result = patientService.findProfileByUserEmail("patient@example.com");

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals("patient@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhone());
        assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth());
        assertEquals("Male", result.getGender());
    }

    @Test
    void testFindProfileByUserEmail_NotFound() {
        when(patientRepository.findByUser_Email("patient@example.com")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> patientService.findProfileByUserEmail("patient@example.com"));
    }

    @Test
    void testUpdateProfile_Success() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("John Updated");
        request.setEmail("patient@example.com");
        request.setPhone("9876543210");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        
        when(patientRepository.findByUser_Email("patient@example.com")).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(patientRepository.findByUser_Email("patient@example.com")).thenReturn(Optional.of(patient));

        PatientProfileResponse result = patientService.updateProfile("patient@example.com", request);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
        assertEquals("John Updated", patient.getUser().getFullName());
        assertEquals("9876543210", patient.getUser().getPhone());
    }

    @Test
    void testUpdateProfile_NotFound() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("John Updated");
        request.setEmail("patient@example.com");
        request.setPhone("9876543210");
        
        when(patientRepository.findByUser_Email("patient@example.com")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> patientService.updateProfile("patient@example.com", request));
    }
}
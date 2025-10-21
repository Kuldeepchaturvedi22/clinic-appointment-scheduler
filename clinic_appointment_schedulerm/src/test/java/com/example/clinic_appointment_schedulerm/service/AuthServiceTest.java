package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.dto.RegisterRequest;
import com.example.clinic_appointment_schedulerm.entity.Doctor;
import com.example.clinic_appointment_schedulerm.entity.Patient;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import com.example.clinic_appointment_schedulerm.repository.DoctorRepository;
import com.example.clinic_appointment_schedulerm.repository.PatientRepository;
import com.example.clinic_appointment_schedulerm.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;



    @InjectMocks
    private AuthService authService;





    @Test
    void testRegisterPatient_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");
        request.setPassword("password");
        request.setFullName("John Doe");
        request.setPhone("1234567890");
        request.setDateOfBirth("1990-01-01");
        request.setGender("Male");

        UserAccount savedUser = UserAccount.builder()
                .id(1L)
                .email("john@example.com")
                .fullName("John Doe")
                .role(UserAccount.UserRole.PATIENT)
                .build();

        when(userAccountRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(savedUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(new Patient());

        UserAccount result = authService.registerPatient(request);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        assertEquals(UserAccount.UserRole.PATIENT, result.getRole());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testRegisterDoctor_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("smith@example.com");
        request.setPassword("password");
        request.setFullName("Dr. Smith");
        request.setPhone("1234567890");
        request.setSpecialization("Cardiology");

        UserAccount savedUser = UserAccount.builder()
                .id(1L)
                .email("smith@example.com")
                .fullName("Dr. Smith")
                .role(UserAccount.UserRole.DOCTOR)
                .build();

        when(userAccountRepository.existsByEmail("smith@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(savedUser);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(new Doctor());

        UserAccount result = authService.registerDoctor(request);

        assertNotNull(result);
        assertEquals("smith@example.com", result.getEmail());
        assertEquals(UserAccount.UserRole.DOCTOR, result.getRole());
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void testRegisterPatient_EmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");
        request.setPassword("password");
        request.setFullName("John Doe");

        when(userAccountRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.registerPatient(request));
    }
}
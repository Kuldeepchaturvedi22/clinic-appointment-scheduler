package com.example.clinic_appointment_schedulerm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

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

        patient = new Patient();
    }

    @Test
    void testPatientCreation() {
        patient.setId(1L);
        patient.setUser(userAccount);
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setGender("Male");

        assertEquals(1L, patient.getId());
        assertEquals(userAccount, patient.getUser());
        assertEquals("John Doe", patient.getUser().getFullName());
        assertEquals("1234567890", patient.getUser().getPhone());
        assertEquals(LocalDate.of(1990, 1, 1), patient.getDateOfBirth());
        assertEquals("Male", patient.getGender());
    }

    @Test
    void testPatientUserRelationship() {
        patient.setUser(userAccount);

        assertNotNull(patient.getUser());
        assertEquals("patient@example.com", patient.getUser().getEmail());
        assertEquals(UserAccount.UserRole.PATIENT, patient.getUser().getRole());
    }


}
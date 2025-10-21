package com.example.clinic_appointment_schedulerm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAccountTest {

    @Test
    void testUserAccountCreation() {
        UserAccount user = new UserAccount();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(UserAccount.UserRole.PATIENT);

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(UserAccount.UserRole.PATIENT, user.getRole());
    }

    @Test
    void testRoleEnum() {
        assertEquals("PATIENT", UserAccount.UserRole.PATIENT.name());
        assertEquals("DOCTOR", UserAccount.UserRole.DOCTOR.name());
        assertEquals("ADMIN", UserAccount.UserRole.ADMIN.name());
    }


}
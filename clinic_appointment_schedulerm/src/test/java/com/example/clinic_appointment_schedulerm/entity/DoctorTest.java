package com.example.clinic_appointment_schedulerm.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DoctorTest {

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

        doctor = new Doctor();
    }

    @Test
    void testDoctorCreation() {
        doctor.setId(1L);
        doctor.setUser(userAccount);
        doctor.setSpecialization("Cardiology");

        assertEquals(1L, doctor.getId());
        assertEquals(userAccount, doctor.getUser());
        assertEquals("Cardiology", doctor.getSpecialization());
        assertEquals("Dr. Smith", doctor.getUser().getFullName());
        assertEquals("1234567890", doctor.getUser().getPhone());
    }

    @Test
    void testDoctorUserRelationship() {
        doctor.setUser(userAccount);

        assertNotNull(doctor.getUser());
        assertEquals("doctor@example.com", doctor.getUser().getEmail());
        assertEquals(UserAccount.UserRole.DOCTOR, doctor.getUser().getRole());
    }

//    @Test
//    void testEqualsAndHashCode() {
//        doctor.setId(1L);
//
//        Doctor doctor2 = new Doctor();
//        doctor2.setId(1L);
//
//        Doctor doctor3 = new Doctor();
//        doctor3.setId(2L);
//
//        assertEquals(doctor, doctor2);
//        assertNotEquals(doctor, doctor3);
//        assertEquals(doctor.hashCode(), doctor2.hashCode());
//    }
//
//    @Test
//    void testToString() {
//        doctor.setId(1L);
//        doctor.setUser(userAccount);
//        doctor.setSpecialization("Cardiology");
//
//        String toString = doctor.toString();
//        assertTrue(toString.contains("Cardiology"));
//    }
}
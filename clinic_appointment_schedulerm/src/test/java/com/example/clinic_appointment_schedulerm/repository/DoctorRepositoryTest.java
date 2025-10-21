package com.example.clinic_appointment_schedulerm.repository;

import com.example.clinic_appointment_schedulerm.entity.Doctor;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DoctorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void testFindByUserEmail_Success() {
        UserAccount user = UserAccount.builder()
                .email("doctor@example.com")
                .password("password")
                .fullName("Dr. Smith")
                .phone("1234567890")
                .role(UserAccount.UserRole.DOCTOR)
                .build();
        entityManager.persist(user);

        Doctor doctor = Doctor.builder()
                .user(user)
                .specialization("Cardiology")
                .build();
        entityManager.persistAndFlush(doctor);

        Optional<Doctor> found = doctorRepository.findByUser_Email("doctor@example.com");

        assertTrue(found.isPresent());
        assertEquals("Dr. Smith", found.get().getUser().getFullName());
        assertEquals("Cardiology", found.get().getSpecialization());
    }

    @Test
    void testFindByUserEmail_NotFound() {
        Optional<Doctor> found = doctorRepository.findByUser_Email("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByNameOrSpecialization() {
        UserAccount user1 = UserAccount.builder()
                .email("doctor1@example.com")
                .password("password")
                .fullName("Dr. Smith")
                .phone("1234567890")
                .role(UserAccount.UserRole.DOCTOR)
                .build();
        entityManager.persist(user1);

        UserAccount user2 = UserAccount.builder()
                .email("doctor2@example.com")
                .password("password")
                .fullName("Dr. Johnson")
                .phone("9876543210")
                .role(UserAccount.UserRole.DOCTOR)
                .build();
        entityManager.persist(user2);

        Doctor doctor1 = Doctor.builder()
                .user(user1)
                .specialization("Cardiology")
                .build();
        entityManager.persist(doctor1);

        Doctor doctor2 = Doctor.builder()
                .user(user2)
                .specialization("Neurology")
                .build();
        entityManager.persistAndFlush(doctor2);

        List<Doctor> foundByName = doctorRepository.findByNameOrSpecialization("smith");
        assertEquals(1, foundByName.size());
        assertEquals("Dr. Smith", foundByName.get(0).getUser().getFullName());

        List<Doctor> foundBySpecialization = doctorRepository.findByNameOrSpecialization("cardio");
        assertEquals(1, foundBySpecialization.size());
        assertEquals("Cardiology", foundBySpecialization.get(0).getSpecialization());
    }
}
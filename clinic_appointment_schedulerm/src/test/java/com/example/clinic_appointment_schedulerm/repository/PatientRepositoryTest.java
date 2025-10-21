package com.example.clinic_appointment_schedulerm.repository;

import com.example.clinic_appointment_schedulerm.entity.Patient;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void testFindByUserEmail_Success() {
        UserAccount user = UserAccount.builder()
                .email("patient@example.com")
                .password("password")
                .fullName("John Doe")
                .phone("1234567890")
                .role(UserAccount.UserRole.PATIENT)
                .build();
        entityManager.persist(user);

        Patient patient = Patient.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("Male")
                .build();
        entityManager.persistAndFlush(patient);

        Optional<Patient> found = patientRepository.findByUser_Email("patient@example.com");

        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getUser().getFullName());
        assertEquals("1234567890", found.get().getUser().getPhone());
        assertEquals("Male", found.get().getGender());
    }

    @Test
    void testFindByUserEmail_NotFound() {
        Optional<Patient> found = patientRepository.findByUser_Email("nonexistent@example.com");

        assertFalse(found.isPresent());
    }
}
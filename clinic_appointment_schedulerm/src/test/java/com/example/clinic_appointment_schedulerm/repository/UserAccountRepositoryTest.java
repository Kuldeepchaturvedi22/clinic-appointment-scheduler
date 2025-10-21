package com.example.clinic_appointment_schedulerm.repository;

import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserAccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    void testFindByEmail_Success() {
        UserAccount user = UserAccount.builder()
                .email("test@example.com")
                .password("password")
                .fullName("Test User")
                .role(UserAccount.UserRole.PATIENT)
                .build();
        entityManager.persistAndFlush(user);

        Optional<UserAccount> found = userAccountRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals(UserAccount.UserRole.PATIENT, found.get().getRole());
    }

    @Test
    void testFindByEmail_NotFound() {
        Optional<UserAccount> found = userAccountRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByEmail_True() {
        UserAccount user = UserAccount.builder()
                .email("test@example.com")
                .password("password")
                .fullName("Test User")
                .role(UserAccount.UserRole.PATIENT)
                .build();
        entityManager.persistAndFlush(user);

        boolean exists = userAccountRepository.existsByEmail("test@example.com");

        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_False() {
        boolean exists = userAccountRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }
}
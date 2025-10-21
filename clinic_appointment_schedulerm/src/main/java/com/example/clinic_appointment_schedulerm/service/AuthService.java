package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.dto.RegisterRequest;
import com.example.clinic_appointment_schedulerm.entity.*;
import com.example.clinic_appointment_schedulerm.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserAccountRepository userRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserAccount registerPatient(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        UserAccount ua = UserAccount.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .role(UserAccount.UserRole.PATIENT)
                .build();
        ua = userRepo.save(ua);
        Patient p = Patient.builder()
                .user(ua)
                .dateOfBirth(LocalDate.parse(req.getDateOfBirth()))
                .gender(req.getGender())
                .build();
        patientRepo.save(p);
        log.info("Registered patient userId={}", ua.getId());
        return ua;
    }

    @Transactional
    public UserAccount registerDoctor(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        UserAccount ua = UserAccount.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .role(UserAccount.UserRole.DOCTOR)
                .build();
        ua = userRepo.save(ua);
        Doctor d = Doctor.builder()
                .user(ua)
                .specialization(req.getSpecialization())
                .build();
        doctorRepo.save(d);
        log.info("Registered doctor userId={}", ua.getId());
        return ua;
    }
}
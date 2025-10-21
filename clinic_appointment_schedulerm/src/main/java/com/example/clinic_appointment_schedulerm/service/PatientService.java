package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.dto.PatientProfileResponse;
import com.example.clinic_appointment_schedulerm.dto.UpdateProfileRequest;
import com.example.clinic_appointment_schedulerm.entity.Patient;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import com.example.clinic_appointment_schedulerm.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repo;

    public List<Patient> findAll() {
        return repo.findAll();
    }

    public Patient findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Patient not found"));
    }

    public Patient save(Patient p) {
        return repo.save(p);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Patient findByUserEmail(String email) {
        return repo.findByUser_Email(email).orElseThrow(() -> new NoSuchElementException("Patient not found for current user"));
    }

    @Transactional(readOnly = true)
    public PatientProfileResponse findProfileByUserEmail(String email) {
        Patient patient = repo.findByUser_Email(email).orElseThrow(() -> new NoSuchElementException("Patient not found for current user"));
        UserAccount user = patient.getUser();

        return PatientProfileResponse.builder()
                .id(patient.getId())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .build();
    }

    @Transactional
    public PatientProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        Patient patient = repo.findByUser_Email(email).orElseThrow(() -> new NoSuchElementException("Patient not found for current user"));
        UserAccount user = patient.getUser();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());

        repo.save(patient);
        return findProfileByUserEmail(request.getEmail());
    }


}
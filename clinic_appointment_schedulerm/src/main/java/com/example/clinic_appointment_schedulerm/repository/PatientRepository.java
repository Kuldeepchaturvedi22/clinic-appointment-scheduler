package com.example.clinic_appointment_schedulerm.repository;

import com.example.clinic_appointment_schedulerm.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByUser_Id(Long userId);

    Optional<Patient> findByUser_Email(String email);
    
    @Query("SELECT p FROM Patient p JOIN FETCH p.user")
    List<Patient> findAllWithUser();
}
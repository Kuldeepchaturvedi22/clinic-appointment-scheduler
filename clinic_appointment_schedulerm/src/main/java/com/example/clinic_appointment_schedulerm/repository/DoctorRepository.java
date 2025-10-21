package com.example.clinic_appointment_schedulerm.repository;

import com.example.clinic_appointment_schedulerm.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByUser_Id(Long userId);
    Optional<Doctor> findByUser_Email(String email);

    @Query("select d from Doctor d join fetch d.user")
    List<Doctor> findAllWithUser();

    @Query("select d from Doctor d join fetch d.user u where lower(u.fullName) like lower(concat('%', :search, '%')) or lower(d.specialization) like lower(concat('%', :search, '%'))")
    List<Doctor> findByNameOrSpecialization(String search);


}
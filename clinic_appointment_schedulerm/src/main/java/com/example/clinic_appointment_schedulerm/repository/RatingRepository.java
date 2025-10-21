package com.example.clinic_appointment_schedulerm.repository;

import com.example.clinic_appointment_schedulerm.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByAppointmentId(Long appointmentId);
    Optional<Rating> findByAppointmentId(Long appointmentId);
    
    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.doctor.id = :doctorId")
    Double getAverageRatingByDoctorId(Long doctorId);
    
    List<Rating> findByDoctorId(Long doctorId);
}
package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.entity.Appointment;
import com.example.clinic_appointment_schedulerm.entity.Rating;
import com.example.clinic_appointment_schedulerm.repository.AppointmentRepository;
import com.example.clinic_appointment_schedulerm.repository.PatientRepository;
import com.example.clinic_appointment_schedulerm.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RatingService {
    
    private final RatingRepository ratingRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    
    @Transactional
    public void rateDoctor(Long appointmentId, Integer stars, String comment, String patientEmail) {
        var appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        
        var patient = patientRepository.findByUser_Email(patientEmail)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        
        // Check if appointment is scheduled/completed and belongs to patient
        if (!appointment.getStatus().equals(Appointment.Status.SCHEDULED) && 
            !appointment.getStatus().equals(Appointment.Status.COMPLETED)) {
            throw new IllegalArgumentException("Can only rate scheduled or completed appointments");
        }
        
        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new IllegalArgumentException("Can only rate your own appointments");
        }
        
        // Check if already rated - if yes, update existing rating
        var existingRating = ratingRepository.findByAppointmentId(appointmentId);
        if (existingRating.isPresent()) {
            var rating = existingRating.get();
            rating.setStars(stars);
            rating.setComment(comment);
            ratingRepository.save(rating);
        } else {
            Rating rating = Rating.builder()
                .patient(patient)
                .doctor(appointment.getDoctor())
                .appointment(appointment)
                .stars(stars)
                .comment(comment)
                .build();
                
            ratingRepository.save(rating);
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDoctorRatings(Long doctorId) {
        return ratingRepository.findByDoctorId(doctorId).stream()
                .map(r -> {
                    Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", r.getId());
                    m.put("stars", r.getStars());
                    m.put("comment", r.getComment() != null ? r.getComment() : "");
                    m.put("patientName", r.getPatient() != null && r.getPatient().getUser() != null
                            ? r.getPatient().getUser().getFullName() : "");
                    m.put("appointmentId", r.getAppointment() != null ? r.getAppointment().getId() : null);
                    return m;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllRatings() {
        return ratingRepository.findAll().stream()
                .map(r -> {
                    Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", r.getId());
                    m.put("stars", r.getStars());
                    m.put("comment", r.getComment() != null ? r.getComment() : "");
                    m.put("patientName", r.getPatient() != null && r.getPatient().getUser() != null
                            ? r.getPatient().getUser().getFullName() : "");
                    m.put("doctorName", r.getDoctor() != null && r.getDoctor().getUser() != null
                            ? r.getDoctor().getUser().getFullName() : "");
                    m.put("appointmentId", r.getAppointment() != null ? r.getAppointment().getId() : null);
                    return m;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Transactional
    public void updateRating(Long ratingId, Integer stars, String comment, String userEmail) {
        var rating = ratingRepository.findById(ratingId)
            .orElseThrow(() -> new IllegalArgumentException("Rating not found"));
        
        // Check if user owns this rating or is admin
        if (!rating.getPatient().getUser().getEmail().equals(userEmail) && !"admin@gmail.com".equals(userEmail)) {
            throw new IllegalArgumentException("Can only edit your own ratings");
        }
        
        rating.setStars(stars);
        rating.setComment(comment);
        ratingRepository.save(rating);
    }
    
    @Transactional
    public void deleteRating(Long ratingId, String userEmail) {
        var rating = ratingRepository.findById(ratingId)
            .orElseThrow(() -> new IllegalArgumentException("Rating not found"));
        
        // Check if user owns this rating or is admin
        if (!rating.getPatient().getUser().getEmail().equals(userEmail) && !"admin@gmail.com".equals(userEmail)) {
            throw new IllegalArgumentException("Can only delete your own ratings");
        }
        
        ratingRepository.delete(rating);
    }
}
package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.repository.AppointmentRepository;
import com.example.clinic_appointment_schedulerm.repository.DoctorRepository;
import com.example.clinic_appointment_schedulerm.repository.PatientRepository;
import com.example.clinic_appointment_schedulerm.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final RatingRepository ratingRepository;
    
    @Transactional(readOnly = true)
    public Map<String, Object> getAllUsers() {
        var patients = patientRepository.findAllWithUser().stream()
            .map(p -> Map.of(
                "id", p.getId(),
                "type", "PATIENT",
                "fullName", p.getUser().getFullName(),
                "email", p.getUser().getEmail(),
                "phone", p.getUser().getPhone(),
                "dateOfBirth", p.getDateOfBirth().toString(),
                "gender", p.getGender() != null ? p.getGender() : "Not specified"
            ))
            .collect(Collectors.toList());
            
        var doctors = doctorRepository.findAllWithUser().stream()
            .map(d -> Map.of(
                "id", d.getId(),
                "type", "DOCTOR", 
                "fullName", d.getUser().getFullName(),
                "email", d.getUser().getEmail(),
                "phone", d.getUser().getPhone(),
                "specialization", d.getSpecialization()
            ))
            .collect(Collectors.toList());
            
        Map<String, Object> result = new HashMap<>();
        result.put("patients", patients);
        result.put("doctors", doctors);
        return result;
    }
    
    @Transactional
    public void deleteUser(String type, Long id) {
        if ("PATIENT".equals(type)) {
            // Delete appointments first
            appointmentRepository.deleteByPatientId(id);
            patientRepository.deleteById(id);
        } else if ("DOCTOR".equals(type)) {
            // Delete appointments first
            appointmentRepository.deleteByDoctorId(id);
            doctorRepository.deleteById(id);
        }
    }
    
    @Transactional
    public void updateUser(String type, Long id, Map<String, String> updates) {
        if ("PATIENT".equals(type)) {
            var patient = patientRepository.findById(id).orElseThrow();
            if (updates.containsKey("fullName")) patient.getUser().setFullName(updates.get("fullName"));
            if (updates.containsKey("email")) patient.getUser().setEmail(updates.get("email"));
            if (updates.containsKey("phone")) patient.getUser().setPhone(updates.get("phone"));
            if (updates.containsKey("gender")) patient.setGender(updates.get("gender"));
            patientRepository.save(patient);
        } else if ("DOCTOR".equals(type)) {
            var doctor = doctorRepository.findById(id).orElseThrow();
            if (updates.containsKey("fullName")) doctor.getUser().setFullName(updates.get("fullName"));
            if (updates.containsKey("email")) doctor.getUser().setEmail(updates.get("email"));
            if (updates.containsKey("phone")) doctor.getUser().setPhone(updates.get("phone"));
            if (updates.containsKey("specialization")) doctor.setSpecialization(updates.get("specialization"));
            doctorRepository.save(doctor);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllAppointments() {
        return appointmentRepository.findAllWithDoctorAndPatientAndUsers().stream()
            .map(a -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", a.getId());
                m.put("startTime", a.getStartTime() != null ? a.getStartTime().toString() : "");
                m.put("endTime", a.getEndTime() != null ? a.getEndTime().toString() : "");
                m.put("status", a.getStatus() != null ? a.getStatus().toString() : "");
                m.put("notes", a.getNotes() != null ? a.getNotes() : "");
                m.put("doctorName", a.getDoctor() != null && a.getDoctor().getUser() != null ? a.getDoctor().getUser().getFullName() : "");
                m.put("doctorSpecialization", a.getDoctor() != null ? String.valueOf(a.getDoctor().getSpecialization()) : "");
                m.put("patientName", a.getPatient() != null && a.getPatient().getUser() != null ? a.getPatient().getUser().getFullName() : "");
                m.put("patientEmail", a.getPatient() != null && a.getPatient().getUser() != null ? a.getPatient().getUser().getEmail() : "");
                return m;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllRatings() {
        return ratingRepository.findAll().stream()
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", r.getId());
                    m.put("stars", r.getStars());
                    m.put("comment", r.getComment() != null ? r.getComment() : "");
                    m.put("patientName", r.getPatient() != null && r.getPatient().getUser() != null ? r.getPatient().getUser().getFullName() : "");
                    m.put("doctorName", r.getDoctor() != null && r.getDoctor().getUser() != null ? r.getDoctor().getUser().getFullName() : "");
                    m.put("appointmentId", r.getAppointment() != null ? r.getAppointment().getId() : null);
                    return m;
                })
                .collect(Collectors.toList());
    }
}
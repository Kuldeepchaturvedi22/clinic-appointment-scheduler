package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.dto.DoctorDashboardResponse;
import com.example.clinic_appointment_schedulerm.dto.DoctorListResponse;
import com.example.clinic_appointment_schedulerm.dto.DoctorProfileResponse;
import com.example.clinic_appointment_schedulerm.dto.UpdateProfileRequest;
import com.example.clinic_appointment_schedulerm.entity.Doctor;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import com.example.clinic_appointment_schedulerm.repository.DoctorRepository;
import com.example.clinic_appointment_schedulerm.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private static final String DOCTOR_NOT_FOUND = "Doctor not found";
    private static final String DOCTOR_NOT_FOUND_FOR_CURRENT_USER = "Doctor not found for current user";

    private final DoctorRepository doctorRepository;
    private final AppointmentService appointmentService;
    private final RatingRepository ratingRepository;

    @Transactional(readOnly = true)
    public List<Doctor> findAll() { return doctorRepository.findAll(); }

    @Transactional(readOnly = true)
    public Doctor findById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(DOCTOR_NOT_FOUND));
    }

    @Transactional
    public Doctor save(Doctor d) { return doctorRepository.save(d); }

    @Transactional
    public void delete(Long id) { doctorRepository.deleteById(id); }

    @Transactional(readOnly = true)
    public Doctor findByUserEmail(String email) {
        return getDoctorByUserEmail(email);
    }

    @Transactional(readOnly = true)
    public DoctorProfileResponse findProfileByUserEmail(String email) {
        Doctor doctor = getDoctorByUserEmail(email);
        return toProfileResponse(doctor);
    }

    @Transactional
    public Doctor updateProfile(String email, UpdateProfileRequest request) {
        Doctor doctor = getDoctorByUserEmail(email);
        UserAccount user = doctor.getUser();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        doctor.setSpecialization(request.getSpecialization());

        return doctorRepository.save(doctor);
    }

    @Transactional
    public DoctorProfileResponse updateProfileAndReturn(String email, UpdateProfileRequest request) {
        updateProfile(email, request);
        // Reload using the same principal email to avoid mismatch immediately after update
        return findProfileByUserEmail(email);
    }

    @Transactional(readOnly = true)
    public List<DoctorListResponse> findAllForList(String search) {
        List<Doctor> doctors = (search == null || search.trim().isEmpty()) 
            ? doctorRepository.findAllWithUser()
            : doctorRepository.findByNameOrSpecialization(search.trim());
        return doctors.stream()
                .map(this::toListResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DoctorDashboardResponse getDashboard(String email) {
        Doctor doctor = getDoctorByUserEmail(email);
        UserAccount user = doctor.getUser();

        OffsetDateTime[] window = appointmentService.getTodayWindow();
        int todaysCount = appointmentService.listByDoctor(doctor.getId(), window[0], window[1]).size();
        int pendingCount = appointmentService.getPendingAppointmentsByDoctor(email).size();
        int completedCount = appointmentService.getCompletedAppointmentsByDoctor(email).size();

        return DoctorDashboardResponse.builder()
                .doctorId(doctor.getId())
                .fullName(user.getFullName())
                .specialization(doctor.getSpecialization())
                .status("Active")
                .todaysAppointments(todaysCount)
                .pendingAppointments(pendingCount)
                .completedAppointments(completedCount)
                .build();
    }

    private Doctor getDoctorByUserEmail(String email) {
        return doctorRepository.findByUser_Email(email)
                .orElseThrow(() -> new NoSuchElementException(DOCTOR_NOT_FOUND_FOR_CURRENT_USER));
    }

    private DoctorProfileResponse toProfileResponse(Doctor doctor) {
        UserAccount user = doctor.getUser();
        return DoctorProfileResponse.builder()
                .id(doctor.getId())
                .specialization(doctor.getSpecialization())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .build();
    }

    private DoctorListResponse toListResponse(Doctor doctor) {
        UserAccount user = doctor.getUser();
        Double avgRating = ratingRepository.getAverageRatingByDoctorId(doctor.getId());
        return DoctorListResponse.builder()
                .id(doctor.getId())
                .fullName(user.getFullName())
                .specialization(doctor.getSpecialization())
                .email(user.getEmail())
                .phone(user.getPhone())
                .averageRating(avgRating != null ? avgRating : 0.0)
                .build();
    }
}
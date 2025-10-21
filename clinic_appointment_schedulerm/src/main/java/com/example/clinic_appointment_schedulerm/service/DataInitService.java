package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.entity.Appointment;
import com.example.clinic_appointment_schedulerm.entity.Doctor;
import com.example.clinic_appointment_schedulerm.entity.Patient;
import com.example.clinic_appointment_schedulerm.repository.AppointmentRepository;
import com.example.clinic_appointment_schedulerm.repository.DoctorRepository;
import com.example.clinic_appointment_schedulerm.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataInitService implements CommandLineRunner {
    
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public void run(String... args) {
        createTestAppointments();
    }

    private void createTestAppointments() {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findByUser_Email("doctor@gmail.com");
            Optional<Patient> patientOpt = patientRepository.findByUser_Email("patient@gmail.com");
            
            if (doctorOpt.isPresent() && patientOpt.isPresent()) {
                Doctor doctor = doctorOpt.get();
                Patient patient = patientOpt.get();
                
                // Check if appointments already exist
                if (appointmentRepository.findByDoctor_Id(doctor.getId()).isEmpty()) {
                    // Create a pending appointment
                    Appointment pendingAppt = Appointment.builder()
                            .doctor(doctor)
                            .patient(patient)
                            .startTime(OffsetDateTime.now().plusDays(1).withHour(10).withMinute(0))
                            .endTime(OffsetDateTime.now().plusDays(1).withHour(11).withMinute(0))
                            .status(Appointment.Status.PENDING)
                            .notes("Test pending appointment")
                            .build();
                    
                    // Create a today's appointment
                    Appointment todayAppt = Appointment.builder()
                            .doctor(doctor)
                            .patient(patient)
                            .startTime(OffsetDateTime.now().withHour(14).withMinute(0))
                            .endTime(OffsetDateTime.now().withHour(15).withMinute(0))
                            .status(Appointment.Status.SCHEDULED)
                            .notes("Test today's appointment")
                            .build();
                    
                    appointmentRepository.save(pendingAppt);
                    appointmentRepository.save(todayAppt);
                    
                    System.out.println("Created test appointments for doctor dashboard");
                }
            }
        } catch (Exception e) {
            System.out.println("Could not create test appointments: " + e.getMessage());
        }
    }
}
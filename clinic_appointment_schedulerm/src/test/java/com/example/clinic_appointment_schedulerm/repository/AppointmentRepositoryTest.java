package com.example.clinic_appointment_schedulerm.repository;

import com.example.clinic_appointment_schedulerm.entity.Appointment;
import com.example.clinic_appointment_schedulerm.entity.Doctor;
import com.example.clinic_appointment_schedulerm.entity.Patient;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AppointmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Doctor doctor;
    private Patient patient;

    @BeforeEach
    void setUp() {
        UserAccount doctorUser = UserAccount.builder()
                .email("doctor@example.com")
                .password("password")
                .fullName("Dr. Smith")
                .phone("1234567890")
                .role(UserAccount.UserRole.DOCTOR)
                .build();
        entityManager.persist(doctorUser);

        UserAccount patientUser = UserAccount.builder()
                .email("patient@example.com")
                .password("password")
                .fullName("John Doe")
                .phone("9876543210")
                .role(UserAccount.UserRole.PATIENT)
                .build();
        entityManager.persist(patientUser);

        doctor = Doctor.builder()
                .user(doctorUser)
                .specialization("Cardiology")
                .build();
        entityManager.persist(doctor);

        patient = Patient.builder()
                .user(patientUser)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender("Male")
                .build();
        entityManager.persist(patient);

        entityManager.flush();
    }

    @Test
    void testFindByDoctorId() {
        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plusHours(2))
                .status(Appointment.Status.PENDING)
                .build();
        entityManager.persistAndFlush(appointment);

        List<Appointment> appointments = appointmentRepository.findByDoctor_Id(doctor.getId());

        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        assertEquals(doctor.getId(), appointments.get(0).getDoctor().getId());
    }

    @Test
    void testFindByPatientId() {
        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plusHours(2))
                .status(Appointment.Status.SCHEDULED)
                .build();
        entityManager.persistAndFlush(appointment);

        List<Appointment> appointments = appointmentRepository.findByPatient_Id(patient.getId());

        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        assertEquals(patient.getId(), appointments.get(0).getPatient().getId());
    }

    @Test
    void testFindWithDoctorAndPatientAndUsersByDoctorAndStatus() {
        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plusHours(2))
                .status(Appointment.Status.PENDING)
                .build();
        entityManager.persistAndFlush(appointment);

        List<Appointment> appointments = appointmentRepository.findWithDoctorAndPatientAndUsersByDoctorAndStatus(doctor.getId(), Appointment.Status.PENDING);

        assertNotNull(appointments);
        assertEquals(1, appointments.size());
        assertEquals(Appointment.Status.PENDING, appointments.get(0).getStatus());
    }

    @Test
    void testExistsByDoctorAndTimeOverlap() {
        OffsetDateTime startTime = OffsetDateTime.now();
        OffsetDateTime endTime = startTime.plusHours(2);
        
        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .startTime(startTime)
                .endTime(endTime)
                .status(Appointment.Status.SCHEDULED)
                .build();
        entityManager.persistAndFlush(appointment);

        boolean exists = appointmentRepository.existsByDoctorAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                doctor, endTime, startTime);

        assertTrue(exists);
    }
}
package com.example.clinic_appointment_schedulerm.repository;

import com.example.clinic_appointment_schedulerm.entity.Appointment;
import com.example.clinic_appointment_schedulerm.entity.Doctor;
import com.example.clinic_appointment_schedulerm.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctor_Id(Long doctorId);
    List<Appointment> findByPatient_Id(Long patientId);
    List<Appointment> findByDoctor_IdAndStartTimeBetween(Long doctorId, OffsetDateTime from, OffsetDateTime to);
    List<Appointment> findByPatient_IdAndStartTimeBetween(Long patientId, OffsetDateTime from, OffsetDateTime to);

    boolean existsByDoctorAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Doctor doctor, OffsetDateTime end, OffsetDateTime start);
    boolean existsByPatientAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Patient patient, OffsetDateTime end, OffsetDateTime start);
    
    List<Appointment> findByDoctor_IdAndStartTimeBetweenOrderByStartTime(Long doctorId, OffsetDateTime from, OffsetDateTime to);
    List<Appointment> findByDoctor_IdAndStatusOrderByStartTimeDesc(Long doctorId, Appointment.Status status);

    // Eagerly fetch doctor->user and patient->user for serialization safety
    @Query("""
           select a from Appointment a
           join fetch a.doctor d
           join fetch d.user
           join fetch a.patient p
           join fetch p.user
           where d.id = :doctorId and a.startTime between :from and :to
           order by a.startTime
           """)
    List<Appointment> findWithDoctorAndPatientAndUsersByDoctorAndStartBetween(Long doctorId, OffsetDateTime from, OffsetDateTime to);

    @Query("""
           select a from Appointment a
           join fetch a.doctor d
           join fetch d.user
           join fetch a.patient p
           join fetch p.user
           where d.id = :doctorId and a.startTime between :from and :to and a.status = :status
           order by a.startTime
           """)
    List<Appointment> findWithDoctorAndPatientAndUsersByDoctorAndStartBetweenAndStatus(Long doctorId, OffsetDateTime from, OffsetDateTime to, Appointment.Status status);

    @Query("""
           select a from Appointment a
           join fetch a.doctor d
           join fetch d.user
           join fetch a.patient p
           join fetch p.user
           where d.id = :doctorId and a.status = :status
           order by a.startTime desc
           """)
    List<Appointment> findWithDoctorAndPatientAndUsersByDoctorAndStatus(Long doctorId, Appointment.Status status);

    @Query("""
           select a from Appointment a
           join fetch a.doctor d
           join fetch d.user
           join fetch a.patient p
           join fetch p.user
           where d.id = :doctorId
           order by a.startTime desc
           """)
    List<Appointment> findAllWithDoctorAndPatientAndUsersByDoctor(Long doctorId);

    @Query("""
           select a from Appointment a
           join fetch a.doctor d
           join fetch d.user
           join fetch a.patient p
           join fetch p.user
           where p.id = :patientId
           order by a.startTime desc
           """)
    List<Appointment> findAllWithDoctorAndPatientAndUsersByPatient(Long patientId);

    List<Appointment> findByStatus(Appointment.Status status);
    
    void deleteByPatientId(Long patientId);
    void deleteByDoctorId(Long doctorId);
    
    @Query("""
           select a from Appointment a
           join fetch a.doctor d
           join fetch d.user
           join fetch a.patient p
           join fetch p.user
           order by a.startTime desc
           """)
    List<Appointment> findAllWithDoctorAndPatientAndUsers();
}
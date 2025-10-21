package com.example.clinic_appointment_schedulerm.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDashboardResponse {
    private Long doctorId;
    private String fullName;
    private String specialization;
    private String status;
    private int todaysAppointments;
    private int pendingAppointments;
    private int completedAppointments;
}
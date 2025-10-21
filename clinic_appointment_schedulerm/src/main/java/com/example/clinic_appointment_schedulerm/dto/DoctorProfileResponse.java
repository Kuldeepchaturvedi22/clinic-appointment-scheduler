package com.example.clinic_appointment_schedulerm.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorProfileResponse {
    private Long id;
    private String specialization;
    private String email;
    private String fullName;
    private String phone;
}
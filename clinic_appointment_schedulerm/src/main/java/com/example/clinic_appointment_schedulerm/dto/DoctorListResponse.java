package com.example.clinic_appointment_schedulerm.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorListResponse {
    private Long id;
    private String fullName;
    private String specialization;
    private String email;
    private String phone;
    private Double averageRating;
}
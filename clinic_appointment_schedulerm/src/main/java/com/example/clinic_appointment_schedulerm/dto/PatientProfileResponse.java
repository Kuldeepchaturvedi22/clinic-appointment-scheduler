package com.example.clinic_appointment_schedulerm.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientProfileResponse {
    // Patient part
    private Long id;
    private LocalDate dateOfBirth;
    private String gender;

    // User part
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String role;
}
package com.example.clinic_appointment_schedulerm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    @NotBlank
    private String fullName;
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String phone;
    
    private LocalDate dateOfBirth;
    private String gender;
    private String specialization; // For doctors only
}
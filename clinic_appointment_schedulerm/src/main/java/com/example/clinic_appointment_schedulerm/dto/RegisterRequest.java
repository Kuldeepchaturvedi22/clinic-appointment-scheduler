package com.example.clinic_appointment_schedulerm.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email @NotBlank
    private String email;
    @NotBlank @Size(min = 8, max = 100)
    private String password;
    @NotBlank
    private String fullName;
    @Pattern(regexp = "^\\+?[0-9]{7,15}$")
    private String phone;

    // For Patient
    private String dateOfBirth; // ISO yyyy-MM-dd
    private String gender;

    // For Doctor
    private String specialization;
}
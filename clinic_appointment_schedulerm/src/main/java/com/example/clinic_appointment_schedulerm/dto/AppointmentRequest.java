package com.example.clinic_appointment_schedulerm.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AppointmentRequest {
    @NotNull
    private Long doctorId;
    @NotNull
    private Long patientId;
    @NotNull
    private OffsetDateTime startTime;
    @NotNull
    private OffsetDateTime endTime;
    private String notes;
}
package com.example.clinic_appointment_schedulerm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "help_chats")
public class HelpChat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String senderEmail;

    @Column(nullable = false)
    private String senderType; // PATIENT, DOCTOR, ADMIN

    @Column(nullable = false)
    private String message;

    @Column
    private String recipientEmail; // For admin replies - which user this reply is for

    @Column(nullable = false)
    private OffsetDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        sentAt = OffsetDateTime.now();
    }
}
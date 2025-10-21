package com.example.clinic_appointment_schedulerm.repository;

import com.example.clinic_appointment_schedulerm.entity.HelpChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HelpChatRepository extends JpaRepository<HelpChat, Long> {
    List<HelpChat> findAllByOrderBySentAtAsc();
    
    @Query("SELECT h FROM HelpChat h WHERE h.senderEmail = :userEmail OR (h.senderEmail = 'admin@gmail.com' AND h.recipientEmail = :userEmail) ORDER BY h.sentAt ASC")
    List<HelpChat> findBySenderEmailOrAdminOrderBySentAtAsc(@Param("userEmail") String userEmail);
}
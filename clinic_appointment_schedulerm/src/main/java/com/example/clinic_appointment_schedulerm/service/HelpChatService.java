package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.entity.HelpChat;
import com.example.clinic_appointment_schedulerm.repository.HelpChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HelpChatService {
    
    private final HelpChatRepository helpChatRepository;


    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMessages(String userEmail) {
        List<HelpChat> messages;
        if ("admin@gmail.com".equals(userEmail)) {
            messages = helpChatRepository.findAllByOrderBySentAtAsc();
        } else {
            messages = helpChatRepository.findBySenderEmailOrAdminOrderBySentAtAsc(userEmail);
        }
        
        return messages.stream()
                .map(msg -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", msg.getId());
                    m.put("senderEmail", msg.getSenderEmail());
                    m.put("senderType", msg.getSenderType());
                    m.put("message", msg.getMessage());
                    m.put("sentAt", msg.getSentAt());
                    return m;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getConversationWithUser(String userEmail) {
        return helpChatRepository.findBySenderEmailOrAdminOrderBySentAtAsc(userEmail)
                .stream()
                .map(msg -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", msg.getId());
                    m.put("senderEmail", msg.getSenderEmail());
                    m.put("senderType", msg.getSenderType());
                    m.put("message", msg.getMessage());
                    m.put("sentAt", msg.getSentAt());
                    return m;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllUserEmails() {
        return helpChatRepository.findAllByOrderBySentAtAsc()
                .stream()
                .filter(msg -> !"ADMIN".equals(msg.getSenderType()))
                .map(HelpChat::getSenderEmail)
                .distinct()
                .collect(Collectors.toList());
    }


    @Transactional
    public void sendMessage(String message, String userEmail, String recipientEmail) {
        String senderType = "admin@gmail.com".equals(userEmail) ? "ADMIN" : "PATIENT";
        
        HelpChat helpChat = HelpChat.builder()
            .senderEmail(userEmail)
            .senderType(senderType)
            .message(message)
            .recipientEmail(recipientEmail)
            .build();
            
        helpChatRepository.save(helpChat);
    }
}
package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.service.HelpChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/help-chat")
@RequiredArgsConstructor
public class HelpChatController {
    
    private final HelpChatService helpChatService;
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getMessages(Authentication auth) {
        return ResponseEntity.ok(helpChatService.getMessages(auth.getName()));
    }
    
    @GetMapping("/conversation/{userEmail}")
    public ResponseEntity<List<Map<String, Object>>> getConversation(@PathVariable String userEmail, Authentication auth) {
        if (!"admin@gmail.com".equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(helpChatService.getConversationWithUser(userEmail));
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<String>> getAllUsers(Authentication auth) {
        if (!"admin@gmail.com".equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(helpChatService.getAllUserEmails());
    }
    
    @PostMapping
    public ResponseEntity<Void> sendMessage(@RequestBody Map<String, String> request, Authentication auth) {
        String message = request.get("message");
        String recipientEmail = request.get("recipientEmail");
        helpChatService.sendMessage(message, auth.getName(), recipientEmail);
        return ResponseEntity.ok().build();
    }
}
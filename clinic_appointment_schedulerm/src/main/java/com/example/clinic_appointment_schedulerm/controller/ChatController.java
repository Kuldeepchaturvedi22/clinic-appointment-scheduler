package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.dto.SendMessageRequest;
import com.example.clinic_appointment_schedulerm.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;


    @GetMapping("/appointment/{appointmentId}")
    public List<ChatService.ChatDto> getMessages(@PathVariable Long appointmentId, Principal principal) {
        return chatService.getMessages(appointmentId, principal.getName());
    }

    @PostMapping("/appointment/{appointmentId}")
    public ChatService.ChatDto sendMessage(@PathVariable Long appointmentId, @RequestBody SendMessageRequest request, Principal principal) {
        return chatService.sendMessage(appointmentId, request.getMessage(), principal.getName());
    }
}
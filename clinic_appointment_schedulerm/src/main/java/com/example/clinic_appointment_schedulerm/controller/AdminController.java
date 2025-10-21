package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
    
    @DeleteMapping("/users/{type}/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String type, @PathVariable Long id) {
        adminService.deleteUser(type, id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/users/{type}/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable String type, @PathVariable Long id, @RequestBody Map<String, String> updates) {
        adminService.updateUser(type, id, updates);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/appointments")
    public ResponseEntity<List<Map<String, Object>>> getAllAppointments() {
        return ResponseEntity.ok(adminService.getAllAppointments());
    }
    
    @GetMapping("/ratings")
    public ResponseEntity<List<Map<String, Object>>> getAllRatings() {
        return ResponseEntity.ok(adminService.getAllRatings());
    }
}
package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    
    private final RatingService ratingService;
    
    @PostMapping("/appointment/{appointmentId}")
    public ResponseEntity<Void> rateDoctor(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, Object> request,
            Authentication auth) {
        Integer stars = (Integer) request.get("stars");
        String comment = (String) request.get("comment");
        ratingService.rateDoctor(appointmentId, stars, comment, auth.getName());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getDoctorRatings(@PathVariable Long doctorId) {
        return ResponseEntity.ok(ratingService.getDoctorRatings(doctorId));
    }
    
    @PutMapping("/{ratingId}")
    public ResponseEntity<Void> updateRating(
            @PathVariable Long ratingId,
            @RequestBody Map<String, Object> request,
            Authentication auth) {
        Integer stars = (Integer) request.get("stars");
        String comment = (String) request.get("comment");
        ratingService.updateRating(ratingId, stars, comment, auth.getName());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long ratingId, Authentication auth) {
        ratingService.deleteRating(ratingId, auth.getName());
        return ResponseEntity.ok().build();
    }
}
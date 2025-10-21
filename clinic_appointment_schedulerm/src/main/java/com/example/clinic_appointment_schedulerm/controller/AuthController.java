package com.example.clinic_appointment_schedulerm.controller;

import com.example.clinic_appointment_schedulerm.dto.*;
import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import com.example.clinic_appointment_schedulerm.security.JwtService;
import com.example.clinic_appointment_schedulerm.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/register-patient")
    public ResponseEntity<MessageResponse> registerPatient(@Valid @RequestBody RegisterRequest req) {
        UserAccount user = authService.registerPatient(req);
        return ResponseEntity.ok(new MessageResponse(
                "You are registered with user id: " + user.getId(),
                user.getId()
        ));
    }


    @PostMapping("/register-doctor")
    public ResponseEntity<MessageResponse> registerDoctor(@Valid @RequestBody RegisterRequest req) {
        UserAccount user = authService.registerDoctor(req);
        return ResponseEntity.ok(new MessageResponse(
                "You are registered with user id: " + user.getId(),
                user.getId()
        ));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        // Handle admin login with hardcoded credentials
        if ("admin@gmail.com".equals(req.getEmail()) && "admin123".equals(req.getPassword())) {
            String token = jwtService.generateToken(req.getEmail(), Map.of("role", "ADMIN"));
            log.info("Admin logged in");
            return ResponseEntity.ok(new AuthResponse(token, "ADMIN", null));
        }
        
        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
            UserDetails ud = (UserDetails) auth.getPrincipal();
            // role extraction from authorities
            String role = ud.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            String token = jwtService.generateToken(ud.getUsername(), Map.of("role", role));
            log.info("User logged in email={}", ud.getUsername());
            return ResponseEntity.ok(new AuthResponse(token, role, null));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("You are not registered. Please register first.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Stateless JWT: client should discard token. Endpoint provided for symmetry/audit.
        log.info("Logout requested");
        return ResponseEntity.noContent().build();
    }
}
package com.example.clinic_appointment_schedulerm.service;

import com.example.clinic_appointment_schedulerm.entity.UserAccount;
import com.example.clinic_appointment_schedulerm.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserAccountRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Handle admin user
        if ("admin@gmail.com".equals(email)) {
            return new org.springframework.security.core.userdetails.User(
                    "admin@gmail.com",
                    "admin123", // This won't be used for validation since admin login bypasses normal auth
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }
        
        UserAccount ua = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                ua.getEmail(),
                ua.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + ua.getRole().name()))
        );
    }
}
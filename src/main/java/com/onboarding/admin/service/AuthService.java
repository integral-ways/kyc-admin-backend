package com.onboarding.admin.service;

import com.onboarding.admin.dto.LoginRequest;
import com.onboarding.admin.dto.LoginResponse;
import com.onboarding.admin.entity.AdminUser;
import com.onboarding.admin.repository.AdminUserRepository;
import com.onboarding.admin.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AdminUserRepository adminUserRepository;
    private final AdminUserDetailsService userDetailsService;
    
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        String token = jwtUtil.generateToken(request.getUsername());
        
        AdminUser adminUser = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        adminUser.setLastLogin(Instant.now());
        adminUserRepository.save(adminUser);
        
        Set<String> permissions = userDetailsService.getUserPermissions(request.getUsername());
        
        return new LoginResponse(
            token,
            adminUser.getUsername(),
            adminUser.getEmail(),
            adminUser.getFullName(),
            permissions
        );
    }
}

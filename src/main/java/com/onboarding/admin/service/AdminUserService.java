package com.onboarding.admin.service;

import com.onboarding.admin.dto.AdminUserDto;
import com.onboarding.admin.dto.CreateAdminUserRequest;
import com.onboarding.admin.entity.AdminUser;
import com.onboarding.admin.entity.Profile;
import com.onboarding.admin.repository.AdminUserRepository;
import com.onboarding.admin.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    
    private final AdminUserRepository adminUserRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<AdminUserDto> getAllUsers() {
        return adminUserRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public AdminUserDto getUserById(String id) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toDto(user);
    }
    
    @Transactional
    public AdminUserDto createUser(CreateAdminUserRequest request) {
        if (adminUserRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (adminUserRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        AdminUser user = new AdminUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setActive(true);
        
        if (request.getProfileIds() != null && !request.getProfileIds().isEmpty()) {
            Set<Profile> profiles = new HashSet<>();
            for (String profileId : request.getProfileIds()) {
                Profile profile = profileRepository.findById(profileId)
                        .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
                profiles.add(profile);
            }
            user.setProfiles(profiles);
        }
        
        return toDto(adminUserRepository.save(user));
    }
    
    @Transactional
    public AdminUserDto toggleUserStatus(String id, boolean active) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(active);
        return toDto(adminUserRepository.save(user));
    }
    
    @Transactional
    public void deleteUser(String id) {
        if (!adminUserRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        adminUserRepository.deleteById(id);
    }
    
    private AdminUserDto toDto(AdminUser user) {
        AdminUserDto dto = new AdminUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setActive(user.isActive());
        dto.setProfiles(user.getProfiles().stream()
                .map(Profile::getName)
                .collect(Collectors.toSet()));
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        return dto;
    }
}

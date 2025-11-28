package com.onboarding.admin.service;

import com.onboarding.admin.dto.ProfileDto;
import com.onboarding.admin.entity.Permission;
import com.onboarding.admin.entity.Profile;
import com.onboarding.admin.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {
    
    private final ProfileRepository profileRepository;
    
    public List<ProfileDto> getAllProfiles() {
        return profileRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public ProfileDto getProfileById(String id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return toDto(profile);
    }
    
    private ProfileDto toDto(Profile profile) {
        ProfileDto dto = new ProfileDto();
        dto.setId(profile.getId());
        dto.setName(profile.getName());
        dto.setDescription(profile.getDescription());
        dto.setActive(profile.isActive());
        dto.setPermissions(profile.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet()));
        return dto;
    }
}

package com.onboarding.admin.controller;

import com.onboarding.admin.dto.ProfileDto;
import com.onboarding.admin.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class ProfileController {
    
    private final ProfileService profileService;
    
    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_PROFILES')")
    public ResponseEntity<List<ProfileDto>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_PROFILES')")
    public ResponseEntity<ProfileDto> getProfileById(@PathVariable String id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }
}

package com.onboarding.admin.controller;

import com.onboarding.admin.dto.KycApplicationDto;
import com.onboarding.admin.service.KycApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class KycApplicationController {
    
    private final KycApplicationService applicationService;
    
    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_APPLICATIONS')")
    public ResponseEntity<Page<KycApplicationDto>> getAllApplications(Pageable pageable) {
        return ResponseEntity.ok(applicationService.getAllApplications(pageable));
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('VIEW_APPLICATIONS')")
    public ResponseEntity<Page<KycApplicationDto>> getApplicationsByStatus(
            @PathVariable String status, Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplicationsByStatus(status, pageable));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_APPLICATIONS')")
    public ResponseEntity<KycApplicationDto> getApplicationById(@PathVariable String id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('REVIEW_APPLICATIONS')")
    public ResponseEntity<KycApplicationDto> updateStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam(required = false) String reviewNotes) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, status, reviewNotes));
    }
    
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('ASSIGN_APPLICATIONS')")
    public ResponseEntity<KycApplicationDto> assignApplication(
            @PathVariable String id,
            @RequestParam String assignedTo) {
        return ResponseEntity.ok(applicationService.assignApplication(id, assignedTo));
    }
}

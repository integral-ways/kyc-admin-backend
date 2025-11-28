package com.onboarding.admin.controller;

import com.onboarding.admin.entity.AuditLog;
import com.onboarding.admin.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AuditLogController {
    
    private final AuditLogService auditLogService;
    
    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Page<AuditLog>> getAllLogs(Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getAllLogs(pageable));
    }
    
    @GetMapping("/resource/{resource}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Page<AuditLog>> getLogsByResource(
            @PathVariable String resource, Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getLogsByResource(resource, pageable));
    }
    
    @GetMapping("/resource-id/{resourceId}")
    @PreAuthorize("hasAuthority('VIEW_APPLICATIONS')")
    public ResponseEntity<Page<AuditLog>> getLogsByResourceId(
            @PathVariable String resourceId, Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getLogsByResourceId(resourceId, pageable));
    }
}

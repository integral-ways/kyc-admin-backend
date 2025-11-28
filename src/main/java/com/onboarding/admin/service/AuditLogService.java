package com.onboarding.admin.service;

import com.onboarding.admin.entity.AuditLog;
import com.onboarding.admin.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    public void log(String action, String resource, String resourceId, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setResource(resource);
        log.setResourceId(resourceId);
        log.setDetails(details);
        
        if (auth != null && auth.isAuthenticated()) {
            log.setUsername(auth.getName());
        }
        
        auditLogRepository.save(log);
    }
    
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
    
    public Page<AuditLog> getLogsByResource(String resource, Pageable pageable) {
        return auditLogRepository.findByResource(resource, pageable);
    }
    
    public Page<AuditLog> getLogsByResourceId(String resourceId, Pageable pageable) {
        return auditLogRepository.findByResourceId(resourceId, pageable);
    }
}

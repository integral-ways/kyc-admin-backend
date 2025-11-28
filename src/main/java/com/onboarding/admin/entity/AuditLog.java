package com.onboarding.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String userId;
    private String username;
    private String action;
    private String resource;
    private String resourceId;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    private String ipAddress;
    private Instant timestamp = Instant.now();
}

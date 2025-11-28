package com.onboarding.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "application_reviews")
public class ApplicationReview {
    @Id
    private String applicationId; // References customer.id from KYC database
    
    private String assignedTo;
    
    @Column(columnDefinition = "TEXT")
    private String reviewNotes;
    
    private Instant reviewedAt;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}

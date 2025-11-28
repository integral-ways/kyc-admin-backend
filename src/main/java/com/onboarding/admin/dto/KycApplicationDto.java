package com.onboarding.admin.dto;

import lombok.Data;
import java.time.Instant;

import com.onboarding.admin.entity.kyc.ApplicationStatus;
import com.onboarding.admin.entity.kyc.EntityType;

@Data
public class KycApplicationDto {
	private String id;
	private String userId;
	private String mobileNumber;
	private String email;
	private String fullName;
	private ApplicationStatus applicationStatus;
	private EntityType entityType;
	private Integer currentStep;
	private Double completionPercentage;
	private String assignedTo;
	private String reviewNotes;
	private Instant reviewedAt;
	private Instant createdAt;
	private Instant updatedAt;
}

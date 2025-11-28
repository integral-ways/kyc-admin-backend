package com.onboarding.admin.entity.kyc;

import java.time.Instant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "customers")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

// login fields
	@Column(unique = true)
	private String idNumber;

	@Column(unique = true)
	private String mobile;

	@Column(unique = true)
	private String username;


// Party fields
	@Enumerated(EnumType.STRING)
	private PartyType partyType;

	@Enumerated(EnumType.STRING)
	private PartyStatus partyStatus;

	@Enumerated(EnumType.STRING)
	private EntityType entityType;

	@Enumerated(EnumType.STRING)
	private ApplicationStatus applicationStatus;

	private String accountNumber;

	private Instant createdAt = Instant.now();

	

// current step
	private Integer currentStep = 1;

// risk flags
	private boolean highRisk;
	private String highRiskReason;

// One-to-one relationships for step entities (EAGER for admin to access data)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private PersonalInfo personalInfo;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private EmploymentInfo employmentInfo;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private GeneralInfo generalInfo;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private FatcaInfo fatcaInfo;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private BankInfo bankInfo;

	// Helper methods for admin display
	public String getFullName() {
		if (personalInfo != null) {
			return String
					.format("%s %s %s %s", personalInfo.getFirstName() != null ? personalInfo.getFirstName() : "",
							personalInfo.getSecondName() != null ? personalInfo.getSecondName() : "",
							personalInfo.getLastName() != null ? personalInfo.getLastName() : "",
							personalInfo.getFamilyName() != null ? personalInfo.getFamilyName() : "")
					.trim().replaceAll("\\s+", " ");
		}
		return username;
	}

	public String getEmail() {
		// Email might be in contact info or other location - adjust as needed
		return null;
	}

	public String getMobileNumber() {
		return mobile;
	}

	public Double getCompletionPercentage() {
		int totalSteps = 7;
		return (currentStep / (double) totalSteps) * 100.0;
	}


	public Instant getUpdatedAt() {
		return createdAt; // Can be enhanced with actual update tracking
	}

	public String getUserId() {
		return id;
	}
}

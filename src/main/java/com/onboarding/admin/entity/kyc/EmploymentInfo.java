package com.onboarding.admin.entity.kyc;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class EmploymentInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	private String customerId;
	private String employmentType; // EMPLOYED / SELF_EMPLOYED / UNEMPLOYED / RETIRED / STUDENT
	private String employerName;
	private String jobTitle;
	private String employmentYears; // Range: 0-1, 1-3, 3-5, 5-10, 10-15, 15-20, 20+
}

package com.onboarding.admin.entity.kyc;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
@Entity
public class GeneralInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	private String customerId;
	private String investmentExperience;
	private String investmentKnowledge;
	private String riskTolerance;

	@Lob
	private String answersJson;

	@Lob
	private String proofsJson;
}

package com.onboarding.admin.entity.kyc;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Data
@Entity
public class PersonalInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

// system values - stored but not exposed to frontend as editable
	private String partyId;
	private String customerId;
	private String partyTypeSystem;
	private String partyStatusSystem;
	private String cmaStatus;
	private boolean highRisk;
	private String highRiskReason;

// Personal fields
	private String title;
	private String firstName;
	private String secondName;
	private String lastName;
	private String familyName;
	private String fullNameAr; // Arabic full name from Nafath
	private String gender; // Male/Female from Nafath
	private String birthDateHijri; // Hijri birth date from Nafath
	private String birthDateGregorian; // Gregorian birth date from Nafath
	private String educationLevel; // Education level
	private String maritalStatus; // Marital status
	private Integer numOfDependents;

	private String approximateAnnualIncome; // Approximate annual income range (e.g., "100000-200000")
	private String approximateNetWorth; // Approximate net worth range (e.g., "500000-1000000")

	@ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
	@CollectionTable(name = "personal_info_income_sources", joinColumns = @JoinColumn(name = "personal_info_id"))
	@Column(name = "income_source")
	private List<String> incomeSources;

// getters/setters omitted
}

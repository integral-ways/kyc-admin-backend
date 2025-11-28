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

// system values
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
	private Integer numOfDependents;
	private Long netWorth;
	private Long netWorthGrowth;
	
	@ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
	@CollectionTable(name = "personal_info_income_sources", joinColumns = @JoinColumn(name = "personal_info_id"))
	@Column(name = "income_source")
	private List<String> incomeSources;

// Address
	private String country;
	private String city;
	private String district;
	private String street;
	private String buildingNumber;

// Contact
	private String primaryContact;
	private String altMobile;
	private String countryCode;
}

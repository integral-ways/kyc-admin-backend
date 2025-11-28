package com.onboarding.admin.entity.kyc;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(name = "nafath_user_profiles")
@NoArgsConstructor
@AllArgsConstructor
public class NafathUserProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(unique = true, nullable = false, length = 10)
	private String nationalId; // 10-digit Saudi National ID or Iqama

	@Column(nullable = false)
	private String idType; // NATIONAL_ID or IQAMA

	@Column(nullable = false)
	private String fullNameEn;

	@Column(nullable = false)
	private String fullNameAr;

	@Column(nullable = false)
	private String gender; // Male or Female

	@Column(nullable = false)
	private String birthDateHijri;

	@Column(nullable = false)
	private String birthDateGregorian;

	@Column(nullable = false, length = 10)
	private String mobile; // 10-digit mobile number

	// Address Information
	@Column(nullable = false)
	private String country;

	@Column(nullable = false)
	private String region;

	@Column(nullable = false)
	private String city;

	private String district;

	private String street;

	private String buildingNumber;

	private String additionalNumber;

	private String postalCode;

	private String unitNumber;

	// Metadata
	@Column(nullable = false)
	private Boolean isActive = true;

	@Column(nullable = false)
	private Instant createdAt = Instant.now();

	private Instant updatedAt;

	private String notes;

	private String status;
	private String requestId;
	private String transId;
	private String token;

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = Instant.now();
	}
}

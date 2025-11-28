package com.onboarding.admin.entity.kyc;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contact_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {

	@Id
	private String id = UUID.randomUUID().toString();

	@Column(nullable = false)
	private String customerId;

	private String primaryContact; // readonly, same mobile used during login
	private String altMobile;
	private String countryCode;
	private String email; // Email address
	private String preferredLanguage; // Preferred language (en, ar)
}

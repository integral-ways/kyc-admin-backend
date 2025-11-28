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
@Table(name = "address_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressInfo {

	@Id
	private String id = UUID.randomUUID().toString();

	@Column(nullable = false)
	private String customerId;

	private String country;
	private String city;
	private String district;
	private String street;
	private String buildingNumber;
}

package com.onboarding.admin.entity.kyc;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class TaxResidency {
	private String country;
	private String taxResidencyTin;
	private String noTinReason;
}

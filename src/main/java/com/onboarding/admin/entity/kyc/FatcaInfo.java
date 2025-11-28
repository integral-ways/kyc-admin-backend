package com.onboarding.admin.entity.kyc;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class FatcaInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	private String customerId;
	
	// US Status
	private boolean usCitizen;
	private boolean usTaxResident;
	private String usTin;
	
	
	// Tax Residency
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "fatca_tax_residencies", joinColumns = @JoinColumn(name = "fatca_info_id"))
	private List<TaxResidency> taxResidency = new ArrayList<>();
	
	// Certifications
	private boolean certifyInformationCorrect;
	private boolean agreeToNotifyChanges;
	private String certificationDate;
}

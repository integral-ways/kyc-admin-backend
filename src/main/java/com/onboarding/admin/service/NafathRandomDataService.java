package com.onboarding.admin.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.onboarding.admin.entity.kyc.NafathUserProfile;
import com.onboarding.admin.repository.kyc.NafathUserProfileRepository;

import org.springframework.transaction.annotation.Transactional;  // ✔ CORRECT

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NafathRandomDataService {

	private final NafathUserProfileRepository repository;
	private final Random random = new Random();

	// Saudi cities
	private static final String[] CITIES = { "Riyadh", "Jeddah", "Mecca", "Medina", "Dammam", "Khobar", "Dhahran",
			"Taif", "Tabuk", "Buraidah" };

	// Saudi regions
	private static final String[] REGIONS = { "Riyadh", "Makkah", "Madinah", "Eastern Province", "Asir", "Tabuk",
			"Qassim", "Hail" };

	// Arabic first names
	private static final String[] ARABIC_FIRST_NAMES = { "أحمد", "محمد", "عبدالله", "علي", "خالد", "سعد", "فهد",
			"عبدالعزيز", "فاطمة", "عائشة", "خديجة", "مريم", "نورة", "سارة", "هند", "لطيفة" };

	// Arabic family names
	private static final String[] ARABIC_FAMILY_NAMES = { "العتيبي", "الدوسري", "القحطاني", "الغامدي", "الشمري",
			"المطيري", "العنزي", "الحربي", "الزهراني", "الشهري" };

	// English first names
	private static final String[] ENGLISH_FIRST_NAMES = { "Ahmed", "Mohammed", "Abdullah", "Ali", "Khalid", "Saad",
			"Fahad", "Abdulaziz", "Fatima", "Aisha", "Khadija", "Maryam", "Noura", "Sarah", "Hind", "Latifa" };

	// English family names
	private static final String[] ENGLISH_FAMILY_NAMES = { "Al-Otaibi", "Al-Dosari", "Al-Qahtani", "Al-Ghamdi",
			"Al-Shammari", "Al-Mutairi", "Al-Anazi", "Al-Harbi", "Al-Zahrani", "Al-Shahri" };

	// Districts
	private static final String[] DISTRICTS = { "Olaya", "Al Malqa", "Al Nakheel", "Al Wurud", "Al Hamra", "Al Rawdah",
			"Al Sulaymaniyah", "Al Malaz", "Al Naseem" };

	// Streets
	private static final String[] STREETS = { "King Fahd Road", "King Abdullah Road", "Olaya Street", "Tahlia Street",
			"Prince Mohammed Bin Abdulaziz Road", "Al Urubah Road", "Makkah Road" };

	/**
	 * Generate a valid Saudi National ID or Iqama number Format: 1XXXXXXXXX
	 * (National ID starts with 1) or 2XXXXXXXXX (Iqama starts with 2)
	 */
	public String generateValidNationalId(String idType) {
		String prefix = idType.equals("NATIONAL_ID") ? "1" : "2";
		StringBuilder id = new StringBuilder(prefix);

		// Generate 9 random digits
		for (int i = 0; i < 9; i++) {
			id.append(random.nextInt(10));
		}

		return id.toString();
	}

	/**
	 * Generate a random mobile number (10 digits starting with 5)
	 */
	public String generateMobileNumber() {
		StringBuilder mobile = new StringBuilder("5");
		for (int i = 0; i < 9; i++) {
			mobile.append(random.nextInt(10));
		}
		return mobile.toString();
	}

	/**
	 * Generate a random birth date
	 */
	public LocalDate generateBirthDate() {
		int year = 1970 + random.nextInt(35); // Between 1970 and 2005
		int month = 1 + random.nextInt(12);
		int day = 1 + random.nextInt(28); // Safe day range
		return LocalDate.of(year, month, day);
	}

	/**
	 * Convert Gregorian date to approximate Hijri date (simplified)
	 */
	public String convertToHijri(LocalDate gregorianDate) {
		// Simplified conversion: Hijri year ≈ Gregorian year - 579
		int hijriYear = gregorianDate.getYear() - 579;
		return String.format("%d-%02d-%02d", hijriYear, gregorianDate.getMonthValue(), gregorianDate.getDayOfMonth());
	}

	/**
	 * Generate a complete random Nafath user profile
	 */
	public NafathUserProfile generateRandomProfile(String idType) {
		NafathUserProfile profile = new NafathUserProfile();

		// Generate unique national ID
		String nationalId;
		do {
			nationalId = generateValidNationalId(idType);
		} while (repository.existsByNationalId(nationalId));

		profile.setNationalId(nationalId);
		profile.setIdType(idType);

		// Generate name
		boolean isMale = random.nextBoolean();
		String gender = isMale ? "Male" : "Female";
		profile.setGender(gender);

		String arabicFirstName = ARABIC_FIRST_NAMES[random.nextInt(ARABIC_FIRST_NAMES.length)];
		String arabicFamilyName = ARABIC_FAMILY_NAMES[random.nextInt(ARABIC_FAMILY_NAMES.length)];
		profile.setFullNameAr(arabicFirstName + " " + arabicFamilyName);

		String englishFirstName = ENGLISH_FIRST_NAMES[random.nextInt(ENGLISH_FIRST_NAMES.length)];
		String englishFamilyName = ENGLISH_FAMILY_NAMES[random.nextInt(ENGLISH_FAMILY_NAMES.length)];
		profile.setFullNameEn(englishFirstName + " " + englishFamilyName);

		// Generate birth date
		LocalDate birthDate = generateBirthDate();
		profile.setBirthDateGregorian(birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
		profile.setBirthDateHijri(convertToHijri(birthDate));

		// Generate mobile
		profile.setMobile(generateMobileNumber());

		// Generate address
		profile.setCountry("Saudi Arabia");
		profile.setRegion(REGIONS[random.nextInt(REGIONS.length)]);
		profile.setCity(CITIES[random.nextInt(CITIES.length)]);
		profile.setDistrict(DISTRICTS[random.nextInt(DISTRICTS.length)]);
		profile.setStreet(STREETS[random.nextInt(STREETS.length)]);
		profile.setBuildingNumber(String.valueOf(1000 + random.nextInt(9000)));
		profile.setAdditionalNumber(String.valueOf(1000 + random.nextInt(9000)));
		profile.setPostalCode(String.valueOf(10000 + random.nextInt(90000)));
		profile.setUnitNumber(String.valueOf(1 + random.nextInt(20)));

		profile.setIsActive(true);

		return repository.save(profile);
	}

	/**
	 * Get all profiles
	 */
	public Page<NafathUserProfile> getAllProfiles(Pageable pageable) {
		return repository.findAll(pageable);
	}


	/**
	 * Get profile by national ID
	 */
	public NafathUserProfile getProfileByNationalId(String nationalId) {
		return repository.findByNationalId(nationalId)
				.orElseThrow(() -> new RuntimeException("Profile not found with national ID: " + nationalId));
	}

	/**
	 * Update profile
	 */
	public NafathUserProfile updateProfile(String id, NafathUserProfile updatedProfile) {
		NafathUserProfile profile = repository.findByNationalId(id)
				.orElseThrow(() -> new RuntimeException("Profile not found with ID: " + id));

		// Update fields
		profile.setFullNameEn(updatedProfile.getFullNameEn());
		profile.setFullNameAr(updatedProfile.getFullNameAr());
		profile.setGender(updatedProfile.getGender());
		profile.setBirthDateHijri(updatedProfile.getBirthDateHijri());
		profile.setBirthDateGregorian(updatedProfile.getBirthDateGregorian());
		profile.setMobile(updatedProfile.getMobile());
		profile.setCountry(updatedProfile.getCountry());
		profile.setRegion(updatedProfile.getRegion());
		profile.setCity(updatedProfile.getCity());
		profile.setDistrict(updatedProfile.getDistrict());
		profile.setStreet(updatedProfile.getStreet());
		profile.setBuildingNumber(updatedProfile.getBuildingNumber());
		profile.setAdditionalNumber(updatedProfile.getAdditionalNumber());
		profile.setPostalCode(updatedProfile.getPostalCode());
		profile.setUnitNumber(updatedProfile.getUnitNumber());
		profile.setIsActive(updatedProfile.getIsActive());
		profile.setNotes(updatedProfile.getNotes());
		profile.setStatus(updatedProfile.getStatus());

		return repository.save(profile);
	}

	/**
	 * Delete profile
	 */
	@Transactional("kycTransactionManager")
	public void deleteProfile(String id) {
		repository.deleteByNationalId(id);
	}

	/**
	 * Convert entity to DTO for Nafath simulation
	 */
	public NafathUserProfile convertToDTO(NafathUserProfile entity) {
		NafathUserProfile dto = new NafathUserProfile();
		BeanUtils.copyProperties(entity, dto);
		dto.setNationalId(entity.getNationalId());
		dto.setFullNameEn(entity.getFullNameEn());
		dto.setFullNameAr(entity.getFullNameAr());
		dto.setGender(entity.getGender());
		dto.setBirthDateHijri(entity.getBirthDateHijri());
		dto.setBirthDateGregorian(entity.getBirthDateGregorian());
		dto.setMobile("966" + entity.getMobile()); // Add country code

		// Create address
		dto.setRegion(entity.getRegion());
		dto.setCity(entity.getCity());
		dto.setDistrict(entity.getDistrict());
		dto.setStreet(entity.getStreet());
		dto.setAdditionalNumber(entity.getAdditionalNumber());
		dto.setPostalCode(entity.getPostalCode());
		dto.setUnitNumber(entity.getUnitNumber());
		
		

		return dto;
	}

	/**
	 * Simulate Nafath authentication
	 */
	public NafathUserProfile simulateNafathAuth(String nationalId) {
		NafathUserProfile entity = repository.findByNationalId(nationalId)
				.orElseThrow(() -> new RuntimeException("No simulated profile found for national ID: " + nationalId));

		if (!entity.getIsActive()) {
			throw new RuntimeException("Profile is inactive");
		}

		return convertToDTO(entity);
	}
}

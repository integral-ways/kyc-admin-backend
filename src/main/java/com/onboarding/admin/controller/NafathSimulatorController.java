package com.onboarding.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onboarding.admin.entity.kyc.NafathUserProfile;
import com.onboarding.admin.service.NafathRandomDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/nafath-simulator")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class NafathSimulatorController {

	private final NafathRandomDataService simulatorService;

	/**
	 * Generate a random Nafath user profile
	 */
	@PostMapping("/generate")
	public ResponseEntity<NafathUserProfile> generateProfile(
			@RequestParam(defaultValue = "NATIONAL_ID") String idType) {
		NafathUserProfile profile = simulatorService.generateRandomProfile(idType);
		return ResponseEntity.ok(profile);
	}

	/**
	 * Generate multiple profiles
	 */
	@PostMapping("/generate-bulk")
	public ResponseEntity<List<NafathUserProfile>> generateBulkProfiles(@RequestParam(defaultValue = "5") int count,
			@RequestParam(defaultValue = "NATIONAL_ID") String idType) {

		List<NafathUserProfile> profiles = new java.util.ArrayList<>();
		for (int i = 0; i < count && i < 50; i++) { // Max 50 at once
			profiles.add(simulatorService.generateRandomProfile(idType));
		}
		return ResponseEntity.ok(profiles);
	}

	/**
	 * Get all profiles
	 */
	@GetMapping
	public ResponseEntity<Page<NafathUserProfile>> getAllProfiles(Pageable pageable) {
		return ResponseEntity.ok(simulatorService.getAllProfiles(pageable));
	}

	/**
	 * Get profile by ID
	 */
	@GetMapping("/{id}")
	public ResponseEntity<NafathUserProfile> getProfile(@PathVariable String id) {
		return ResponseEntity.ok(simulatorService.getProfileByNationalId(id));
	}

	/**
	 * Update profile
	 */
	@PutMapping("/{id}")
	public ResponseEntity<NafathUserProfile> updateProfile(@PathVariable String id,
			@RequestBody NafathUserProfile profile) {
		return ResponseEntity.ok(simulatorService.updateProfile(id, profile));
	}

	/**
	 * Delete profile
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteProfile(@PathVariable String id) {
		simulatorService.deleteProfile(id);
		return ResponseEntity.ok(Map.of("message", "Profile deleted successfully"));
	}

	/**
	 * Simulate Nafath authentication (for testing)
	 */
	@PostMapping("/simulate-auth")
	public ResponseEntity<NafathUserProfile> simulateAuth(@RequestParam String nationalId) {
		return ResponseEntity.ok(simulatorService.simulateNafathAuth(nationalId));
	}
}

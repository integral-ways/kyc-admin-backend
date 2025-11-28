package com.onboarding.admin.repository.kyc;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.onboarding.admin.entity.kyc.NafathUserProfile;

@Repository
public interface NafathUserProfileRepository extends JpaRepository<NafathUserProfile, String> {

	Optional<NafathUserProfile> findByNationalId(String nationalId);

	boolean existsByNationalId(String nationalId);

	List<NafathUserProfile> findByIdType(String idType);

	void deleteByNationalId(String NationalId);
}

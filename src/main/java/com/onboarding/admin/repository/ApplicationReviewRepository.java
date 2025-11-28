package com.onboarding.admin.repository;

import com.onboarding.admin.entity.ApplicationReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationReviewRepository extends JpaRepository<ApplicationReview, String> {
    Optional<ApplicationReview> findByApplicationId(String applicationId);
}

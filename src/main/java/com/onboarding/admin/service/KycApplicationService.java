package com.onboarding.admin.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboarding.admin.dto.KycApplicationDto;
import com.onboarding.admin.entity.ApplicationReview;
import com.onboarding.admin.entity.kyc.Customer;
import com.onboarding.admin.repository.ApplicationReviewRepository;
import com.onboarding.admin.repository.kyc.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KycApplicationService {
    
    private final CustomerRepository customerRepository;
    private final ApplicationReviewRepository reviewRepository;
    private final AuditLogService auditLogService;
    
    public Page<KycApplicationDto> getAllApplications(Pageable pageable) {
        // Read from KYC database (customers table)
        return customerRepository.findAll(pageable).map(this::customerToDto);
    }
    
    public Page<KycApplicationDto> getApplicationsByStatus(String status, Pageable pageable) {
        // Filter by calculated status
        List<Customer> allCustomers = customerRepository.findAll();
        List<Customer> filtered = allCustomers.stream()
                .filter(customer -> customer.getApplicationStatus().equals(status))
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        
        List<KycApplicationDto> pageContent = filtered.subList(start, end).stream()
                .map(this::customerToDto)
                .toList();
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filtered.size());
    }
    
    public KycApplicationDto getApplicationById(String id) {
        // Try to get from KYC database first
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        return customerToDto(customer);
    }
    
    @Transactional("adminTransactionManager")
    public KycApplicationDto updateApplicationStatus(String id, String status, String reviewNotes) {
        // Get customer from KYC database
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Get or create review record in admin database
        ApplicationReview review = reviewRepository.findByApplicationId(id)
                .orElseGet(() -> {
                    ApplicationReview newReview = new ApplicationReview();
                    newReview.setApplicationId(id);
                    return newReview;
                });
        
        review.setReviewNotes(reviewNotes);
        review.setReviewedAt(Instant.now());
        review.setUpdatedAt(Instant.now());
        
        reviewRepository.save(review);
        
        auditLogService.log(
            "UPDATE_STATUS",
            "KYC_APPLICATION",
            id,
            String.format("Status updated to %s with notes", status)
        );
        
        return customerToDto(customer);
    }
    
    @Transactional("adminTransactionManager")
    public KycApplicationDto assignApplication(String id, String assignedTo) {
        // Get customer from KYC database
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Get or create review record in admin database
        ApplicationReview review = reviewRepository.findByApplicationId(id)
                .orElseGet(() -> {
                    ApplicationReview newReview = new ApplicationReview();
                    newReview.setApplicationId(id);
                    return newReview;
                });
        
        String oldAssignee = review.getAssignedTo();
        review.setAssignedTo(assignedTo);
        review.setUpdatedAt(Instant.now());
        
        reviewRepository.save(review);
        
        auditLogService.log(
            "ASSIGN",
            "KYC_APPLICATION",
            id,
            String.format("Assigned from %s to %s", oldAssignee, assignedTo)
        );
        
        return customerToDto(customer);
    }
    
    private KycApplicationDto customerToDto(Customer customer) {
        KycApplicationDto dto = new KycApplicationDto();
        dto.setId(customer.getId());
        dto.setUserId(customer.getUserId());
        dto.setMobileNumber(customer.getMobileNumber());
        dto.setEmail(customer.getEmail());
        dto.setFullName(customer.getFullName());
        dto.setApplicationStatus(customer.getApplicationStatus());
        dto.setEntityType(customer.getEntityType());
        dto.setCurrentStep(customer.getCurrentStep());
        dto.setCompletionPercentage(customer.getCompletionPercentage());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        
        // Get review data from admin database if exists
        reviewRepository.findByApplicationId(customer.getId()).ifPresent(review -> {
            dto.setAssignedTo(review.getAssignedTo());
            dto.setReviewNotes(review.getReviewNotes());
            dto.setReviewedAt(review.getReviewedAt());
        });
        
        return dto;
    }
}

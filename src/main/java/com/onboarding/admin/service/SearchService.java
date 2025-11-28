package com.onboarding.admin.service;

import com.onboarding.admin.dto.KycApplicationDto;
import com.onboarding.admin.dto.SearchRequest;
import com.onboarding.admin.entity.ApplicationReview;
import com.onboarding.admin.entity.kyc.Customer;
import com.onboarding.admin.repository.ApplicationReviewRepository;
import com.onboarding.admin.repository.kyc.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    
    private final CustomerRepository customerRepository;
    private final ApplicationReviewRepository reviewRepository;
    
    public Page<KycApplicationDto> searchApplications(SearchRequest request, Pageable pageable) {
        // Read from KYC database
        List<Customer> allCustomers = customerRepository.findAll();
        
        // Get all reviews for filtering by assignee
        Map<String, ApplicationReview> reviewMap = reviewRepository.findAll().stream()
                .collect(Collectors.toMap(ApplicationReview::getApplicationId, review -> review));
        
        // Filter customers
        List<Customer> filtered = allCustomers.stream()
                .filter(customer -> matchesQuery(customer, request.getQuery()))
                .filter(customer -> matchesStatus(customer, request.getStatus()))
                .filter(customer -> matchesType(customer, request.getOnboardingType()))
                .filter(customer -> matchesAssignee(customer, request.getAssignedTo(), reviewMap))
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        
        List<KycApplicationDto> pageContent = filtered.subList(start, end).stream()
                .map(customer -> customerToDto(customer, reviewMap.get(customer.getId())))
                .collect(Collectors.toList());
        
        return new PageImpl<>(pageContent, pageable, filtered.size());
    }
    
    private boolean matchesQuery(Customer customer, String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }
        String lowerQuery = query.toLowerCase();
        return (customer.getFullName() != null && customer.getFullName().toLowerCase().contains(lowerQuery)) ||
               (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(lowerQuery)) ||
               (customer.getMobileNumber() != null && customer.getMobileNumber().contains(query)) ||
               (customer.getId() != null && customer.getId().toLowerCase().contains(lowerQuery));
    }
    
    private boolean matchesStatus(Customer customer, String status) {
        return status == null || status.trim().isEmpty() || customer.getApplicationStatus().equals(status);
    }
    
    private boolean matchesType(Customer customer, String type) {
        return type == null || type.trim().isEmpty() || customer.getEntityType().equals(type);
    }
    
    private boolean matchesAssignee(Customer customer, String assignee, Map<String, ApplicationReview> reviewMap) {
        if (assignee == null || assignee.trim().isEmpty()) {
            return true;
        }
        ApplicationReview review = reviewMap.get(customer.getId());
        return review != null && review.getAssignedTo() != null && review.getAssignedTo().equals(assignee);
    }
    
    private KycApplicationDto customerToDto(Customer customer, ApplicationReview review) {
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
        
        // Add review data if exists
        if (review != null) {
            dto.setAssignedTo(review.getAssignedTo());
            dto.setReviewNotes(review.getReviewNotes());
            dto.setReviewedAt(review.getReviewedAt());
        }
        
        return dto;
    }
}

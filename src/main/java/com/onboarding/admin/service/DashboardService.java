package com.onboarding.admin.service;

import com.onboarding.admin.dto.DashboardStatsDto;
import com.onboarding.admin.entity.kyc.Customer;
import com.onboarding.admin.repository.kyc.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final CustomerRepository customerRepository;
    
    public DashboardStatsDto getStats() {
        // Read from KYC database
        List<Customer> allCustomers = customerRepository.findAll();
        
        long total = allCustomers.size();
        long submitted = countByStatus(allCustomers, "SUBMITTED");
        long underReview = countByStatus(allCustomers, "UNDER_REVIEW");
        long approved = countByStatus(allCustomers, "APPROVED");
        long rejected = countByStatus(allCustomers, "REJECTED");
        long draft = countByStatus(allCustomers, "DRAFT");
        long pendingInfo = countByStatus(allCustomers, "PENDING_INFO");
        
        Instant now = Instant.now();
        Instant startOfDay = now.truncatedTo(ChronoUnit.DAYS);
        Instant startOfWeek = now.minus(7, ChronoUnit.DAYS);
        Instant startOfMonth = now.minus(30, ChronoUnit.DAYS);
        
        long todaySubmissions = countSubmissionsAfter(allCustomers, startOfDay);
        long weekSubmissions = countSubmissionsAfter(allCustomers, startOfWeek);
        long monthSubmissions = countSubmissionsAfter(allCustomers, startOfMonth);
        
        return new DashboardStatsDto(
            total, submitted, underReview, approved, rejected, draft, pendingInfo,
            todaySubmissions, weekSubmissions, monthSubmissions
        );
    }
    
    public List<?> getActiveWidgets() {
        // Placeholder for future widget functionality
        return List.of();
    }
    
    private long countByStatus(List<Customer> customers, String status) {
        return customers.stream()
                .filter(customer -> customer.getApplicationStatus().equals(status))
                .count();
    }
    
    private long countSubmissionsAfter(List<Customer> customers, Instant after) {
        return customers.stream()
                .filter(customer -> customer.getCreatedAt() != null && customer.getCreatedAt().isAfter(after))
                .count();
    }
}

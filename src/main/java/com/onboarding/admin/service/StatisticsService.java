package com.onboarding.admin.service;

import com.onboarding.admin.dto.StatisticsDto;
import com.onboarding.admin.entity.kyc.Customer;
import com.onboarding.admin.repository.kyc.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final CustomerRepository customerRepository;
    
    public StatisticsDto getStatistics() {
        // Read from KYC database
        List<Customer> allCustomers = customerRepository.findAll();
        
        Map<String, Long> statusDistribution = allCustomers.stream()
                .collect(Collectors.groupingBy(
                        customer -> customer.getApplicationStatus().toString(),
                        Collectors.counting()
                ));
        
        Map<String, Long> typeDistribution = allCustomers.stream()
                .collect(Collectors.groupingBy(
                        customer -> customer.getEntityType().toString(),
                        Collectors.counting()
                ));
        
        Map<String, Long> dailySubmissions = getDailySubmissions(allCustomers, 7);
        Map<String, Long> monthlySubmissions = getMonthlySubmissions(allCustomers, 6);
        
        Double avgCompletion = allCustomers.stream()
                .mapToDouble(customer -> customer.getCompletionPercentage() != null ? customer.getCompletionPercentage() : 0.0)
                .average()
                .orElse(0.0);
        
        return new StatisticsDto(
                statusDistribution,
                typeDistribution,
                dailySubmissions,
                monthlySubmissions,
                avgCompletion,
                (long) allCustomers.size()
        );
    }
    
    private Map<String, Long> getDailySubmissions(List<Customer> customers, int days) {
        Map<String, Long> result = new HashMap<>();
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = days - 1; i >= 0; i--) {
            Instant dayStart = now.minus(i, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
            Instant dayEnd = dayStart.plus(1, ChronoUnit.DAYS);
            
            LocalDate date = LocalDate.ofInstant(dayStart, ZoneId.systemDefault());
            String dateStr = date.format(formatter);
            
            long count = customers.stream()
                    .filter(customer -> customer.getCreatedAt() != null)
                    .filter(customer -> !customer.getCreatedAt().isBefore(dayStart) && customer.getCreatedAt().isBefore(dayEnd))
                    .count();
            
            result.put(dateStr, count);
        }
        
        return result;
    }
    
    private Map<String, Long> getMonthlySubmissions(List<Customer> customers, int months) {
        Map<String, Long> result = new HashMap<>();
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (int i = months - 1; i >= 0; i--) {
            Instant monthStart = now.minus(i * 30L, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
            Instant monthEnd = monthStart.plus(30, ChronoUnit.DAYS);
            
            LocalDate date = LocalDate.ofInstant(monthStart, ZoneId.systemDefault());
            String monthStr = date.format(formatter);
            
            long count = customers.stream()
                    .filter(customer -> customer.getCreatedAt() != null)
                    .filter(customer -> !customer.getCreatedAt().isBefore(monthStart) && customer.getCreatedAt().isBefore(monthEnd))
                    .count();
            
            result.put(monthStr, count);
        }
        
        return result;
    }
}

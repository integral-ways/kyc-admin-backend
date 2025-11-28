package com.onboarding.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDto {
    private Map<String, Long> statusDistribution;
    private Map<String, Long> typeDistribution;
    private Map<String, Long> dailySubmissions;
    private Map<String, Long> monthlySubmissions;
    private Double averageCompletionPercentage;
    private Long totalApplications;
}

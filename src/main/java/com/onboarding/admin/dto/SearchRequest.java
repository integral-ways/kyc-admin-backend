package com.onboarding.admin.dto;

import lombok.Data;

@Data
public class SearchRequest {
    private String query;
    private String status;
    private String onboardingType;
    private String assignedTo;
    private String dateFrom;
    private String dateTo;
}

package com.onboarding.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDto {
    private long totalApplications;
    private long submittedApplications;
    private long underReviewApplications;
    private long approvedApplications;
    private long rejectedApplications;
    private long draftApplications;
    private long pendingInfoApplications;
    private long todaySubmissions;
    private long weekSubmissions;
    private long monthSubmissions;
}

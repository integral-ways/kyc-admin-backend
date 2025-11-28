package com.onboarding.admin.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dashboard_widgets")
public class DashboardWidget {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String title;
    private String type; // STAT, CHART, TABLE
    private String query;
    private String chartType; // LINE, BAR, PIE, DOUGHNUT
    private Integer position;
    private Integer width; // 1-12 (Bootstrap columns)
    private boolean active = true;
    
    @Column(columnDefinition = "TEXT")
    private String config; // JSON configuration
}

package com.onboarding.admin.controller;

import com.onboarding.admin.dto.DashboardStatsDto;
import com.onboarding.admin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
    
    @GetMapping("/widgets")
    public ResponseEntity<?> getWidgets() {
        return ResponseEntity.ok(dashboardService.getActiveWidgets());
    }
}

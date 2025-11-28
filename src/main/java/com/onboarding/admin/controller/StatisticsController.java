package com.onboarding.admin.controller;

import com.onboarding.admin.dto.StatisticsDto;
import com.onboarding.admin.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_APPLICATIONS')")
    public ResponseEntity<StatisticsDto> getStatistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}

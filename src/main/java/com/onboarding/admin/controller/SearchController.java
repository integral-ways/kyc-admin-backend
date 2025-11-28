package com.onboarding.admin.controller;

import com.onboarding.admin.dto.KycApplicationDto;
import com.onboarding.admin.dto.SearchRequest;
import com.onboarding.admin.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class SearchController {
    
    private final SearchService searchService;
    
    @PostMapping("/applications")
    @PreAuthorize("hasAuthority('VIEW_APPLICATIONS')")
    public ResponseEntity<Page<KycApplicationDto>> searchApplications(
            @RequestBody SearchRequest request, Pageable pageable) {
        return ResponseEntity.ok(searchService.searchApplications(request, pageable));
    }
}

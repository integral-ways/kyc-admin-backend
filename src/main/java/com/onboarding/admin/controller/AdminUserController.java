package com.onboarding.admin.controller;

import com.onboarding.admin.dto.AdminUserDto;
import com.onboarding.admin.dto.CreateAdminUserRequest;
import com.onboarding.admin.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AdminUserController {
    
    private final AdminUserService adminUserService;
    
    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<List<AdminUserDto>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<AdminUserDto> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(adminUserService.getUserById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<AdminUserDto> createUser(@Valid @RequestBody CreateAdminUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminUserService.createUser(request));
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<AdminUserDto> activateUser(@PathVariable String id) {
        return ResponseEntity.ok(adminUserService.toggleUserStatus(id, true));
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<AdminUserDto> deactivateUser(@PathVariable String id) {
        return ResponseEntity.ok(adminUserService.toggleUserStatus(id, false));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

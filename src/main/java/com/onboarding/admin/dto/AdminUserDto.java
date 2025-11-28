package com.onboarding.admin.dto;

import lombok.Data;
import java.time.Instant;
import java.util.Set;

@Data
public class AdminUserDto {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private boolean active;
    private Set<String> profiles;
    private Instant createdAt;
    private Instant lastLogin;
}

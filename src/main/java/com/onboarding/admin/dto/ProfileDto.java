package com.onboarding.admin.dto;

import lombok.Data;
import java.util.Set;

@Data
public class ProfileDto {
    private String id;
    private String name;
    private String description;
    private boolean active;
    private Set<String> permissions;
}

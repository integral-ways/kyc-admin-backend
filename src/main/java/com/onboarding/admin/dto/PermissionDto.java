package com.onboarding.admin.dto;

import lombok.Data;

@Data
public class PermissionDto {
    private String id;
    private String name;
    private String description;
    private String resource;
    private String action;
}

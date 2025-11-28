package com.onboarding.admin.config;

import com.onboarding.admin.entity.AdminUser;
import com.onboarding.admin.entity.Permission;
import com.onboarding.admin.entity.Profile;
import com.onboarding.admin.repository.AdminUserRepository;
import com.onboarding.admin.repository.PermissionRepository;
import com.onboarding.admin.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final PermissionRepository permissionRepository;
    private final ProfileRepository profileRepository;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        if (permissionRepository.count() == 0) {
            initializePermissions();
            initializeProfiles();
            initializeAdminUser();
        }
    }
    
    private void initializePermissions() {
        String[][] permissions = {
            {"VIEW_APPLICATIONS", "View KYC applications", "applications", "read"},
            {"REVIEW_APPLICATIONS", "Review and update KYC applications", "applications", "update"},
            {"ASSIGN_APPLICATIONS", "Assign applications to reviewers", "applications", "assign"},
            {"MANAGE_USERS", "Manage admin users", "users", "manage"},
            {"MANAGE_PROFILES", "Manage profiles and permissions", "profiles", "manage"}
        };
        
        for (String[] perm : permissions) {
            Permission permission = new Permission();
            permission.setName(perm[0]);
            permission.setDescription(perm[1]);
            permission.setResource(perm[2]);
            permission.setAction(perm[3]);
            permissionRepository.save(permission);
        }
    }
    
    private void initializeProfiles() {
        Profile adminProfile = new Profile();
        adminProfile.setName("ADMIN");
        adminProfile.setDescription("Full system access");
        adminProfile.setPermissions(new HashSet<>(permissionRepository.findAll()));
        profileRepository.save(adminProfile);
        
        Profile reviewerProfile = new Profile();
        reviewerProfile.setName("REVIEWER");
        reviewerProfile.setDescription("Can review and update applications");
        reviewerProfile.setPermissions(new HashSet<>(Arrays.asList(
            permissionRepository.findByName("VIEW_APPLICATIONS").orElseThrow(),
            permissionRepository.findByName("REVIEW_APPLICATIONS").orElseThrow()
        )));
        profileRepository.save(reviewerProfile);
    }
    
    private void initializeAdminUser() {
        AdminUser admin = new AdminUser();
        admin.setUsername("admin");
        admin.setEmail("admin@kyc.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setFullName("System Administrator");
        admin.setProfiles(new HashSet<>(Arrays.asList(
            profileRepository.findByName("ADMIN").orElseThrow()
        )));
        adminUserRepository.save(admin);
    }
}

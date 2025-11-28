package com.onboarding.admin.service;

import com.onboarding.admin.entity.AdminUser;
import com.onboarding.admin.entity.Permission;
import com.onboarding.admin.entity.Profile;
import com.onboarding.admin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {
    
    private final AdminUserRepository adminUserRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        if (!adminUser.isActive()) {
            throw new UsernameNotFoundException("User is inactive: " + username);
        }
        
        Set<GrantedAuthority> authorities = getAuthorities(adminUser);
        
        return User.builder()
                .username(adminUser.getUsername())
                .password(adminUser.getPasswordHash())
                .authorities(authorities)
                .build();
    }
    
    private Set<GrantedAuthority> getAuthorities(AdminUser adminUser) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        for (Profile profile : adminUser.getProfiles()) {
            for (Permission permission : profile.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        
        return authorities;
    }
    
    public Set<String> getUserPermissions(String username) {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return adminUser.getProfiles().stream()
                .flatMap(profile -> profile.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}

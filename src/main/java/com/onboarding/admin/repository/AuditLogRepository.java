package com.onboarding.admin.repository;

import com.onboarding.admin.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
    Page<AuditLog> findByUserId(String userId, Pageable pageable);
    Page<AuditLog> findByResource(String resource, Pageable pageable);
    Page<AuditLog> findByResourceId(String resourceId, Pageable pageable);
}

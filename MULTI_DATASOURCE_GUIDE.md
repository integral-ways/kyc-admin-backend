# Multi-Datasource Configuration Guide

## Overview

The KYC Admin Backend now supports **two separate databases**:

1. **Admin Database** (`kycadmindb`) - Stores admin-specific data
   - Admin users
   - Profiles and permissions
   - Audit logs
   - Application review notes and assignments

2. **KYC Database** (`kycdb`) - Reads KYC application data
   - Customer/application data from main KYC system
   - Read-only access to customer table

## Architecture

```
┌─────────────────────────────────────┐
│   KYC Admin Backend (Port 8081)    │
├─────────────────────────────────────┤
│                                     │
│  ┌──────────────┐  ┌─────────────┐ │
│  │ Admin Data   │  │  KYC Data   │ │
│  │ Source       │  │  Source     │ │
│  └──────┬───────┘  └──────┬──────┘ │
│         │                 │         │
└─────────┼─────────────────┼─────────┘
          │                 │
          ▼                 ▼
   ┌─────────────┐   ┌─────────────┐
   │ Admin DB    │   │  KYC DB     │
   │ (H2/MySQL)  │   │ (H2/MySQL)  │
   └─────────────┘   └─────────────┘
```

## Configuration

### application.yml

```yaml
spring:
  datasource:
    # Admin datasource
    admin:
      jdbc-url: jdbc:h2:mem:kycadmindb
      driver-class-name: org.h2.Driver
      username: admin
      password: admin
    
    # KYC datasource (connects to main KYC database)
    kyc:
      jdbc-url: jdbc:h2:tcp://localhost:9092/mem:kycdb
      driver-class-name: org.h2.Driver
      username: sa
      password: password
```

### For Production (MySQL/PostgreSQL)

```yaml
spring:
  datasource:
    admin:
      jdbc-url: jdbc:mysql://localhost:3306/kyc_admin
      driver-class-name: com.mysql.cj.jdbc.Driver
      username: admin_user
      password: ${ADMIN_DB_PASSWORD}
    
    kyc:
      jdbc-url: jdbc:mysql://localhost:3306/kyc_main
      driver-class-name: com.mysql.cj.jdbc.Driver
      username: kyc_readonly_user
      password: ${KYC_DB_PASSWORD}
```

## Data Source Configurations

### 1. AdminDataSourceConfig

**Purpose**: Manages admin-specific data

**Packages**:
- Entities: `com.onboarding.admin.entity` (excluding `entity.kyc`)
- Repositories: `com.onboarding.admin.repository` (excluding `repository.kyc`)

**Entities**:
- AdminUser
- Profile
- Permission
- KycApplication (for review notes and assignments)
- AuditLog

**Transaction Manager**: `adminTransactionManager` (Primary)

### 2. KycDataSourceConfig

**Purpose**: Reads KYC application data

**Packages**:
- Entities: `com.onboarding.admin.entity.kyc`
- Repositories: `com.onboarding.admin.repository.kyc`

**Entities**:
- Customer (maps to `customers` table in KYC database)

**Transaction Manager**: `kycTransactionManager`

## How It Works

### Reading Applications

1. **List Applications**: Reads from `customers` table in KYC database
2. **Get Application Details**: Reads from `customers` table
3. **Display**: Converts `Customer` entity to `KycApplicationDto`

```java
// In KycApplicationService
public Page<KycApplicationDto> getAllApplications(Pageable pageable) {
    // Reads from KYC database
    return customerRepository.findAll(pageable).map(this::customerToDto);
}
```

### Updating Application Status

1. **Read**: Gets customer data from KYC database
2. **Create/Update**: Creates or updates record in Admin database
3. **Store**: Saves review notes and assignment in Admin database
4. **Audit**: Logs action in Admin database

```java
@Transactional("adminTransactionManager")
public KycApplicationDto updateApplicationStatus(String id, ApplicationStatus status, String reviewNotes) {
    // Get or create application record in admin database
    KycApplication application = applicationRepository.findById(id)
            .orElseGet(() -> createFromCustomer(id));
    
    // Update and save in admin database
    application.setStatus(status);
    application.setReviewNotes(reviewNotes);
    return toDto(applicationRepository.save(application));
}
```

## Database Tables

### Admin Database Tables

```sql
-- Admin users
CREATE TABLE admin_users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    last_login TIMESTAMP
);

-- Profiles
CREATE TABLE profiles (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    active BOOLEAN DEFAULT TRUE
);

-- Permissions
CREATE TABLE permissions (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    resource VARCHAR(50),
    action VARCHAR(50)
);

-- KYC Applications (admin data only)
CREATE TABLE kyc_applications (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    mobile_number VARCHAR(20),
    email VARCHAR(100),
    full_name VARCHAR(100),
    status VARCHAR(20),
    onboarding_type VARCHAR(20),
    current_step INT,
    completion_percentage DOUBLE,
    assigned_to VARCHAR(50),
    review_notes TEXT,
    submitted_at TIMESTAMP,
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Audit logs
CREATE TABLE audit_logs (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    username VARCHAR(50),
    action VARCHAR(50),
    resource VARCHAR(50),
    resource_id VARCHAR(36),
    details TEXT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP
);
```

### KYC Database Tables (Read-Only)

```sql
-- Customers (from main KYC system)
CREATE TABLE customers (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    mobile_number VARCHAR(20),
    email VARCHAR(100),
    full_name VARCHAR(100),
    status VARCHAR(20),
    onboarding_type VARCHAR(20),
    current_step INT,
    completion_percentage DOUBLE,
    submitted_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

## Benefits

### 1. Separation of Concerns
- Admin data separate from application data
- Clear boundaries between systems
- Independent scaling

### 2. Read-Only Access to KYC Data
- Admin system doesn't modify main KYC data
- Safer operations
- Audit trail in separate database

### 3. Flexible Deployment
- Can use different database types
- Can scale independently
- Can backup separately

### 4. Security
- Different credentials for each database
- Can restrict admin database access
- Read-only user for KYC database

## Migration from Single Database

If you're migrating from a single database setup:

1. **Backup existing data**
2. **Update application.yml** with two datasources
3. **Run the application** - it will create admin tables automatically
4. **Verify** both databases are accessible
5. **Test** application listing and updates

## Connecting to Existing KYC Database

### H2 Database (Development)

Start the main KYC application with H2 in server mode:

```yaml
# In main KYC application
spring:
  datasource:
    url: jdbc:h2:tcp://localhost:9092/mem:kycdb
```

### MySQL (Production)

```yaml
spring:
  datasource:
    kyc:
      jdbc-url: jdbc:mysql://kyc-db-server:3306/kyc_production
      username: kyc_readonly
      password: ${KYC_DB_PASSWORD}
```

Create a read-only user:

```sql
CREATE USER 'kyc_readonly'@'%' IDENTIFIED BY 'secure_password';
GRANT SELECT ON kyc_production.customers TO 'kyc_readonly'@'%';
FLUSH PRIVILEGES;
```

## Troubleshooting

### Connection Issues

**Problem**: Can't connect to KYC database

**Solution**:
1. Verify KYC database is running
2. Check connection URL and port
3. Verify credentials
4. Check firewall rules

### Entity Not Found

**Problem**: `Customer` entity not found

**Solution**:
1. Verify `customers` table exists in KYC database
2. Check table name matches entity annotation
3. Verify column names match

### Transaction Issues

**Problem**: Transaction errors

**Solution**:
1. Use correct transaction manager annotation
2. For admin operations: `@Transactional("adminTransactionManager")`
3. For KYC read operations: No transaction needed (read-only)

## Performance Considerations

### Connection Pooling

Both datasources use HikariCP with default settings. For production:

```yaml
spring:
  datasource:
    admin:
      hikari:
        maximum-pool-size: 10
        minimum-idle: 5
    kyc:
      hikari:
        maximum-pool-size: 20
        minimum-idle: 10
```

### Caching

Consider caching frequently accessed KYC data:

```java
@Cacheable("customers")
public Customer getCustomerById(String id) {
    return customerRepository.findById(id).orElseThrow();
}
```

## Testing

### Unit Tests

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ContextConfiguration(classes = {AdminDataSourceConfig.class})
class AdminRepositoryTest {
    // Test admin repositories
}

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ContextConfiguration(classes = {KycDataSourceConfig.class})
class KycRepositoryTest {
    // Test KYC repositories
}
```

## Summary

✅ **Two separate databases** for admin and KYC data
✅ **Read-only access** to KYC database
✅ **Independent scaling** and management
✅ **Clear separation** of concerns
✅ **Audit trail** in admin database
✅ **Flexible deployment** options

The multi-datasource setup provides a robust, scalable solution for the KYC Admin system while maintaining data integrity and security.

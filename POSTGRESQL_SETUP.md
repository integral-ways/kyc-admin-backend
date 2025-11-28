# PostgreSQL Multi-Database Setup Guide

## Overview

The KYC Admin Backend uses **two separate PostgreSQL databases**:

1. **kyc_admin** - Admin-specific data (users, permissions, audit logs)
2. **kyc** - Main KYC application data (customers/applications)

## Database Configuration

### Current Setup

```yaml
spring:
  datasource:
    admin:
      jdbc-url: jdbc:postgresql://localhost:5432/kyc_admin
      username: postgres
      password: 123456
    
    kyc:
      jdbc-url: jdbc:postgresql://localhost:5432/kyc
      username: postgres
      password: 123456
```

## Step-by-Step Setup

### 1. Create Databases

```bash
# Connect to PostgreSQL
psql -U postgres

# Create admin database
CREATE DATABASE kyc_admin;

# Create KYC database (if not exists)
CREATE DATABASE kyc;

# Verify databases
\l

# Exit
\q
```

### 2. Create Database Users (Optional - Recommended for Production)

```sql
-- Create admin user
CREATE USER kyc_admin_user WITH PASSWORD 'secure_admin_password';
GRANT ALL PRIVILEGES ON DATABASE kyc_admin TO kyc_admin_user;

-- Create read-only user for KYC database
CREATE USER kyc_readonly_user WITH PASSWORD 'secure_readonly_password';
GRANT CONNECT ON DATABASE kyc TO kyc_readonly_user;

-- Grant read permissions on KYC database
\c kyc
GRANT USAGE ON SCHEMA public TO kyc_readonly_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO kyc_readonly_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO kyc_readonly_user;
```

### 3. Update application.yml

**For Development (same user):**
```yaml
spring:
  datasource:
    admin:
      jdbc-url: jdbc:postgresql://localhost:5432/kyc_admin
      driver-class-name: org.postgresql.Driver
      username: postgres
      password: your_password
    
    kyc:
      jdbc-url: jdbc:postgresql://localhost:5432/kyc
      driver-class-name: org.postgresql.Driver
      username: postgres
      password: your_password
```

**For Production (separate users):**
```yaml
spring:
  datasource:
    admin:
      jdbc-url: jdbc:postgresql://localhost:5432/kyc_admin
      driver-class-name: org.postgresql.Driver
      username: kyc_admin_user
      password: ${ADMIN_DB_PASSWORD}
    
    kyc:
      jdbc-url: jdbc:postgresql://localhost:5432/kyc
      driver-class-name: org.postgresql.Driver
      username: kyc_readonly_user
      password: ${KYC_DB_PASSWORD}
```

### 4. Create KYC Database Schema

The `customers` table must exist in the KYC database:

```sql
-- Connect to KYC database
\c kyc

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    mobile_number VARCHAR(20),
    email VARCHAR(100),
    full_name VARCHAR(100),
    status VARCHAR(20),
    onboarding_type VARCHAR(20),
    current_step INTEGER,
    completion_percentage DOUBLE PRECISION,
    submitted_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_customers_status ON customers(status);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_mobile ON customers(mobile_number);
CREATE INDEX idx_customers_created_at ON customers(created_at);
```

### 5. Insert Test Data (Optional)

```sql
-- Insert sample customers
INSERT INTO customers (id, user_id, mobile_number, email, full_name, status, onboarding_type, current_step, completion_percentage, submitted_at, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'user001', '+1234567890', 'john.doe@example.com', 'John Doe', 'SUBMITTED', 'INDIVIDUAL', 5, 80.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440002', 'user002', '+1234567891', 'jane.smith@example.com', 'Jane Smith', 'UNDER_REVIEW', 'INDIVIDUAL', 6, 90.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440003', 'user003', '+1234567892', 'bob.johnson@example.com', 'Bob Johnson', 'APPROVED', 'INDIVIDUAL', 7, 100.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440004', 'user004', '+1234567893', 'alice.williams@example.com', 'Alice Williams', 'DRAFT', 'INDIVIDUAL', 3, 40.0, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440005', 'user005', '+1234567894', 'charlie.brown@example.com', 'Charlie Brown', 'REJECTED', 'INDIVIDUAL', 5, 75.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

### 6. Start the Application

```bash
cd kyc-admin-backend
mvn spring-boot:run
```

## Verification

### 1. Check Database Connections

```bash
# Check admin database
psql -U postgres -d kyc_admin -c "\dt"

# Check KYC database
psql -U postgres -d kyc -c "\dt"
```

### 2. Verify Tables Created

**Admin Database Tables:**
- admin_users
- profiles
- permissions
- profile_permissions
- admin_user_profiles
- kyc_applications (for review notes)
- audit_logs

**KYC Database Tables:**
- customers (must exist before starting)

### 3. Test API Endpoints

```bash
# Login
curl -X POST http://localhost:8081/api/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Get applications (requires token)
curl -X GET http://localhost:8081/api/admin/applications \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Connection Pooling

The configuration includes HikariCP settings:

```yaml
hikari:
  maximum-pool-size: 10      # Max connections
  minimum-idle: 5            # Min idle connections
  connection-timeout: 30000  # 30 seconds
```

Adjust based on your needs:
- **High traffic**: Increase `maximum-pool-size` to 20-50
- **Low resources**: Decrease to 5-10
- **Slow queries**: Increase `connection-timeout`

## Environment Variables (Production)

Use environment variables for sensitive data:

```bash
# Set environment variables
export ADMIN_DB_PASSWORD="your_secure_admin_password"
export KYC_DB_PASSWORD="your_secure_kyc_password"
export JWT_SECRET="your_256_bit_secret_key"

# Run application
mvn spring-boot:run
```

Or use `.env` file:

```bash
# .env
ADMIN_DB_PASSWORD=your_secure_admin_password
KYC_DB_PASSWORD=your_secure_kyc_password
JWT_SECRET=your_256_bit_secret_key
```

Update `application.yml`:

```yaml
spring:
  datasource:
    admin:
      password: ${ADMIN_DB_PASSWORD}
    kyc:
      password: ${KYC_DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
```

## Troubleshooting

### Connection Refused

**Error**: `Connection refused`

**Solution**:
```bash
# Check if PostgreSQL is running
sudo systemctl status postgresql

# Start PostgreSQL
sudo systemctl start postgresql

# Check port
sudo netstat -tulpn | grep 5432
```

### Authentication Failed

**Error**: `password authentication failed`

**Solution**:
```bash
# Reset password
sudo -u postgres psql
ALTER USER postgres PASSWORD 'new_password';
\q

# Update application.yml with new password
```

### Database Does Not Exist

**Error**: `database "kyc_admin" does not exist`

**Solution**:
```bash
# Create database
createdb -U postgres kyc_admin
createdb -U postgres kyc
```

### Table Does Not Exist

**Error**: `relation "customers" does not exist`

**Solution**:
```sql
-- Connect and create table
psql -U postgres -d kyc
-- Run CREATE TABLE statement from step 4
```

### Permission Denied

**Error**: `permission denied for table customers`

**Solution**:
```sql
-- Grant permissions
\c kyc
GRANT SELECT ON customers TO kyc_readonly_user;
```

## Performance Optimization

### 1. Add Indexes

```sql
-- On customers table
CREATE INDEX idx_customers_status ON customers(status);
CREATE INDEX idx_customers_created_at ON customers(created_at);
CREATE INDEX idx_customers_submitted_at ON customers(submitted_at);
```

### 2. Analyze Tables

```sql
-- Update statistics
ANALYZE customers;
ANALYZE admin_users;
```

### 3. Connection Pool Tuning

```yaml
spring:
  datasource:
    admin:
      hikari:
        maximum-pool-size: 20
        minimum-idle: 10
        idle-timeout: 300000
        max-lifetime: 1800000
```

## Backup Strategy

### Daily Backup Script

```bash
#!/bin/bash
# backup.sh

DATE=$(date +%Y%m%d_%H%M%S)

# Backup admin database
pg_dump -U postgres kyc_admin > /backups/kyc_admin_$DATE.sql

# Backup KYC database
pg_dump -U postgres kyc > /backups/kyc_$DATE.sql

# Keep only last 7 days
find /backups -name "*.sql" -mtime +7 -delete
```

### Restore from Backup

```bash
# Restore admin database
psql -U postgres -d kyc_admin < /backups/kyc_admin_20240101.sql

# Restore KYC database
psql -U postgres -d kyc < /backups/kyc_20240101.sql
```

## Monitoring

### Check Active Connections

```sql
-- Admin database
SELECT count(*) FROM pg_stat_activity WHERE datname = 'kyc_admin';

-- KYC database
SELECT count(*) FROM pg_stat_activity WHERE datname = 'kyc';
```

### Check Slow Queries

```sql
-- Enable logging
ALTER DATABASE kyc_admin SET log_min_duration_statement = 1000;

-- View logs
tail -f /var/log/postgresql/postgresql-*.log
```

## Security Best Practices

1. ✅ Use separate users for admin and KYC databases
2. ✅ Grant only necessary permissions (read-only for KYC)
3. ✅ Use environment variables for passwords
4. ✅ Enable SSL for database connections
5. ✅ Regular backups
6. ✅ Monitor connection usage
7. ✅ Use strong passwords
8. ✅ Restrict network access (firewall rules)

## Summary

✅ **Two PostgreSQL databases**: kyc_admin and kyc
✅ **Separate configurations**: Different JDBC URLs
✅ **Connection pooling**: HikariCP configured
✅ **Security**: Read-only access to KYC database
✅ **Performance**: Indexes and optimization
✅ **Monitoring**: Connection and query tracking
✅ **Backup**: Automated backup strategy

Your system is now configured to use PostgreSQL for both databases with proper separation and security!

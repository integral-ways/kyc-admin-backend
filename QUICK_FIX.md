# Quick Fix for Multi-Datasource Setup

## Current Configuration

The system is configured to use:
- **Admin Database**: H2 in-memory (`jdbc:h2:mem:kycadmindb`)
- **KYC Database**: PostgreSQL (`jdbc:postgresql://localhost:5432/kyc`)

## Error: PostgreSQL Driver Issue

If you see the error:
```
Driver org.postgresql.Driver claims to not accept jdbcUrl
```

This means PostgreSQL is not running or the connection details are incorrect.

## Solutions

### Option 1: Use H2 for Both Databases (Development)

Update `application.yml`:

```yaml
spring:
  datasource:
    admin:
      jdbc-url: jdbc:h2:mem:kycadmindb
      driver-class-name: org.h2.Driver
      username: admin
      password: admin
    
    kyc:
      jdbc-url: jdbc:h2:mem:kycdb  # Changed to H2
      driver-class-name: org.h2.Driver  # Changed to H2
      username: sa
      password: password
```

**Note**: This won't connect to real KYC data, but allows testing the admin system.

### Option 2: Connect to Existing PostgreSQL KYC Database

1. **Ensure PostgreSQL is running**:
```bash
# Check if PostgreSQL is running
pg_isready -h localhost -p 5432
```

2. **Verify database exists**:
```bash
psql -h localhost -U postgres -l
```

3. **Update credentials in `application.yml`**:
```yaml
spring:
  datasource:
    kyc:
      jdbc-url: jdbc:postgresql://localhost:5432/kyc
      driver-class-name: org.postgresql.Driver
      username: your_username
      password: your_password
```

4. **Ensure `customers` table exists**:
```sql
-- Connect to database
psql -h localhost -U postgres -d kyc

-- Check if customers table exists
\dt customers

-- If not, create it
CREATE TABLE customers (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    mobile_number VARCHAR(20),
    email VARCHAR(100),
    full_name VARCHAR(100),
    status VARCHAR(20),
    onboarding_type VARCHAR(20),
    current_step INT,
    completion_percentage DOUBLE PRECISION,
    submitted_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Option 3: Use H2 in Server Mode (Connect to Main KYC App)

If your main KYC application uses H2, start it in server mode:

**In main KYC application's `application.yml`**:
```yaml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost:9092/mem:kycdb;DB_CLOSE_DELAY=-1
```

**In admin backend's `application.yml`**:
```yaml
spring:
  datasource:
    kyc:
      jdbc-url: jdbc:h2:tcp://localhost:9092/mem:kycdb
      driver-class-name: org.h2.Driver
      username: sa
      password: password
```

## Testing the Configuration

### 1. Test Admin Database (H2)

```bash
# Start the application
mvn spring-boot:run

# Access H2 Console
http://localhost:8081/api/admin/h2-console

# Connection details:
JDBC URL: jdbc:h2:mem:kycadmindb
Username: admin
Password: admin
```

### 2. Test KYC Database Connection

Add this test endpoint temporarily:

```java
@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    @Qualifier("kycDataSource")
    private DataSource kycDataSource;
    
    @GetMapping("/kyc-connection")
    public String testKycConnection() {
        try (Connection conn = kycDataSource.getConnection()) {
            return "KYC Database connected successfully!";
        } catch (Exception e) {
            return "KYC Database connection failed: " + e.getMessage();
        }
    }
}
```

Access: `http://localhost:8081/api/admin/test/kyc-connection`

## Current Setup Summary

### What's Working
✅ Admin database (H2) - for users, permissions, audit logs
✅ Multi-datasource configuration
✅ Spring Boot application structure
✅ All entities and repositories

### What Needs Configuration
⚠️ KYC database connection - needs valid PostgreSQL connection OR switch to H2

## Recommended for Development

Use H2 for both databases:

```yaml
spring:
  datasource:
    admin:
      jdbc-url: jdbc:h2:mem:kycadmindb
      driver-class-name: org.h2.Driver
      username: admin
      password: admin
    
    kyc:
      jdbc-url: jdbc:h2:mem:kycdb
      driver-class-name: org.h2.Driver
      username: sa
      password: password
```

Then manually insert test data:

```sql
-- Access H2 console and run:
INSERT INTO customers (id, user_id, mobile_number, email, full_name, status, onboarding_type, current_step, completion_percentage, submitted_at, created_at, updated_at)
VALUES 
('1', 'user1', '+1234567890', 'user1@example.com', 'John Doe', 'SUBMITTED', 'INDIVIDUAL', 5, 80.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2', 'user2', '+1234567891', 'user2@example.com', 'Jane Smith', 'UNDER_REVIEW', 'INDIVIDUAL', 6, 90.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

## Recommended for Production

Use PostgreSQL for both:

```yaml
spring:
  datasource:
    admin:
      jdbc-url: jdbc:postgresql://localhost:5432/kyc_admin
      driver-class-name: org.postgresql.Driver
      username: admin_user
      password: ${ADMIN_DB_PASSWORD}
    
    kyc:
      jdbc-url: jdbc:postgresql://localhost:5432/kyc_main
      driver-class-name: org.postgresql.Driver
      username: kyc_readonly
      password: ${KYC_DB_PASSWORD}
```

## Quick Start Commands

### Development (H2 for both):
```bash
# 1. Update application.yml to use H2 for KYC datasource
# 2. Run the application
cd kyc-admin-backend
mvn spring-boot:run

# 3. Access the application
# Frontend: http://localhost:4201
# Backend: http://localhost:8081/api/admin
# H2 Console: http://localhost:8081/api/admin/h2-console
```

### Production (PostgreSQL):
```bash
# 1. Ensure PostgreSQL is running
# 2. Create databases
createdb kyc_admin
createdb kyc_main

# 3. Update application.yml with correct credentials
# 4. Run the application
mvn spring-boot:run
```

## Troubleshooting

### Error: "Driver claims to not accept jdbcUrl"
- **Cause**: Wrong driver for the JDBC URL
- **Fix**: Ensure driver-class-name matches the jdbc-url protocol

### Error: "Connection refused"
- **Cause**: Database server not running
- **Fix**: Start PostgreSQL or use H2

### Error: "Authentication failed"
- **Cause**: Wrong username/password
- **Fix**: Update credentials in application.yml

### Error: "Database does not exist"
- **Cause**: Database not created
- **Fix**: Create the database using `createdb` or pgAdmin

## Need Help?

1. Check logs for detailed error messages
2. Verify database is accessible
3. Test connection with database client
4. Review application.yml configuration
5. Ensure correct driver is in pom.xml

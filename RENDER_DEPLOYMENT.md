# Render.io Deployment Guide

## Problem Fixed
The application was trying to connect to `localhost:5432` instead of using environment variables. This has been fixed by:
1. Updated `AdminDataSourceConfig.java` to read from properties
2. Updated `KycDataSourceConfig.java` to read from properties
3. Created `application-prod.properties` for production deployment

## Deployment Steps

### 1. Create PostgreSQL Databases on Render
Create two PostgreSQL databases:
- **Admin Database**: For user management and admin operations
- **KYC Database**: For KYC data

### 2. Configure Environment Variables in Render
In your Render web service, add these environment variables:

```
SPRING_PROFILES_ACTIVE=prod

# Admin Database
ADMIN_DB_URL=jdbc:postgresql://<your-admin-db-host>/<database-name>
ADMIN_DB_USERNAME=<your-admin-db-username>
ADMIN_DB_PASSWORD=<your-admin-db-password>

# KYC Database
KYC_DB_URL=jdbc:postgresql://<your-kyc-db-host>/<database-name>
KYC_DB_USERNAME=<your-kyc-db-username>
KYC_DB_PASSWORD=<your-kyc-db-password>

# Optional: Override defaults
SERVER_PORT=8081
JWT_SECRET=<your-secure-jwt-secret>
JWT_EXPIRATION=86400000
JPA_DDL_AUTO=validate
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
```

### 3. Get Database Connection Details from Render
For each PostgreSQL database:
1. Go to your database dashboard
2. Copy the **External Database URL** (not Internal)
3. Convert it to JDBC format:
   - From: `postgres://user:password@host:5432/database`
   - To: `jdbc:postgresql://host:5432/database`

### 4. Build Command
```bash
mvn clean package -DskipTests
```

### 5. Start Command
```bash
java -jar target/kyc-admin-backend-0.0.1-SNAPSHOT.jar
```

### 6. Port Configuration
Render expects your app to listen on the port specified in the `PORT` environment variable. Update your `application.properties`:
```properties
server.port=${PORT:8081}
```

## Troubleshooting

### Connection Refused Error
If you see `Connection to localhost:5432 refused`:
- Verify environment variables are set correctly in Render
- Ensure you're using the **External** database URL, not Internal
- Check that `SPRING_PROFILES_ACTIVE=prod` is set

### No Open Ports Detected
If Render shows "No open ports detected":
- Make sure your app uses `${PORT}` environment variable
- Check application logs for startup errors
- Verify the app actually starts (check for exceptions)

### Database Connection Timeout
- Verify database credentials are correct
- Check that databases are in the same region for better latency
- Increase connection timeout if needed

## Health Check
Add this endpoint to verify deployment:
```
GET /api/admin/actuator/health
```

## Notes
- Use `prod` profile for production deployment
- Use `dev` or `uat` profiles for development/staging
- Never commit database credentials to git
- Always use environment variables for sensitive data

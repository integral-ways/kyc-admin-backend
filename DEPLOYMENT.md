# KYC Admin Backend - Deployment Guide

## Production Deployment Checklist

### 1. Database Configuration

Replace H2 with PostgreSQL or MySQL:

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/kyc_admin
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate  # Use Flyway/Liquibase for migrations
    show-sql: false
```

Add dependency in `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. Security Configuration

#### Update JWT Secret
```yaml
jwt:
  secret: ${JWT_SECRET}  # Use strong random key (256-bit)
  expiration: 3600000    # 1 hour for production
```

Generate strong secret:
```bash
openssl rand -base64 64
```

#### Enable HTTPS
```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: tomcat
```

### 3. CORS Configuration

Update allowed origins:
```yaml
cors:
  allowed-origins: https://admin.yourdomain.com,https://yourdomain.com
```

### 4. Logging Configuration

```yaml
logging:
  level:
    root: INFO
    com.onboarding.admin: DEBUG
  file:
    name: /var/log/kyc-admin/application.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 5. Environment Variables

Create `.env` file (never commit this):
```bash
# Database
DB_USERNAME=kyc_admin_user
DB_PASSWORD=strong_password_here

# JWT
JWT_SECRET=your_256_bit_secret_key_here

# Server
SERVER_PORT=8081
CONTEXT_PATH=/api/admin

# CORS
ALLOWED_ORIGINS=https://admin.yourdomain.com

# SSL
KEYSTORE_PASSWORD=keystore_password_here
```

### 6. Build for Production

```bash
# Clean and build
mvn clean package -DskipTests

# Build with tests
mvn clean package

# The JAR will be in target/kyc-admin-backend-1.0.0.jar
```

### 7. Docker Deployment

#### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/kyc-admin-backend-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

#### docker-compose.yml
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: kyc_admin
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  kyc-admin-backend:
    build: .
    ports:
      - "8081:8081"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/kyc_admin
    depends_on:
      - postgres

volumes:
  postgres_data:
```

Build and run:
```bash
docker-compose up -d
```

### 8. Systemd Service (Linux)

Create `/etc/systemd/system/kyc-admin.service`:
```ini
[Unit]
Description=KYC Admin Backend
After=network.target

[Service]
Type=simple
User=kyc-admin
WorkingDirectory=/opt/kyc-admin
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /opt/kyc-admin/kyc-admin-backend-1.0.0.jar
Restart=on-failure
RestartSec=10

Environment="DB_USERNAME=kyc_admin_user"
Environment="DB_PASSWORD=strong_password"
Environment="JWT_SECRET=your_secret_key"

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable kyc-admin
sudo systemctl start kyc-admin
sudo systemctl status kyc-admin
```

### 9. Nginx Reverse Proxy

```nginx
server {
    listen 443 ssl http2;
    server_name admin-api.yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    location /api/admin {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 10. Monitoring & Health Checks

Add Spring Boot Actuator:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Configure:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

Health check endpoint: `GET /api/admin/actuator/health`

### 11. Database Migrations

Use Flyway for database versioning:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

Create migration files in `src/main/resources/db/migration/`:
- `V1__initial_schema.sql`
- `V2__add_audit_logs.sql`

### 12. Backup Strategy

#### Database Backup
```bash
# PostgreSQL backup
pg_dump -U kyc_admin_user kyc_admin > backup_$(date +%Y%m%d).sql

# Automated daily backup
0 2 * * * /usr/bin/pg_dump -U kyc_admin_user kyc_admin > /backups/kyc_admin_$(date +\%Y\%m\%d).sql
```

#### Application Backup
- Configuration files
- SSL certificates
- Environment variables
- Application logs

### 13. Performance Tuning

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

server:
  tomcat:
    threads:
      max: 200
      min-spare: 10
```

### 14. Security Hardening

1. **Rate Limiting**: Implement with Spring Cloud Gateway or Nginx
2. **IP Whitelisting**: Configure in security config
3. **Password Policy**: Enforce strong passwords
4. **Session Management**: Configure session timeout
5. **CSRF Protection**: Enable for state-changing operations
6. **SQL Injection**: Already protected by JPA
7. **XSS Protection**: Sanitize inputs
8. **Audit Logging**: Already implemented

### 15. Monitoring Tools

- **Application Monitoring**: Spring Boot Actuator + Prometheus
- **Log Aggregation**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **APM**: New Relic, Datadog, or AppDynamics
- **Uptime Monitoring**: UptimeRobot, Pingdom

### 16. CI/CD Pipeline

#### GitHub Actions Example
```yaml
name: Deploy KYC Admin Backend

on:
  push:
    branches: [ main ]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
          
      - name: Build with Maven
        run: mvn clean package -DskipTests
        
      - name: Deploy to server
        run: |
          scp target/*.jar user@server:/opt/kyc-admin/
          ssh user@server 'sudo systemctl restart kyc-admin'
```

### 17. Disaster Recovery

1. **Regular Backups**: Daily automated backups
2. **Backup Testing**: Monthly restore tests
3. **Failover Plan**: Document recovery procedures
4. **RTO/RPO**: Define recovery time and point objectives
5. **Documentation**: Keep deployment docs updated

### 18. Post-Deployment Verification

```bash
# Health check
curl https://admin-api.yourdomain.com/api/admin/actuator/health

# Test login
curl -X POST https://admin-api.yourdomain.com/api/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Check logs
tail -f /var/log/kyc-admin/application.log

# Monitor resources
htop
```

### 19. Scaling Considerations

#### Horizontal Scaling
- Use load balancer (Nginx, HAProxy)
- Stateless application design (JWT tokens)
- Shared database or read replicas
- Redis for session management (if needed)

#### Vertical Scaling
- Increase JVM heap size: `-Xmx2g -Xms1g`
- Optimize database queries
- Add database indexes
- Enable caching

### 20. Maintenance

- **Regular Updates**: Keep dependencies updated
- **Security Patches**: Apply promptly
- **Log Rotation**: Configure logrotate
- **Database Maintenance**: Regular VACUUM and ANALYZE
- **Certificate Renewal**: Automate with Let's Encrypt

## Quick Production Deployment

```bash
# 1. Clone repository
git clone <repository-url>
cd kyc-admin-backend

# 2. Set environment variables
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export JWT_SECRET=$(openssl rand -base64 64)

# 3. Build
mvn clean package -DskipTests

# 4. Run with production profile
java -jar -Dspring.profiles.active=prod target/kyc-admin-backend-1.0.0.jar
```

## Troubleshooting

### Application won't start
- Check logs: `tail -f /var/log/kyc-admin/application.log`
- Verify database connection
- Check port availability: `netstat -tulpn | grep 8081`

### Database connection issues
- Verify credentials
- Check firewall rules
- Test connection: `psql -h localhost -U kyc_admin_user -d kyc_admin`

### Performance issues
- Check database query performance
- Monitor JVM memory usage
- Review application logs for errors
- Check connection pool settings

## Support

For production support, contact: devops@yourdomain.com

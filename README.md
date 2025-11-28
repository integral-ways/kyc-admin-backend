# KYC Admin Backend

Enterprise-grade Spring Boot backend service for KYC application management with comprehensive features.

## Features

✅ **Authentication & Authorization**
- JWT-based authentication with secure token management
- Role-based access control (RBAC) with Profiles and Permissions
- Password encryption with BCrypt
- Session management and token expiration

✅ **Application Management**
- Complete CRUD operations for KYC applications
- Status workflow management (Draft → Submitted → Under Review → Approved/Rejected)
- Application assignment to reviewers
- Pagination and filtering support
- Advanced search capabilities

✅ **User Management**
- Admin user creation and management
- Profile assignment
- User activation/deactivation
- User activity tracking

✅ **Audit & Compliance**
- Comprehensive audit logging for all critical operations
- Track who did what and when
- Audit trail for compliance requirements

✅ **Analytics & Reporting**
- Dashboard statistics
- Status distribution analytics
- Daily and monthly submission trends
- Completion percentage tracking

✅ **Security**
- CORS configuration
- Global exception handling
- Input validation
- SQL injection prevention with JPA

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security + JWT (jjwt 0.11.5)
- **Database**: H2 (Development), JPA/Hibernate
- **Build Tool**: Maven
- **Java Version**: 17

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build & Run

1. Navigate to backend directory:
```bash
cd kyc-admin-backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Application will start at: `http://localhost:8081/api/admin`

### Database Access

H2 Console: `http://localhost:8081/api/admin/h2-console`
- JDBC URL: `jdbc:h2:mem:kycadmindb`
- Username: `admin`
- Password: `admin`

## Default Credentials

- **Username**: `admin`
- **Password**: `admin123`

## API Documentation

### Authentication Endpoints

#### Login
```http
POST /api/admin/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "token": "eyJhbGc...",
  "username": "admin",
  "email": "admin@kyc.com",
  "fullName": "System Administrator",
  "permissions": ["VIEW_APPLICATIONS", "REVIEW_APPLICATIONS", ...]
}
```

### Application Management

#### Get All Applications (Paginated)
```http
GET /api/admin/applications?page=0&size=10
Authorization: Bearer {token}
```

#### Get Application by ID
```http
GET /api/admin/applications/{id}
Authorization: Bearer {token}
```

#### Filter by Status
```http
GET /api/admin/applications/status/SUBMITTED?page=0&size=10
Authorization: Bearer {token}
```

#### Update Application Status
```http
PUT /api/admin/applications/{id}/status?status=APPROVED&reviewNotes=Approved
Authorization: Bearer {token}
```

#### Assign Application
```http
PUT /api/admin/applications/{id}/assign?assignedTo=reviewer1
Authorization: Bearer {token}
```

### User Management

#### Get All Users
```http
GET /api/admin/users
Authorization: Bearer {token}
Permission: MANAGE_USERS
```

#### Create User
```http
POST /api/admin/users
Authorization: Bearer {token}
Permission: MANAGE_USERS
Content-Type: application/json

{
  "username": "reviewer1",
  "email": "reviewer@kyc.com",
  "password": "password123",
  "fullName": "John Reviewer",
  "profileIds": ["profile-id-here"]
}
```

#### Activate/Deactivate User
```http
PUT /api/admin/users/{id}/activate
PUT /api/admin/users/{id}/deactivate
Authorization: Bearer {token}
Permission: MANAGE_USERS
```

### Dashboard & Statistics

#### Get Dashboard Stats
```http
GET /api/admin/dashboard/stats
Authorization: Bearer {token}

Response:
{
  "totalApplications": 150,
  "submittedApplications": 45,
  "underReviewApplications": 30,
  "approvedApplications": 60,
  "rejectedApplications": 10,
  "todaySubmissions": 5,
  "weekSubmissions": 25,
  "monthSubmissions": 100
}
```

#### Get Statistics
```http
GET /api/admin/statistics
Authorization: Bearer {token}

Response:
{
  "statusDistribution": {...},
  "typeDistribution": {...},
  "dailySubmissions": {...},
  "monthlySubmissions": {...},
  "averageCompletionPercentage": 75.5,
  "totalApplications": 150
}
```

### Search

#### Advanced Search
```http
POST /api/admin/search/applications?page=0&size=10
Authorization: Bearer {token}
Content-Type: application/json

{
  "query": "john",
  "status": "SUBMITTED",
  "onboardingType": "INDIVIDUAL",
  "assignedTo": "reviewer1"
}
```

### Audit Logs

#### Get All Audit Logs
```http
GET /api/admin/audit-logs?page=0&size=20
Authorization: Bearer {token}
Permission: MANAGE_USERS
```

#### Get Logs by Resource
```http
GET /api/admin/audit-logs/resource/KYC_APPLICATION?page=0&size=20
Authorization: Bearer {token}
```

#### Get Logs by Resource ID
```http
GET /api/admin/audit-logs/resource-id/{applicationId}?page=0&size=20
Authorization: Bearer {token}
```

### Profiles & Permissions

#### Get All Profiles
```http
GET /api/admin/profiles
Authorization: Bearer {token}
Permission: MANAGE_PROFILES
```

#### Get All Permissions
```http
GET /api/admin/permissions
Authorization: Bearer {token}
Permission: MANAGE_PROFILES
```

## Permissions

| Permission | Description | Resource | Action |
|------------|-------------|----------|--------|
| `VIEW_APPLICATIONS` | View KYC applications | applications | read |
| `REVIEW_APPLICATIONS` | Review and update applications | applications | update |
| `ASSIGN_APPLICATIONS` | Assign applications to reviewers | applications | assign |
| `MANAGE_USERS` | Manage admin users | users | manage |
| `MANAGE_PROFILES` | Manage profiles and permissions | profiles | manage |

## Profiles

### ADMIN Profile
- Full system access
- All permissions included
- Can manage users, profiles, and applications

### REVIEWER Profile
- Can view applications (`VIEW_APPLICATIONS`)
- Can review and update applications (`REVIEW_APPLICATIONS`)
- Limited to application management only

## Application Status Workflow

```
DRAFT → SUBMITTED → UNDER_REVIEW → APPROVED/REJECTED
                                 ↓
                           PENDING_INFO
```

- **DRAFT**: Initial state, user is filling the form
- **SUBMITTED**: User has submitted the application
- **UNDER_REVIEW**: Admin is reviewing the application
- **APPROVED**: Application approved by admin
- **REJECTED**: Application rejected by admin
- **PENDING_INFO**: Waiting for additional information from user

## Configuration

### application.yml

```yaml
server:
  port: 8081
  servlet:
    context-path: /api/admin

jwt:
  secret: your-secret-key-change-in-production
  expiration: 86400000  # 24 hours

cors:
  allowed-origins: http://localhost:4201
```

## Security Considerations

⚠️ **For Production:**

1. Change JWT secret in `application.yml`
2. Use a production database (PostgreSQL/MySQL)
3. Enable HTTPS
4. Implement rate limiting
5. Add password policies
6. Enable 2FA for admin users
7. Regular security audits
8. Implement IP whitelisting
9. Add request logging
10. Use environment variables for sensitive data

## Error Handling

The API uses standard HTTP status codes:

- `200 OK` - Success
- `201 Created` - Resource created
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

Error Response Format:
```json
{
  "status": 400,
  "message": "Error description",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## Development

### Adding New Permissions

1. Add permission in `DataInitializer.java`
2. Assign to appropriate profiles
3. Use `@PreAuthorize("hasAuthority('PERMISSION_NAME')")` on endpoints

### Adding New Endpoints

1. Create controller method
2. Add appropriate security annotations
3. Add CORS configuration if needed
4. Update API documentation

## Testing

Run tests:
```bash
mvn test
```

## Logging

Application logs include:
- Authentication attempts
- Authorization failures
- Application status changes
- User management operations
- Audit trail events

## Support

For issues or questions, please contact the development team.

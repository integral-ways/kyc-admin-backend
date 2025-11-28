# KYC Admin Backend - Complete API Reference

## Base URL
```
http://localhost:8081/api/admin
```

## Authentication

All endpoints except `/auth/login` require JWT authentication.

Include the token in the Authorization header:
```
Authorization: Bearer {your-jwt-token}
```

---

## 1. Authentication API

### 1.1 Login
**Endpoint:** `POST /auth/login`

**Description:** Authenticate user and receive JWT token

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "email": "admin@kyc.com",
  "fullName": "System Administrator",
  "permissions": [
    "VIEW_APPLICATIONS",
    "REVIEW_APPLICATIONS",
    "ASSIGN_APPLICATIONS",
    "MANAGE_USERS",
    "MANAGE_PROFILES"
  ]
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid credentials

---

## 2. Application Management API

### 2.1 Get All Applications
**Endpoint:** `GET /applications`

**Permission:** `VIEW_APPLICATIONS`

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 10) - Page size

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": "uuid",
      "userId": "user-uuid",
      "mobileNumber": "+1234567890",
      "email": "user@example.com",
      "fullName": "John Doe",
      "status": "SUBMITTED",
      "onboardingType": "INDIVIDUAL",
      "currentStep": 5,
      "completionPercentage": 80.0,
      "assignedTo": "reviewer1",
      "reviewNotes": null,
      "submittedAt": "2024-01-01T10:00:00Z",
      "reviewedAt": null,
      "createdAt": "2024-01-01T09:00:00Z",
      "updatedAt": "2024-01-01T10:00:00Z"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

### 2.2 Get Application by ID
**Endpoint:** `GET /applications/{id}`

**Permission:** `VIEW_APPLICATIONS`

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "userId": "user-uuid",
  "mobileNumber": "+1234567890",
  "email": "user@example.com",
  "fullName": "John Doe",
  "status": "SUBMITTED",
  "onboardingType": "INDIVIDUAL",
  "currentStep": 5,
  "completionPercentage": 80.0,
  "assignedTo": "reviewer1",
  "reviewNotes": null,
  "submittedAt": "2024-01-01T10:00:00Z",
  "reviewedAt": null,
  "createdAt": "2024-01-01T09:00:00Z",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

**Error Responses:**
- `404 Not Found` - Application not found

### 2.3 Get Applications by Status
**Endpoint:** `GET /applications/status/{status}`

**Permission:** `VIEW_APPLICATIONS`

**Path Parameters:**
- `status` - One of: DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PENDING_INFO

**Query Parameters:**
- `page` (optional, default: 0)
- `size` (optional, default: 10)

**Response:** Same as 2.1

### 2.4 Update Application Status
**Endpoint:** `PUT /applications/{id}/status`

**Permission:** `REVIEW_APPLICATIONS`

**Query Parameters:**
- `status` (required) - New status
- `reviewNotes` (optional) - Review notes

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "status": "APPROVED",
  "reviewNotes": "All documents verified",
  "reviewedAt": "2024-01-01T11:00:00Z",
  ...
}
```

### 2.5 Assign Application
**Endpoint:** `PUT /applications/{id}/assign`

**Permission:** `ASSIGN_APPLICATIONS`

**Query Parameters:**
- `assignedTo` (required) - Username of assignee

**Response:** `200 OK`

---

## 3. User Management API

### 3.1 Get All Users
**Endpoint:** `GET /users`

**Permission:** `MANAGE_USERS`

**Response:** `200 OK`
```json
[
  {
    "id": "uuid",
    "username": "admin",
    "email": "admin@kyc.com",
    "fullName": "System Administrator",
    "active": true,
    "profiles": ["ADMIN"],
    "createdAt": "2024-01-01T00:00:00Z",
    "lastLogin": "2024-01-01T10:00:00Z"
  }
]
```

### 3.2 Get User by ID
**Endpoint:** `GET /users/{id}`

**Permission:** `MANAGE_USERS`

**Response:** `200 OK` - Same as 3.1 (single object)

### 3.3 Create User
**Endpoint:** `POST /users`

**Permission:** `MANAGE_USERS`

**Request Body:**
```json
{
  "username": "reviewer1",
  "email": "reviewer@kyc.com",
  "password": "password123",
  "fullName": "John Reviewer",
  "profileIds": ["profile-uuid"]
}
```

**Validation:**
- `username`: 3-50 characters, required
- `email`: Valid email format, required
- `password`: Minimum 6 characters, required
- `fullName`: Required

**Response:** `201 Created`

**Error Responses:**
- `400 Bad Request` - Username or email already exists

### 3.4 Activate User
**Endpoint:** `PUT /users/{id}/activate`

**Permission:** `MANAGE_USERS`

**Response:** `200 OK`

### 3.5 Deactivate User
**Endpoint:** `PUT /users/{id}/deactivate`

**Permission:** `MANAGE_USERS`

**Response:** `200 OK`

### 3.6 Delete User
**Endpoint:** `DELETE /users/{id}`

**Permission:** `MANAGE_USERS`

**Response:** `204 No Content`

---

## 4. Dashboard API

### 4.1 Get Dashboard Statistics
**Endpoint:** `GET /dashboard/stats`

**Permission:** Any authenticated user

**Response:** `200 OK`
```json
{
  "totalApplications": 150,
  "submittedApplications": 45,
  "underReviewApplications": 30,
  "approvedApplications": 60,
  "rejectedApplications": 10,
  "draftApplications": 5,
  "pendingInfoApplications": 0,
  "todaySubmissions": 5,
  "weekSubmissions": 25,
  "monthSubmissions": 100
}
```

### 4.2 Get Dashboard Widgets
**Endpoint:** `GET /dashboard/widgets`

**Permission:** Any authenticated user

**Response:** `200 OK`

---

## 5. Statistics API

### 5.1 Get Comprehensive Statistics
**Endpoint:** `GET /statistics`

**Permission:** `VIEW_APPLICATIONS`

**Response:** `200 OK`
```json
{
  "statusDistribution": {
    "SUBMITTED": 45,
    "UNDER_REVIEW": 30,
    "APPROVED": 60,
    "REJECTED": 10,
    "DRAFT": 5
  },
  "typeDistribution": {
    "INDIVIDUAL": 120,
    "ENTITY": 30
  },
  "dailySubmissions": {
    "2024-01-01": 5,
    "2024-01-02": 8,
    ...
  },
  "monthlySubmissions": {
    "2024-01": 100,
    "2023-12": 95,
    ...
  },
  "averageCompletionPercentage": 75.5,
  "totalApplications": 150
}
```

---

## 6. Search API

### 6.1 Advanced Application Search
**Endpoint:** `POST /search/applications`

**Permission:** `VIEW_APPLICATIONS`

**Query Parameters:**
- `page` (optional, default: 0)
- `size` (optional, default: 10)

**Request Body:**
```json
{
  "query": "john",
  "status": "SUBMITTED",
  "onboardingType": "INDIVIDUAL",
  "assignedTo": "reviewer1",
  "dateFrom": "2024-01-01",
  "dateTo": "2024-01-31"
}
```

**All fields are optional**

**Response:** `200 OK` - Same format as 2.1

---

## 7. Audit Log API

### 7.1 Get All Audit Logs
**Endpoint:** `GET /audit-logs`

**Permission:** `MANAGE_USERS`

**Query Parameters:**
- `page` (optional, default: 0)
- `size` (optional, default: 20)

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": "uuid",
      "userId": "user-uuid",
      "username": "admin",
      "action": "UPDATE_STATUS",
      "resource": "KYC_APPLICATION",
      "resourceId": "app-uuid",
      "details": "Status changed from SUBMITTED to APPROVED",
      "ipAddress": "192.168.1.1",
      "timestamp": "2024-01-01T10:00:00Z"
    }
  ],
  "totalElements": 500,
  "totalPages": 25,
  "size": 20,
  "number": 0
}
```

### 7.2 Get Logs by Resource
**Endpoint:** `GET /audit-logs/resource/{resource}`

**Permission:** `MANAGE_USERS`

**Path Parameters:**
- `resource` - Resource type (e.g., KYC_APPLICATION, ADMIN_USER)

**Query Parameters:** Same as 7.1

**Response:** Same as 7.1

### 7.3 Get Logs by Resource ID
**Endpoint:** `GET /audit-logs/resource-id/{resourceId}`

**Permission:** `VIEW_APPLICATIONS`

**Path Parameters:**
- `resourceId` - Specific resource ID

**Query Parameters:** Same as 7.1

**Response:** Same as 7.1

---

## 8. Profile Management API

### 8.1 Get All Profiles
**Endpoint:** `GET /profiles`

**Permission:** `MANAGE_PROFILES`

**Response:** `200 OK`
```json
[
  {
    "id": "uuid",
    "name": "ADMIN",
    "description": "Full system access",
    "active": true,
    "permissions": [
      "VIEW_APPLICATIONS",
      "REVIEW_APPLICATIONS",
      "ASSIGN_APPLICATIONS",
      "MANAGE_USERS",
      "MANAGE_PROFILES"
    ]
  }
]
```

### 8.2 Get Profile by ID
**Endpoint:** `GET /profiles/{id}`

**Permission:** `MANAGE_PROFILES`

**Response:** `200 OK` - Same as 8.1 (single object)

---

## 9. Permission Management API

### 9.1 Get All Permissions
**Endpoint:** `GET /permissions`

**Permission:** `MANAGE_PROFILES`

**Response:** `200 OK`
```json
[
  {
    "id": "uuid",
    "name": "VIEW_APPLICATIONS",
    "description": "View KYC applications",
    "resource": "applications",
    "action": "read"
  }
]
```

---

## Common Error Responses

### 400 Bad Request
```json
{
  "status": 400,
  "message": "Invalid input data",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### 401 Unauthorized
```json
{
  "status": 401,
  "message": "Invalid username or password",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### 403 Forbidden
```json
{
  "status": 403,
  "message": "Access denied",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "message": "Resource not found",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "message": "An unexpected error occurred",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

---

## Rate Limiting

Currently not implemented. Recommended for production:
- 100 requests per minute per IP
- 1000 requests per hour per user

## Pagination

All paginated endpoints support:
- `page`: Zero-based page index (default: 0)
- `size`: Number of items per page (default: 10, max: 100)
- `sort`: Sort field and direction (e.g., `createdAt,desc`)

## CORS

Configured to allow requests from:
- `http://localhost:4201` (Frontend)

Additional origins can be added in `application.yml`

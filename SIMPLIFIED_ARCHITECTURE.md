# Simplified Admin Architecture

## Overview

The KYC Admin Backend now reads **directly from the `customers` table** in the KYC database. No duplicate `KycApplication` entity needed!

## Data Flow

### Reading Applications
```
Frontend → Backend → CustomerRepository → customers table (KYC DB)
```

All application data comes directly from the existing `customers` table.

### Storing Review Data
```
Frontend → Backend → ApplicationReviewRepository → application_reviews table (Admin DB)
```

Only admin-specific data (review notes, assignments) is stored separately.

## Database Tables

### KYC Database (Read-Only)
- **customers** - All application data (existing table)

### Admin Database
- **admin_users** - Admin user accounts
- **profiles** - User roles
- **permissions** - Access permissions
- **application_reviews** - Review notes and assignments (NEW)
- **audit_logs** - Activity tracking

## New Entity: ApplicationReview

Stores only admin-specific data:
```java
@Entity
@Table(name = "application_reviews")
public class ApplicationReview {
    @Id
    private String applicationId; // References customer.id
    
    private String assignedTo;
    private String reviewNotes;
    private Instant reviewedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
```

## How It Works

### 1. List Applications
```java
// Reads directly from customers table
customerRepository.findAll(pageable)
```

### 2. Get Application Details
```java
// Get customer data
Customer customer = customerRepository.findById(id);

// Get review data if exists
Optional<ApplicationReview> review = reviewRepository.findByApplicationId(id);

// Merge and return
return customerToDto(customer, review);
```

### 3. Update Review Notes
```java
// Get customer (read-only)
Customer customer = customerRepository.findById(id);

// Save review data in admin database
ApplicationReview review = new ApplicationReview();
review.setApplicationId(customer.getId());
review.setReviewNotes(notes);
reviewRepository.save(review);
```

### 4. Assign Reviewer
```java
// Get customer (read-only)
Customer customer = customerRepository.findById(id);

// Save assignment in admin database
ApplicationReview review = reviewRepository.findByApplicationId(id)
    .orElse(new ApplicationReview());
review.setAssignedTo(reviewer);
reviewRepository.save(review);
```

## Benefits

✅ **No Data Duplication** - Reads directly from source
✅ **Single Source of Truth** - customers table is authoritative
✅ **Simpler Architecture** - One less entity to manage
✅ **Read-Only Access** - Admin never modifies customer data
✅ **Separation of Concerns** - Review data stored separately

## API Endpoints (Unchanged)

All endpoints work the same way:
- `GET /applications` - List all (from customers table)
- `GET /applications/{id}` - Get details (from customers + reviews)
- `PUT /applications/{id}/status` - Update review notes
- `PUT /applications/{id}/assign` - Assign reviewer

## Summary

The admin system is now simpler and more efficient:
- Reads application data from `customers` table (KYC DB)
- Stores review-specific data in `application_reviews` table (Admin DB)
- No duplicate entities or data synchronization needed

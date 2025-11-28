#!/bin/bash

# PostgreSQL Setup Script for KYC Admin Backend
# This script creates the required databases and tables

echo "=========================================="
echo "KYC Admin Backend - PostgreSQL Setup"
echo "=========================================="
echo ""

# Configuration
DB_USER="postgres"
DB_PASSWORD="123456"
ADMIN_DB="kyc_admin"
KYC_DB="kyc"

echo "This script will:"
echo "1. Create database: $ADMIN_DB"
echo "2. Create database: $KYC_DB"
echo "3. Create customers table in $KYC_DB"
echo "4. Insert sample data"
echo ""
read -p "Continue? (y/n) " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    echo "Setup cancelled."
    exit 1
fi

echo ""
echo "Step 1: Creating databases..."
echo "------------------------------"

# Create admin database
PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -h localhost -c "CREATE DATABASE $ADMIN_DB;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✓ Database '$ADMIN_DB' created successfully"
else
    echo "⚠ Database '$ADMIN_DB' may already exist"
fi

# Create KYC database
PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -h localhost -c "CREATE DATABASE $KYC_DB;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✓ Database '$KYC_DB' created successfully"
else
    echo "⚠ Database '$KYC_DB' may already exist"
fi

echo ""
echo "Step 2: Creating customers table..."
echo "------------------------------------"

# Create customers table
PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -h localhost -d $KYC_DB <<EOF
CREATE TABLE IF NOT EXISTS customers (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    mobile_number VARCHAR(20),
    email VARCHAR(100),
    full_name VARCHAR(100),
    status VARCHAR(20) CHECK (status IN ('DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'PENDING_INFO')),
    onboarding_type VARCHAR(20) CHECK (onboarding_type IN ('INDIVIDUAL', 'ENTITY')),
    current_step INTEGER,
    completion_percentage DOUBLE PRECISION,
    submitted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_customers_status ON customers(status);
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customers_mobile ON customers(mobile_number);
CREATE INDEX IF NOT EXISTS idx_customers_created_at ON customers(created_at);
CREATE INDEX IF NOT EXISTS idx_customers_submitted_at ON customers(submitted_at);
EOF

if [ $? -eq 0 ]; then
    echo "✓ Customers table created successfully"
else
    echo "✗ Failed to create customers table"
    exit 1
fi

echo ""
echo "Step 3: Inserting sample data..."
echo "---------------------------------"

# Insert sample data
PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -h localhost -d $KYC_DB <<EOF
INSERT INTO customers (id, user_id, mobile_number, email, full_name, status, onboarding_type, current_step, completion_percentage, submitted_at, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'user001', '+1234567890', 'john.doe@example.com', 'John Doe', 'SUBMITTED', 'INDIVIDUAL', 5, 80.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440002', 'user002', '+1234567891', 'jane.smith@example.com', 'Jane Smith', 'UNDER_REVIEW', 'INDIVIDUAL', 6, 90.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440003', 'user003', '+1234567892', 'bob.johnson@example.com', 'Bob Johnson', 'APPROVED', 'INDIVIDUAL', 7, 100.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440004', 'user004', '+1234567893', 'alice.williams@example.com', 'Alice Williams', 'DRAFT', 'INDIVIDUAL', 3, 40.0, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440005', 'user005', '+1234567894', 'charlie.brown@example.com', 'Charlie Brown', 'REJECTED', 'INDIVIDUAL', 5, 75.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440006', 'entity001', '+1234567895', 'acme@example.com', 'ACME Corporation', 'SUBMITTED', 'ENTITY', 4, 60.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440007', 'entity002', '+1234567896', 'techcorp@example.com', 'Tech Corp Ltd', 'UNDER_REVIEW', 'ENTITY', 5, 70.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440008', 'user006', '+1234567897', 'david.miller@example.com', 'David Miller', 'PENDING_INFO', 'INDIVIDUAL', 4, 55.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440009', 'user007', '+1234567898', 'emma.davis@example.com', 'Emma Davis', 'SUBMITTED', 'INDIVIDUAL', 5, 85.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440010', 'user008', '+1234567899', 'frank.wilson@example.com', 'Frank Wilson', 'APPROVED', 'INDIVIDUAL', 7, 100.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
EOF

if [ $? -eq 0 ]; then
    echo "✓ Sample data inserted successfully"
else
    echo "✗ Failed to insert sample data"
    exit 1
fi

echo ""
echo "Step 4: Verification..."
echo "-----------------------"

# Verify data
CUSTOMER_COUNT=$(PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -h localhost -d $KYC_DB -t -c "SELECT COUNT(*) FROM customers;")
echo "✓ Total customers: $CUSTOMER_COUNT"

echo ""
echo "=========================================="
echo "Setup completed successfully!"
echo "=========================================="
echo ""
echo "Database Information:"
echo "  Admin Database: $ADMIN_DB"
echo "  KYC Database: $KYC_DB"
echo "  Username: $DB_USER"
echo "  Password: $DB_PASSWORD"
echo ""
echo "Next steps:"
echo "1. Update application.yml with your database credentials"
echo "2. Run: mvn spring-boot:run"
echo "3. Access: http://localhost:8081/api/admin"
echo "4. Login with: admin / admin123"
echo ""

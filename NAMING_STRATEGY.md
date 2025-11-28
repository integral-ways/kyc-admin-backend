# JPA Naming Strategy Configuration

## Overview
The admin backend uses two different naming strategies for the two datasources to match their respective database schemas.

## Configuration (Spring Boot 3.4.3 Compatible)

### KYC DataSource (Read-Only)
**Strategy**: `PhysicalNamingStrategyStandardImpl`
- Uses exact field names as column names (no conversion)
- Matches the existing KYC database schema
- Example: `firstName` → `firstname` column

**Properties**:
```java
hibernate.physical_naming_strategy = org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
hibernate.implicit_naming_strategy = org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
```

### Admin DataSource
**Strategy**: `CamelCaseToUnderscoresNamingStrategy`
- Converts camelCase to snake_case
- Standard for new PostgreSQL tables
- Example: `firstName` → `first_name` column

**Properties**:
```java
hibernate.physical_naming_strategy = org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy
hibernate.implicit_naming_strategy = org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
```

## Why Different Strategies?

1. **KYC Database**: Existing database with camelCase columns (e.g., `firstname`, `lastname`)
   - Must use `PhysicalNamingStrategyStandardImpl` to match existing schema
   - Read-only access, no schema modifications

2. **Admin Database**: New database following PostgreSQL best practices
   - Uses `CamelCaseToUnderscoresNamingStrategy` for snake_case columns
   - Allows Hibernate to auto-generate schema with proper naming

## Spring Boot 3.4.3 Compatibility

Both strategies are part of Hibernate ORM 6.x (included in Spring Boot 3.4.3):
- `org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl`
- `org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy`
- `org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl`

These are the recommended strategies for Hibernate 6.x and are fully compatible with Spring Boot 3.x.

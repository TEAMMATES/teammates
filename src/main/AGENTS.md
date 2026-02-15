# TEAMMATES Backend (Java) - AI Agent Guide

## Overview

This directory contains the main Java backend code for TEAMMATES, running on Google App Engine.

## Package Structure

```
teammates/
├── main/              # Application entry point
├── ui/                # UI Component (REST API)
│   ├── webapi/        # REST API endpoints (Actions)
│   ├── request/       # Request DTOs
│   ├── output/        # Response DTOs
│   └── servlets/      # HTTP servlets
├── logic/             # Logic Component
│   ├── core/          # Core business logic (*Logic classes)
│   ├── api/           # Logic API (Facade)
│   └── external/      # External service integrations
├── storage/           # Storage Component
│   ├── api/           # Storage API (*Db classes)
│   ├── entity/        # Entity classes (persistable)
│   └── search/        # Search functionality
├── common/            # Common Component
│   ├── datatransfer/  # Data Transfer Objects (*Attributes)
│   ├── exception/     # Custom exceptions
│   └── util/          # Utility classes
└── sqllogic/          # SQL-specific logic
```

## Component Responsibilities

### UI Component (`ui` package)

- **Entry point** for HTTP requests
- **Access control** via `GateKeeper` class
- **Request processing** via `Action` classes (Template Method pattern)
- **RESTful design**: Endpoints for resources, use HTTP methods appropriately

**Key Patterns:**
- All `Action` classes extend `Action` base class
- Use `*Request` classes for request data
- Use `*Output` classes for response data
- Throw custom exceptions for error handling
- Access control is checked in UI layer, NOT in Logic layer

### Logic Component (`logic` package)

- **Business logic** and transactions
- **Cascade operations** (create/update/delete)
- **Input sanitization**
- **External service integration** (email, task queue)

**Key Patterns:**
- `Logic` class is a Facade to all `*Logic` classes
- `*Logic` classes handle business rules for specific entities
- Access control is NOT checked here - UI layer does that
- Methods return `null` for "not found" (read operations)
- Methods throw exceptions for invalid operations (write operations)
- Update operations use `*UpdateOptions` pattern

### Storage Component (`storage` package)

- **CRUD operations** on entities
- **Data validation** before persistence
- **Hides persistence details** from Logic layer

**Key Patterns:**
- Entity classes are in `storage.entity` (not visible outside)
- Use `*Attributes` DTOs to transfer data
- `*Db` classes provide the API
- Delete operations fail silently if entity doesn't exist
- Returns `null` for "not found" (read operations)
- No cascade operations - Logic layer handles those

### Common Component (`common` package)

- **Data Transfer Objects** (`*Attributes` classes)
- **Custom exceptions** (`EntityNotFoundException`, etc.)
- **Utility classes** (`StringHelper`, `TimeHelper`, `FieldValidator`, etc.)

## Common Patterns

### Creating Entities

```java
// Logic layer
Logic logic = new Logic();
CourseAttributes course = CourseAttributes.builder()
    .withId("course-id")
    .withName("Sample Course")
    .build();
logic.createCourse(course);
```

### Updating Entities

```java
// Use UpdateOptions
CourseUpdateOptions updateOptions = CourseUpdateOptions.builder()
    .withId(courseId)
    .withName("New Name")
    .build();
logic.updateCourse(updateOptions);
```

### Exception Handling

- `EntityNotFoundException` → HTTP 404
- `UnauthorizedAccessException` → HTTP 403
- `InvalidParametersException` → HTTP 400
- `EntityAlreadyExistsException` → HTTP 409

All 4XX responses must log at `warning` level or above.
All 5XX responses must log at `severe` level.

### Access Control

Access control is checked in UI layer using `GateKeeper`:

```java
gateKeeper.verifyAccessible(
    logic.getInstructorForGoogleId(courseId, userInfo.getId()),
    logic.getCourse(courseId),
    Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR
);
```

## Important Files

- `common/util/Const.java` - All constants
- `common/util/FieldValidator.java` - Input validation
- `common/util/SanitizationHelper.java` - XSS prevention
- `ui/webapi/GateKeeper.java` - Access control

## API Design Principles

- Design endpoints for resources (e.g., `/session` for `FeedbackSession`)
- Prefer multiple REST calls over single RPC calls
- Use HTTP request body (not URL parameters) for POST/PUT data
- Preprocess data to hide backend complexities (e.g., timestamps as UNIX epoch milliseconds)
- Backend is single source of truth - frontend types are generated from backend

## Testing

- Unit tests are in `src/test/` with matching package structure
- Use `DataBundle` for test data setup
- Test files follow naming: `*Test.java` or `*ActionTest.java`

## Things to Avoid

- Don't put business logic in Storage layer
- Don't check access control in Logic layer
- Don't expose entity classes outside Storage package
- Don't use raw entity objects - use `*Attributes` DTOs
- Don't skip input validation/sanitization
- Don't throw exceptions from UI layer - catch and transform them

# TEAMMATES Integration Tests - AI Agent Guide

## Overview

Integration tests that test component interactions and database operations.

## Structure

Similar to unit tests but focus on integration between components.

## Testing Framework

- **TestNG** - Test framework
- Configuration: `src/it/resources/testng-it.xml`

## Running Tests

```bash
# Run all integration tests
./gradlew integrationTests

# Run specific test class
./gradlew integrationTests --tests TestClassName
```

## Test Patterns

Integration tests verify:
- Component interactions (e.g., Logic + Storage)
- Database operations
- External service integrations
- End-to-end workflows within backend

## Best Practices

- Test component boundaries
- Verify data persistence
- Test transaction handling
- Clean up test data after tests
- Test realistic scenarios

## Things to Avoid

- Don't duplicate unit test coverage
- Don't test external services directly (use mocks when appropriate)
- Don't forget to clean up database state
- Don't create tests that depend on execution order

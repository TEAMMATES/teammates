# TEAMMATES Unit Tests - AI Agent Guide

## Overview

This directory contains unit tests for the Java backend code. Tests follow the same package structure as production code.

## Structure

Tests mirror the production package structure:
- `teammates.logic.core.*` tests → `teammates.logic.core.*` production code
- Test files are named `*Test.java` or `*ActionTest.java`

## Testing Framework

- **TestNG** - Test framework
- **JaCoCo** - Code coverage
- Configuration: `src/test/resources/testng-component.xml`

## Running Tests

```bash
# Run all component tests
./gradlew componentTests

# Run specific test class
./gradlew componentTests --tests TestClassName

# Generate coverage report
./gradlew componentTests jacocoReport
```

**Note**: Backend tests require a database instance and full-text search service to be running.

## Test Patterns

### Test Structure

```java
@Test
public void testMethodName_condition_expectedBehavior() {
    // Arrange
    DataBundle dataBundle = getTypicalDataBundle();
    
    // Act
    // Execute code under test
    
    // Assert
    // Verify results
}
```

### Test Naming

Follow format: `test<functionName>_<scenario>_<outcome>`

Examples:
```java
public void testGetComment_commentDoesNotExist_returnsNull()
public void testCreateComment_commentDoesNotExist_success()
public void testCreateComment_commentAlreadyExists_throwsEntityAlreadyExistsException()
```

### Using DataBundle

```java
DataBundle dataBundle = getTypicalDataBundle();
AccountsLogic accountsLogic = AccountsLogic.inst();

// Test with the data
InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
```

### Creating Test Data

Use `getTypicalX` functions in `BaseTestCase`:

```java
Account account = getTypicalAccount();
account.setEmail("newemail@teammates.com");

Student student = getTypicalStudent();
student.setName("New Student Name");
```

### Testing Actions

```java
@Test
public void testExecute_validRequest_returnsSuccess() {
    GetFeedbackSessionsAction action = getAction(validRequest);
    JsonResult result = getJsonResult(action);
    
    FeedbackSessionsData response = (FeedbackSessionsData) result.getOutput();
    assertEquals(expectedSize, response.getFeedbackSessions().size());
}
```

## Best Practices

1. **Write independent tests** - No dependencies between tests
2. **Use descriptive names** - `testMethodName_condition_expectedBehavior()`
3. **Keep tests short** - One assertion per test when possible
4. **Test edge cases** - Boundary values, null inputs, etc.
5. **Test error conditions** - Invalid inputs, missing entities, etc.
6. **Aim for 100% coverage** - Cover all code paths
7. **Include only relevant details** - Don't create unnecessary test data
8. **Favor readability over uniqueness** - Code duplication is acceptable in tests

## Testing Private Methods

- Prefer testing through public methods
- If needed, use reflection to access private methods
- Only test private methods if they're complex enough to warrant it

## Common Test Utilities

- `BaseTestCase` - Base class for all tests
- `getTypicalDataBundle()` - Standard test data
- `getAction()` - Helper to create action instances
- `getJsonResult()` - Helper to execute and get JSON results

## Things to Avoid

- Don't test implementation details - test behavior
- Don't create tests that depend on execution order
- Don't skip testing error conditions
- Don't forget to clean up test data
- Don't use production data in tests
- Don't create excessive abstractions - favor readability

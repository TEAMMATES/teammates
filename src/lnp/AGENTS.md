# TEAMMATES Load & Performance Tests - AI Agent Guide

## Overview

Load and performance tests to evaluate system stability under load.

## Purpose

- Test system under expected load
- Identify performance bottlenecks
- Verify system stability
- Test resource usage

## Running Tests

```bash
# Run load and performance tests
./gradlew lnpTests
```

## Best Practices

- Test realistic user scenarios
- Monitor resource usage
- Test under various load conditions
- Document performance characteristics

## Things to Avoid

- Don't run performance tests in CI by default (too expensive)
- Don't test unrealistic scenarios
- Don't forget to clean up after tests

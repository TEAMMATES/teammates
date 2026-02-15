# TEAMMATES - AI Agent Guide

## Project Overview

TEAMMATES is a free online tool for managing peer evaluations and feedback paths for students. It's a cloud-based service running on Google App Engine, serving hundreds of universities worldwide.

**Key Characteristics:**
- Large codebase (~150k-200k LoC) maintained by student contributors
- High quality standards with extensive automated testing
- Java backend (Google App Engine) + Angular 16 frontend
- Follows MVC pattern with RESTful API design

## Repository Structure

```
src/
├── main/          # Java backend (UI, Logic, Storage, Common components)
├── web/           # Angular/TypeScript frontend
├── test/          # Unit tests (Java)
├── e2e/           # End-to-end tests (Selenium)
├── it/            # Integration tests
├── lnp/           # Load and performance tests
└── client/        # Administrative client scripts
```

## Architecture

- **UI Component**: RESTful API endpoints (`ui.webapi`) + Angular frontend (`ui.website`)
- **Logic Component**: Business logic layer (`logic.core`, `logic.api`, `logic.external`)
- **Storage Component**: Data persistence layer using Google Cloud Datastore
- **Common Component**: Shared utilities, DTOs, and exceptions

## Key Principles

1. **Maintainability is top priority**: Prefer simple over complex
2. **Consistent code style**: Code should look like it was written by one person
3. **Comprehensive testing**: Strive for 100% test coverage
4. **Boy Scout Rule**: Leave code cleaner than you found it
5. **One issue per PR**: Keep changes focused and reversible

## Technology Stack

- **Backend**: Java, Google App Engine, Google Cloud Datastore
- **Frontend**: Angular 16, TypeScript, Bootstrap 5
- **Testing**: TestNG (Java), Jest (TypeScript), Selenium (E2E)
- **Build Tools**: Gradle, npm

## Important Files

- `docs/design.md` - Architecture and design patterns
- `docs/best-practices/` - Coding, testing, UI design guidelines
- `docs/development.md` - Development workflow
- `build.gradle` - Build configuration
- `package.json` - Frontend dependencies

## Coding Standards

- **Java**: Follow [OSS Generic Java Coding Standard](https://oss-generic.github.io/process/codingStandards/CodingStandard-Java.html)
- **TypeScript**: Follow ESLint configuration in `static-analysis/teammates-eslint.yml`
- **CSS/HTML**: Follow style guides in `docs/best-practices/`

## Common Patterns

- **REST API**: Design endpoints for resources, use HTTP methods appropriately
- **Exception Handling**: Use custom exceptions (`EntityNotFoundException`, `UnauthorizedAccessException`, etc.)
- **Data Transfer**: Use `*Attributes` classes from `common.datatransfer` package
- **Access Control**: Check in UI layer before calling Logic layer

## Testing Requirements

- **Unit Tests**: Write tests for all new code, aim for 100% coverage
- **Integration Tests**: Test component interactions
- **E2E Tests**: Test user workflows end-to-end
- **Test Independence**: Tests should not depend on each other

## When Making Changes

1. **Read existing code** in the same area to understand patterns
2. **Follow the existing structure** - don't reinvent the wheel
3. **Write tests first** (TDD approach, especially for bug fixes)
4. **Keep changes small** - one issue per PR
5. **Update documentation** if you're changing behavior or adding features

## Things to Avoid

- Don't create new patterns when existing ones work
- Don't skip tests for "simple" changes
- Don't mix refactoring with bug fixes in the same PR
- Don't use redundant comments - code should be self-explanatory
- Don't modify multiple unrelated areas in one change

## Reference Documentation

- [Developer Documentation](https://teammates.github.io/teammates)
- [Design Document](docs/design.md)
- [Contributing Guide](docs/CONTRIBUTING.md)
- [Best Practices](docs/best-practices/)

---

**Note**: For specific guidance on each subfolder, see the `agents.md` file in that subfolder.

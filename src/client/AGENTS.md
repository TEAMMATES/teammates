# TEAMMATES Client Scripts - AI Agent Guide

## Overview

Administrative scripts that connect directly to the backend for administrative tasks.

## Structure

```
client/
├── scripts/      # Administrative scripts
├── connector/    # Backend connection utilities
└── util/         # Helper utilities
```

## Purpose

Client scripts are used for:
- Data migration
- Administrative tasks
- Statistics calculation
- Direct database operations

## Usage

Run as standard Java applications, connecting directly to Google Cloud Datastore.

**Note**: It is not encouraged to compile and run scripts via command line; use supported IDEs instead.

## Best Practices

- Use for administrative tasks only
- Handle errors gracefully
- Log all operations
- Test scripts before running on production data
- Follow security best practices when accessing production

## Things to Avoid

- Don't use for regular application features
- Don't run on production without testing
- Don't skip error handling
- Don't forget to log operations

<frontmatter>
  title: "Architecture"
</frontmatter>

# Architecture

## Overview

<puml src="../diagrams/highlevelArchitecture.puml"/>

TEAMMATES is a web application with the following main components:

- **UI (Browser)**: A single-page application built with Angular, served over HTTP with subsequent data requests sent asynchronously.
- **UI (Server)**: A RESTful controller serving as the entry point for backend logic.
- **Logic**: The business logic of the application.
- **Storage**: The data persistence layer.
- **Common**: Shared utility code, helper classes, and data transfer objects used across the application.

The following additional components are used for testing:

- **Test Driver**: Automated regression testing using `TestNG` (Java) and `Jest` (TypeScript).
- **E2E**: End-to-end testing using `Selenium` to interact with the application via a web browser.

The diagram below shows how each component is organised into packages and the dependencies between them.

<puml src="../diagrams/packageDiagram.puml"/>

## UI Component

<puml src="../diagrams/UiComponent.puml"/>

The UI component is the entry point for all requests received by the application. Incoming requests pass through custom filters (e.g. `OriginCheckFilter`) before being forwarded to the appropriate `*Servlet` for processing.

The frontend (`ui::website`) is not a Java package — it is an Angular application consisting of HTML, SCSS, and TypeScript files, built into standard HTML, CSS, and JavaScript for the browser.

### Request Processing

<puml src="../diagrams/UiWorkflow.puml"/>

The initial page request is handled as follows:

1. `WebPageServlet` returns the built single web page (`index.html`).
1. The browser renders the page and executes the page scripts, triggering HTTP requests to the server.

Subsequent HTTP requests are handled as follows:

1. `WebApiServlet` uses `ActionFactory` to generate the matching `Action` object, e.g. `GetFeedbackSessionsAction`.
1. The `Action` object checks the user's access rights, performs the action, and packages the result into an `ActionResult` object.
1. `WebApiServlet` returns the result to the browser.

Static asset files (CSS, JS, images) are served directly.

The Web API is protected by two layers of access control:

- **Origin check**: Mitigates [CSRF attacks](https://owasp.org/www-community/attacks/csrf).
- **Authentication and authorization check**: Verifies the logged-in user has sufficient privileges.

### Template Method pattern

We use the [Template Method pattern](http://en.wikipedia.org/wiki/Template_method_pattern) to abstract the process flow into the `Action` classes.

### Policies

**Access control**: The UI is expected to check access control using the `GateKeeper` class.

**Transaction management**: A Hibernate session and transaction is opened per request.

**Request validation**: Request parameters are validated at this layer.

## Logic Component

<puml src="../diagrams/LogicComponent.puml"/>

The `Logic` component handles the business logic of TEAMMATES, including validating business constraints, sanitizing input from the UI, and integrating with third-party services such as email providers.

Package overview:

- **`logic.api`**: Provides the API of the component to be accessed by the UI.
- **`logic.core`**: Contains the core logic of the system.
- **`logic.external`**: Holds the logic of external services integration.

### Logic API

The Logic API is represented by the following classes:

- `Logic`: A [Facade](http://en.wikipedia.org/wiki/Facade_pattern) connecting to the various `*Logic` classes to handle business logic and access the `Storage` component.
- `UserProvision`: Retrieves user information from request cookies.
- `AuthProxy`: Provides authentication-related services.
- `EmailGenerator`: Generates emails to be sent.
- `EmailSender`: Sends emails using the provider configured in the build configuration.
- `TaskQueuer`: Queues tasks for deferred execution.
- `LogsProcessor`: Handles advanced logging beyond the standard logger.
- `RecaptchaVerifier`: Verifies reCAPTCHA tokens.

Many classes in this layer use environment-based implementations — connecting to real production services in staging/production and local alternatives in development.

### Policies

**API for creating entities**:

- Null parameters: Causes an assertion failure.
- Invalid parameters: Throws `InvalidParametersException`.
- Entity already exists: Throws `EntityAlreadyExistsException` (escalated from Storage level).

**API for retrieving entities**:

- Null parameters: Causes an assertion failure.
- Entity not found: Returns `null`, allowing read operations to double as existence checks.

**API for updating entities**:

- Entity not found: Throws `EntityDoesNotExistException`.
- Invalid parameters: Throws `InvalidParametersException`.

**API for deleting entities**:

- Entity not found: Fails silently — if it does not exist, it is as good as deleted.
- Cascade policy: When a parent entity is deleted, all entities with referential integrity to it are also deleted.

## Storage Component

<puml src="../diagrams/StorageComponent.puml"/>

The `Storage` component performs CRUD operations on data entities. It contains minimal logic beyond what is directly relevant to persistence. Cascade operations are handled at the database level.

It is responsible for:

- Enforcing database constraints on entities.
- Hiding database complexity from the `Logic` component.

Package overview:

- **`storage.api`**: Provides the API accessed by the Logic component.
- **`storage.entity`**: Persistable entity classes.

### Storage API

Represented by the `*Db` classes. These classes act as the bridge to the database.

### Policies

**API for creating**: Duplicate entities will result in a Hibernate constraint violation exception.

**API for retrieving**: Returns `null` if the entity does not exist.

**API for updating**: Missing or invalid entities will result in a Hibernate exception.

**API for deleting**: Delete operations are passed directly to the database. Existence checks and silent delete behaviour are handled by the `Logic` component.

## Common Component

The Common component contains common utilities used across TEAMMATES.

<puml src="../diagrams/CommonComponent.puml"/>

Package overview:

- **`common.util`**: Contains utility classes.
- **`common.exceptions`**: Contains custom exceptions.
- **`common.datatransfer`**: Contains data transfer objects.

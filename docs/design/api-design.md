<frontmatter>
  title: "API Design"
</frontmatter>

# API Design

## REST API Design Policies

- **Design endpoints for resources.** For example, `FeedbackSession` maps to `/session`. Use `GET`, `POST`, `PUT`, and `DELETE` to retrieve, create, update, and delete resources respectively.
- **Prefer multiple API calls over a single combined call.** Multiple API calls promote reuse of business logic and can be parallelized for better performance.
- **Separate access control logic from execution logic.** When an endpoint serves multiple purposes, use `Intent` to distinguish them. For example, instructors can access `/session` with intent `INSTRUCTOR_SUBMISSION` or `FULL_DETAIL`, each with different access requirements.
- **Prefer request body over URL parameters for `POST` and `PUT` requests.** URL parameters identify a resource; the request body describes what to do with it. Request body also supports richer data formats and validation. DTOs for API requests and responses are defined in the `request` and `output` packages respectively.
- **Preprocess data to hide backend complexities.** For example, timestamps are passed as UNIX epoch milliseconds in the API while represented as `Instant` in the backend. Fields that should be hidden for data privacy reasons should have corresponding methods in the request/output objects.
- **Endpoints should not be concerned with data presentation.** For example, when downloading a CSV, the frontend requests the same data as the web page and handles the conversion itself.

## Data Exchange

- The backend is the single source of truth for all data formats. Frontend types are generated from this.
  - `api-const.ts` is generated from important constants and API endpoint information in the backend.
  - `api-output.ts` and `api-request.ts` in the frontend are generated from DTO schemas in `output` and `request` packages.

## Exception Handling

- The UI component is responsible for catching all exceptions and returning a properly formed, user-friendly response including the status message and HTTP status code.
- Use custom runtime exception classes that map to HTTP status codes (e.g. `EntityNotFoundException` → 404, `UnauthorizedAccessException` → 403) rather than setting status codes manually in action classes.
- All `4XX` responses must be logged at `warning` level or above. All `5XX` responses must be logged at `severe` level.
  - `502` responses may skip `severe` logging if the upstream component already logged it.
- HTTP status codes should follow [RFC7231](https://tools.ietf.org/html/rfc7231) as closely as possible.

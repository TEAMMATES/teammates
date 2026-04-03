<frontmatter>
  title: "Authentication"
</frontmatter>

# Authentication

## Overview

TEAMMATES supports multiple authentication providers to verify user identity and establish a session. The authentication flow begins at the `LoginServlet`, which determines the appropriate provider based on the `provider` request parameter. Upon successful authentication, a session cookie is initialized using the verified user email.

The supported authentication providers are:

- **Google OAuth**
- **Microsoft Entra ID**
- **Firebase** (Google sign-in and email sign-in)
- **Dev Server Login** (development only)

## AuthProxy

`AuthProxy` is a singleton in the Logic API that provides authentication-related services. It wraps an `AuthService` implementation, selected at startup based on the application configuration:

- If `app.enable.devserver.login` is enabled (development mode) or the application is not configured to use Firebase, `AuthProxy` delegates to `EmptyAuthService`, which is a no-op implementation.
- If the application is configured to use Firebase in production, `AuthProxy` attempts to instantiate `FirebaseAuthService`. This requires a `firebase-credentials.json` file in the classpath. If the file is missing or initialization fails, it falls back to `EmptyAuthService`.

`AuthProxy` is only used for generating and sending login links to users who are logging in via the Firebase email sign-in flow. It is not involved in the Google OAuth, Microsoft Entra ID, or Dev Server Login flows.

## Authentication Flows

### Google OAuth

1. The user initiates login with `provider=google`. `LoginServlet` constructs a Google authorization URL with a redirect URI and an encrypted `AuthState` containing the next URL, session ID, and provider type.
2. The user is redirected to Google's sign-in page to select a Google account.
3. After authentication, Google redirects to `OAuth2CallbackServlet` with an authorization code in the URL.
4. The servlet exchanges the authorization code for an access token via Google's token endpoint.
5. The access token is used to request the user's email from Google's resource server.
6. The email is used to create a `UserInfoCookie`, which is set as the session cookie. The user is then redirected to the original destination.

### Microsoft Entra ID

1. The user initiates login with `provider=microsoft_entra`. `LoginServlet` constructs a Microsoft authorization URL with a redirect URI, the `openid` and `email` scopes, a `FORM_POST` response mode, and an encrypted `AuthState`.
2. The user is redirected to Microsoft's sign-in page to select a Microsoft account.
3. After authentication, Microsoft redirects to `OAuth2CallbackServlet` with an authorization code.
4. The servlet exchanges the authorization code for an access token via Microsoft's token endpoint.
5. The access token is used to request the user's email from Microsoft's resource server.
6. The email is used to create a `UserInfoCookie`, which is set as the session cookie. The user is then redirected to the original destination.

### Firebase

1. The user initiates login with `provider=firebase`. `LoginServlet` redirects to the frontend `login-page` component (`/web/login`).
2. The login page presents the available sign-in methods (Google or email).
   - **Google sign-in**: The frontend `AuthService` triggers a Google sign-in popup/redirect via the Firebase SDK. On success, the Firebase auth result provides an ID token.
   - **Email sign-in**: The user enters their email. A login link is generated via `AuthProxy` (backed by `FirebaseAuthService`) and sent to the user's email. When the user clicks the link, the frontend handles the email redirect result and obtains an ID token.
3. The frontend sends the ID token to `OAuth2CallbackServlet` as a query parameter.
4. The servlet verifies the ID token using the Firebase Admin SDK, extracting the user's email from the resulting Firebase token.
5. The Firebase user account is deleted immediately after extracting the email, as TEAMMATES does not persist Firebase user records.
6. The email is used to create a `UserInfoCookie`, which is set as the session cookie. The user is then redirected to the original destination.

### Dev Server Login

Dev Server Login is available only when the application is running in development mode (`app.env=development`) and `app.enable.devserver.login=true` in `build-dev.properties`.

1. The user initiates any login request. `LoginServlet` detects the dev server login configuration and redirects to `DevServerLoginServlet`.
2. `DevServerLoginServlet` serves the `devServerLoginPage.html`, which presents a simple form for the user to enter an email address.
3. The user submits the form. `DevServerLoginServlet` handles the POST request, creates a `UserInfoCookie` from the submitted email, and sets it as the session cookie without any external verification.
4. The user is redirected to the original destination.

## Key Classes

| Class | Location | Responsibility |
|---|---|---|
| `LoginServlet` | `ui.servlets` | Entry point for all login requests; routes to the correct provider |
| `OAuth2CallbackServlet` | `ui.servlets` | Handles callbacks from Google, Microsoft, and Firebase |
| `DevServerLoginServlet` | `ui.servlets` | Handles dev server login without external auth |
| `AuthServlet` | `ui.servlets` | Base class with shared helpers for auth servlets |
| `AuthProxy` | `logic.api` | Singleton providing auth services (login link generation) |
| `FirebaseAuthService` | `logic.external` | Firebase-backed auth service for email login links |
| `EmptyAuthService` | `logic.external` | No-op auth service fallback |
| `AuthState` | `ui.servlets` | Encapsulates state passed through OAuth redirects |
| `UserInfoCookie` | `common.datatransfer` | Represents the authenticated user session cookie |
| `login-page.component` | `web/app` | Frontend login page for the Firebase auth flow |

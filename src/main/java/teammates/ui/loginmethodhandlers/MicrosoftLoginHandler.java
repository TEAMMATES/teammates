package teammates.ui.loginmethodhandlers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;

import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.AuthorizationRequestUrlParameters;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import teammates.common.datatransfer.Provider;
import teammates.common.util.Config;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.ui.exception.AuthException;
import teammates.ui.output.LoginMethod;

import tools.jackson.core.type.TypeReference;

/**
 * Login method handler for Microsoft Entra login.
 */
public class MicrosoftLoginHandler implements LoginMethodHandler {

    private static final Logger log = Logger.getLogger();

    private static final Set<String> SCOPES = Set.of("openid", "email");
    private static final Set<String> MULTI_TENANT_AUTHORITIES = Set.of("common", "organizations");

    private final String expectedTenantId;

    public MicrosoftLoginHandler() {
        this(Config.OIDC_MICROSOFT_TENANT_ID);
    }

    // For dependency injection.
    MicrosoftLoginHandler(String expectedTenantId) {
        this.expectedTenantId = expectedTenantId;
    }

    @Override
    public String handleLogin(HttpServletRequest req, String nextUrl) throws IOException, AuthException {
        AuthState state = new AuthState(nextUrl, req.getSession().getId(), LoginMethod.MICROSOFT);
        String encryptedState = StringHelper.encrypt(JsonUtils.toCompactJson(state));

        String loginUrl = getAuthorizationRequestUrl(getRedirectUri(req), encryptedState);

        log.request(req, HttpStatus.SC_MOVED_TEMPORARILY, "Redirect to Microsoft Entra sign-in page");

        return loginUrl;
    }

    String getAuthorizationRequestUrl(String redirectUri, String encryptedState) throws AuthException {
        AuthorizationRequestUrlParameters parameters = AuthorizationRequestUrlParameters
                .builder(redirectUri, SCOPES)
                .state(encryptedState)
                .build();

        return getClientApplication().getAuthorizationRequestUrl(parameters).toString();
    }

    @Override
    public AuthResult handleCallback(HttpServletRequest req, AuthState state) throws IOException, AuthException {
        String error = req.getParameter("error");
        if (error != null) {
            throw new AuthException("Error in Microsoft Entra OAuth2 callback: " + error);
        }

        String code = req.getParameter("code");
        if (code == null) {
            throw new AuthException("Missing authorization code");
        }

        String sessionId = state.sessionId();
        if (!sessionId.equals(req.getSession().getId())) {
            String message = String.format("Different session ID: expected %s, got %s",
                    sessionId, req.getSession().getId());
            throw new AuthException(message);
        }

        IAuthenticationResult token = requestToken(code, getRedirectUri(req));
        Map<String, Object> claims = parseIdTokenClaims(token.idToken());

        String tenantId = getRequiredClaim(claims, "tid");
        if (!isExpectedTenantId(tenantId)) {
            throw new AuthException("Invalid tenant ID: expected " + expectedTenantId
                    + ", got " + tenantId);
        }

        String email = getRequiredClaim(claims, "email");
        if (StringHelper.isEmpty(email)) {
            // Require a valid email.
            // Microsoft Entra ID tokens may not always contain the email claim.
            throw new AuthException("Missing email claim in Microsoft Entra ID token");
        }

        String subject = getRequiredClaim(claims, "sub");

        return new AuthResult(Provider.MICROSOFT, subject, tenantId, email);
    }

    IAuthenticationResult requestToken(String code, String redirectUri) throws AuthException {
        try {
            AuthorizationCodeParameters parameters = AuthorizationCodeParameters
                    .builder(code, new URI(redirectUri))
                    .scopes(SCOPES)
                    .build();
            return getClientApplication().acquireToken(parameters).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthException("Interrupted while requesting Microsoft Entra token", e);
        } catch (ExecutionException | URISyntaxException e) {
            throw new AuthException("Failed to request Microsoft Entra token", e);
        }
    }

    private Map<String, Object> parseIdTokenClaims(String rawIdToken) throws AuthException {
        if (StringHelper.isEmpty(rawIdToken)) {
            throw new AuthException("Missing ID token");
        }

        String[] tokenParts = rawIdToken.split("\\.");
        if (tokenParts.length < 2) {
            throw new AuthException("Invalid ID token");
        }
        try {
            String claimsJson = new String(Base64.getUrlDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);
            return JsonUtils.fromJson(claimsJson, new TypeReference<>() {});
        } catch (IllegalArgumentException e) {
            throw new AuthException("Invalid ID token claims", e);
        }
    }

    private String getRequiredClaim(Map<String, Object> claims, String claimName) throws AuthException {
        Object rawValue = claims.get(claimName);
        String value = rawValue instanceof String ? (String) rawValue : null;
        if (StringHelper.isEmpty(value)) {
            throw new AuthException("Missing " + claimName + " claim");
        }
        return value;
    }

    private boolean isExpectedTenantId(String tenantId) {
        if (StringHelper.isEmpty(tenantId)) {
            return false;
        }
        // Either expecting multi-tenant or the specific tenant ID.
        return MULTI_TENANT_AUTHORITIES.contains(expectedTenantId)
                || expectedTenantId.equals(tenantId);
    }

    private ConfidentialClientApplication getClientApplication() throws AuthException {
        try {
            return ConfidentialClientApplication
                    .builder(Config.OIDC_MICROSOFT_CLIENT_ID,
                            ClientCredentialFactory.createFromSecret(Config.OIDC_MICROSOFT_CLIENT_SECRET))
                    .authority("https://login.microsoftonline.com/" + expectedTenantId)
                    .build();
        } catch (Exception e) {
            throw new AuthException("Failed to create Microsoft Entra client application", e);
        }
    }

    /**
     * Returns the redirect URI for the given HTTP servlet request.
     */
    private String getRedirectUri(HttpServletRequest req) throws AuthException {
        String requestUrl = req.getRequestURL().toString();
        if (Config.isDevServerLoginEnabled()) {
            requestUrl = requestUrl.replaceFirst("^https://", "http://");
        } else {
            requestUrl = requestUrl.replaceFirst("^http://", "https://");
        }
        try {
            URI uri = new URI(requestUrl);
            return new URI(uri.getScheme(), uri.getAuthority(), "/oauth2callback", null, null).toString();
        } catch (URISyntaxException e) {
            throw new AuthException("Invalid Microsoft Entra redirect URI", e);
        }
    }

}

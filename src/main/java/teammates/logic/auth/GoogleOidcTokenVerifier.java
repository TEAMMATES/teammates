package teammates.logic.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import teammates.common.util.Config;

/**
 * Verifies Google OIDC ID tokens and exposes {@code iss}, {@code sub}, and profile claims.
 */
public final class GoogleOidcTokenVerifier {

    private static final GoogleIdTokenVerifier VERIFIER = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(Config.OAUTH2_CLIENT_ID))
            .build();

    private GoogleOidcTokenVerifier() {
    }

    /**
     * Verifies a Google ID token string; returns null if invalid.
     */
    public static GoogleIdToken verify(String idTokenString) {
        try {
            return VERIFIER.verify(idTokenString);
        } catch (IOException | GeneralSecurityException e) {
            return null;
        }
    }
}

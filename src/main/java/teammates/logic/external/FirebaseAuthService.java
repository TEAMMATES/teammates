package teammates.logic.external;

import java.io.IOException;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import teammates.common.exception.AuthException;
import teammates.common.util.FileHelper;
import teammates.common.util.Logger;

/**
 * Provides Firebase Admin Auth authentication services.
 * <p>The FirebaseApp instance is initialized here.</p>
 * @see <a href="https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/auth/package-summary">Firebase Admin Auth</a>
 */
public class FirebaseAuthService implements AuthService {

    private static final Logger log = Logger.getLogger();

    public FirebaseAuthService() throws AuthException {
        try {
            InputStream firebaseCredentialsStream = FileHelper.getResourceAsStream("firebase-credentials.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(firebaseCredentialsStream))
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("Initialized FirebaseApp instance of name " + FirebaseApp.getInstance().getName());
        } catch (IOException | IllegalStateException | IllegalArgumentException e) {
            log.severe("Cannot initialize FirebaseApp: " + e.getMessage());
            throw new AuthException(e);
        }
    }

    @Override
    public String generateLoginLink(String userEmail, String continueUrl) {
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                .setUrl(continueUrl)
                .setHandleCodeInApp(true)
                .build();
        try {
            return FirebaseAuth.getInstance().generateSignInWithEmailLink(userEmail, actionCodeSettings);
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            log.severe(e.getMessage());
            return null;
        }
    }

}

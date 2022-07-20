package teammates.logic.api;

import teammates.common.exception.FirebaseException;
import teammates.common.util.Config;
import teammates.logic.external.EmptyFirebaseService;
import teammates.logic.external.FirebaseService;
import teammates.logic.external.GoogleFirebaseService;

/**
 * Used to initialize a FirebaseApp and provide Firebase services.
 */
public class FirebaseInstance {

    private static final FirebaseInstance instance = new FirebaseInstance();
    private final FirebaseService service;

    FirebaseInstance() {
        if (Config.IS_DEV_SERVER) {
            service = new EmptyFirebaseService();
        } else {
            service = new GoogleFirebaseService(Config.APP_FIREBASE_SERVICEACCOUNT_FILENAME);
        }
    }

    public static FirebaseInstance inst() {
        return instance;
    }

    /**
     * Generates a login link encoded with {@code userEmail} and {@code continueUrl}.
     */
    public String generateLoginLink(String userEmail, String continueUrl) {
        return service.generateLoginLink(userEmail, continueUrl);
    }

    /**
     * Deletes the user with the specified {@code userEmail}.
     */
    public void deleteUser(String userEmail) throws FirebaseException {
        service.deleteUser(userEmail);
    }

}

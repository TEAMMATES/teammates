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
        FirebaseService fs;
        if (Config.IS_DEV_SERVER) {
            fs = new EmptyFirebaseService();
        } else {
            try {
                fs = new GoogleFirebaseService();
            } catch (FirebaseException e) {
                fs = new EmptyFirebaseService();
            }
        }
        service = fs;
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

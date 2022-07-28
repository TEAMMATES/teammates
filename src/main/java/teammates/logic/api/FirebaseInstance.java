package teammates.logic.api;

import teammates.common.exception.FirebaseException;
import teammates.common.util.Config;
import teammates.logic.external.EmptyAuthService;
import teammates.logic.external.AuthService;
import teammates.logic.external.FirebaseService;

/**
 * Provides Firebase services.
 */
public class FirebaseInstance {

    private static final FirebaseInstance instance = new FirebaseInstance();
    private final AuthService service;

    FirebaseInstance() {
        AuthService fs;
        if (Config.IS_DEV_SERVER) {
            fs = new EmptyAuthService();
        } else {
            try {
                fs = new FirebaseService();
            } catch (FirebaseException e) {
                fs = new EmptyAuthService();
            }
        }
        service = fs;
    }

    public static FirebaseInstance inst() {
        return instance;
    }

    /**
     * Generates a Firebase login link unique to the logging in user.
     * @param userEmail email of the logging in user.
     * @param continueUrl URL upon successful login.
     * @return null if error occurs while generating the login link.
     */
    public String generateLoginLink(String userEmail, String continueUrl) {
        return service.generateLoginLink(userEmail, continueUrl);
    }

    /**
     * Deletes the Firebase user with the specified {@code userEmail}.
     * @throws FirebaseException if error occurs while deleting the user.
     */
    public void deleteUser(String userEmail) throws FirebaseException {
        service.deleteUser(userEmail);
    }

}

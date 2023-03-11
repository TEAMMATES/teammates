package teammates.logic.api;

import teammates.common.exception.AuthException;
import teammates.common.util.Config;
import teammates.logic.external.AuthService;
import teammates.logic.external.EmptyAuthService;
import teammates.logic.external.FirebaseAuthService;

/**
 * Provides authentication-related services.
 */
public class AuthProxy {

    private static final AuthProxy PROXY = new AuthProxy();
    private final AuthService service;

    AuthProxy() {
        AuthService fs;
        if (Config.ENABLE_DEVSERVER_LOGIN) {
            fs = new EmptyAuthService();
        } else {
            try {
                fs = new FirebaseAuthService();
            } catch (AuthException e) {
                fs = new EmptyAuthService();
            }
        }
        service = fs;
    }

    public static AuthProxy inst() {
        return PROXY;
    }

    public AuthService getService() {
        return service;
    }

    /**
     * Generates login link for the logging in user.
     * @param userEmail email of the logging in user.
     * @param continueUrl URL upon successful login.
     * @return null if error occurs while generating the login link.
     */
    public String generateLoginLink(String userEmail, String continueUrl) {
        return service.generateLoginLink(userEmail, continueUrl);
    }

    /**
     * Deletes user with the specified {@code userEmail}.
     * <p>Fails silently if no such user</p>
     * @throws AuthException if error occurs while deleting the user.
     */
    public void deleteUser(String userEmail) throws AuthException {
        service.deleteUser(userEmail);
    }

}

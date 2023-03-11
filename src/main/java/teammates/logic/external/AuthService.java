package teammates.logic.external;

import teammates.common.exception.AuthException;

/**
 * Interface that provides authentication-related services.
 */
public interface AuthService {

    /**
     * Generates login link for the logging in user.
     * @param userEmail email of the logging in user.
     * @param continueUrl URL upon successful login.
     * @return null if error occurs while generating the login link.
     */
    String generateLoginLink(String userEmail, String continueUrl);

    /**
     * Deletes user with the specified {@code userEmail}.
     * @throws AuthException if error occurs while deleting the user.
     */
    void deleteUser(String userEmail) throws AuthException;

}

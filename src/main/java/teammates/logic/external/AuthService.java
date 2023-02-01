package teammates.logic.external;

import teammates.common.exception.AuthException;
import teammates.common.util.LoginLinkOptions;

/**
 * Interface that provides authentication-related services.
 */
public interface AuthService {

    /**
     * Generates login link for the logging in user.
     * @param loginLinkOptions options to generate the login link.
     * @return null if error occurs while generating the login link.
     */
    String generateLoginLink(LoginLinkOptions loginLinkOptions);

    /**
     * Deletes user with the specified {@code userEmail}.
     * @throws AuthException if error occurs while deleting the user.
     */
    void deleteUser(String userEmail) throws AuthException;

}

package teammates.logic.external;

import teammates.common.exception.AuthException;
import teammates.common.util.LoginLinkOptions;

/**
 * Service that does not execute any authentication operations.
 */
public class EmptyAuthService implements AuthService {

    @Override
    public String generateLoginLink(LoginLinkOptions loginLinkOptions) {
        return "";
    }

    @Override
    public void deleteUser(String userEmail) throws AuthException {
        // No user deleted
    }

}

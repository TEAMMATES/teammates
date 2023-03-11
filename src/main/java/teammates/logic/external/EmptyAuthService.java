package teammates.logic.external;

import teammates.common.exception.AuthException;

/**
 * Service that does not execute any authentication operations.
 */
public class EmptyAuthService implements AuthService {

    @Override
    public String generateLoginLink(String userEmail, String continueUrl) {
        return "";
    }

    @Override
    public void deleteUser(String userEmail) throws AuthException {
        // No user deleted
    }

}

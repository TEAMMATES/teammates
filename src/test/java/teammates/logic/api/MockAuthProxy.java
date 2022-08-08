package teammates.logic.api;

import teammates.common.exception.AuthException;

/**
 * Allows mocking of the {@link AuthProxy} used in production.
 */
public class MockAuthProxy extends AuthProxy {

    @Override
    public String generateLoginLink(String userEmail, String continueUrl) {
        return "";
    }

    @Override
    public void deleteUser(String userEmail) throws AuthException {
        // No user deleted
    }

}

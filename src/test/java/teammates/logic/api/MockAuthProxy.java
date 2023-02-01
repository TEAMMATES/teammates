package teammates.logic.api;

import teammates.common.exception.AuthException;
import teammates.common.util.LoginLinkOptions;

/**
 * Allows mocking of the {@link AuthProxy} used in production.
 */
public class MockAuthProxy extends AuthProxy {

    @Override
    public String generateLoginLink(LoginLinkOptions loginLinkOptions) {
        return "";
    }

    @Override
    public void deleteUser(String userEmail) throws AuthException {
        // No user deleted
    }

}

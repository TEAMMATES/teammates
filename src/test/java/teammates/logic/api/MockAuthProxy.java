package teammates.logic.api;

/**
 * Allows mocking of the {@link AuthProxy} used in production.
 */
public class MockAuthProxy extends AuthProxy {

    @Override
    public String generateLoginLink(String userEmail, String continueUrl) {
        return "";
    }

    @Override
    public boolean isLoginEmailEnabled() {
        // Set to true for testing
        return true;
    }

}

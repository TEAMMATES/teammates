package teammates.logic.api;

import teammates.common.exception.FirebaseException;

/**
 * Allows mocking of the {@link FirebaseInstance} used in production.
 */
public class MockFirebaseInstance extends FirebaseInstance {

    @Override
    public String generateLoginLink(String userEmail, String continueUrl) {
        return "";
    }

    @Override
    public void deleteUser(String userEmail) throws FirebaseException {
        // No Firebase user to delete
    }

}

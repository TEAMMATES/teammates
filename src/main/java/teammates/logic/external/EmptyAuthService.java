package teammates.logic.external;

import teammates.common.exception.FirebaseException;

/**
 * Service that does not execute any Firebase operations.
 */
public class EmptyAuthService implements AuthService {

    @Override
    public String generateLoginLink(String userEmail, String continueUrl) {
        return "";
    }

    @Override
    public void deleteUser(String userEmail) throws FirebaseException {
        // No Firebase user to delete
    }

}

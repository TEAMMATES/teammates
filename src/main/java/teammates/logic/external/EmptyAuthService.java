package teammates.logic.external;

/**
 * Service that does not execute any authentication operations.
 */
public class EmptyAuthService implements AuthService {

    @Override
    public String generateLoginLink(String userEmail, String continueUrl) {
        return "";
    }

}

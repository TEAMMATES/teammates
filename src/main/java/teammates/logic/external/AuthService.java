package teammates.logic.external;

import teammates.common.exception.FirebaseException;

/**
 * Interface that provides Firebase services.
 */
public interface AuthService {

    /**
     * Generates a Firebase login link unique to the logging in user.
     * @param userEmail email of the logging in user.
     * @param continueUrl URL upon successful login.
     * @return null if error occurs while generating the login link.
     */
    String generateLoginLink(String userEmail, String continueUrl);

    /**
     * Deletes the Firebase user with the specified {@code userEmail}.
     * @throws FirebaseException if error occurs while deleting the user.
     */
    void deleteUser(String userEmail) throws FirebaseException;

}

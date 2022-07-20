package teammates.logic.external;

import teammates.common.exception.FirebaseException;

/**
 * An interface that provides Firebase services.
 */
public interface FirebaseService {

    /**
     * Generates a login link encoded with {@code userEmail} and {@code continueUrl}.
     */
    String generateLoginLink(String userEmail, String continueUrl);

    /**
     * Deletes the user with the specified {@code userEmail}.
     */
    void deleteUser(String userEmail) throws FirebaseException;

}

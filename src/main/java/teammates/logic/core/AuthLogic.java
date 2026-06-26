package teammates.logic.core;

import java.util.Objects;

import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.SessionKey;
import teammates.common.datatransfer.SessionKeyAccessDecision;
import teammates.common.datatransfer.SessionKeyAccessResult;
import teammates.common.datatransfer.SessionKeyValidationResult;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.KeyUtil;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Handles operations related to authentication and authorization.
 */
public final class AuthLogic {
    private static final AuthLogic instance = new AuthLogic();

    private UsersLogic usersLogic;

    private AuthLogic() {
        // prevent initialization
    }

    /**
     * Initializes the required dependencies.
     */
    public void initLogicDependencies(UsersLogic usersLogic) {
        this.usersLogic = usersLogic;
    }

    public static AuthLogic inst() {
        return instance;
    }

    /**
     * Returns the student associated with the given authentication context and
     * course ID.
     *
     * <p>
     * If a valid registration key is present, it returns the unregistered student
     * from the authentication context.
     * Otherwise, it retrieves the student from the database linked to the account
     * and course ID.
     */
    public Student getStudentFromAuthContext(AuthContext authContext, String courseId) {
        if (authContext.regKeyStudent() != null) {
            return authContext.regKeyStudent();
        }

        Account account = authContext.account();
        if (account == null) {
            return null;
        }

        return usersLogic.getStudentByAccountId(account.getId(), courseId);
    }

    /**
     * Returns the instructor associated with the given authentication context and
     * course ID.
     *
     * <p>
     * Retrieves the instructor from the database linked to the account and course ID.
     * Instructors cannot authenticate via registration key.
     */
    public Instructor getInstructorFromAuthContext(AuthContext authContext, String courseId) {
        Account account = authContext.account();
        if (account == null) {
            return null;
        }

        return usersLogic.getInstructorByAccountId(account.getId(), courseId);
    }

    /**
     * Returns the session key access result based on the current account and the provided encrypted session key.
     */
    public SessionKeyAccessResult getSessionKeyAccessResult(Account currentAccount, String encryptedKey) {
        try {
            if (encryptedKey == null) {
                return new SessionKeyAccessResult(
                    SessionKeyAccessDecision.INVALID_KEY,
                    "This session link is invalid.");
            }

            SessionKeyValidationResult result = validateEncryptedSessionKey(encryptedKey);
            Student student = result.student();

            if (student.getAccount() == null) {
                return new SessionKeyAccessResult(SessionKeyAccessDecision.ALLOW_UNREGISTERED, null);
            }

            if (currentAccount == null) {
                return new SessionKeyAccessResult(
                        SessionKeyAccessDecision.SIGN_IN_REQUIRED,
                        "This session link is associated with an account. Please sign in to continue.");
            }

            if (Objects.equals(currentAccount, student.getAccount())) {
                return new SessionKeyAccessResult(SessionKeyAccessDecision.ALLOW_SIGNED_IN, null);
            }

            return new SessionKeyAccessResult(
                    SessionKeyAccessDecision.SIGN_IN_WITH_ANOTHER_ACCOUNT,
                    "This session link is associated with another account. Please sign in with that account.");
        } catch (UnauthorizedAccessException e) {
            return new SessionKeyAccessResult(
                    SessionKeyAccessDecision.INVALID_KEY,
                    "This session link is invalid.");
        }
    }

    /**
     * Validates the provided encrypted session key and returns the associated student and session key.
     */
    public SessionKeyValidationResult validateEncryptedSessionKey(String encryptedKey)
            throws UnauthorizedAccessException {
        SessionKey sessionKey;
        try {
            sessionKey = KeyUtil.decryptSessionKey(encryptedKey);
            sessionKey.validate();
        } catch (InvalidParametersException | IllegalArgumentException e) {
            throw new UnauthorizedAccessException("Invalid encrypted session key", e);
        }

        Student student = usersLogic.getStudent(sessionKey.userId());
        if (student == null) {
            throw new UnauthorizedAccessException("Invalid encrypted session key: no student found");
        }

        if (!sessionKey.regKey().equals(student.getRegKey())) {
            throw new UnauthorizedAccessException("Invalid encrypted session key");
        }

        return new SessionKeyValidationResult(student, sessionKey);
    }
}

package teammates.logic.core;

import java.util.Objects;

import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.CourseJoinKey;
import teammates.common.datatransfer.CourseJoinKeyAccessDecision;
import teammates.common.datatransfer.CourseJoinKeyAccessResult;
import teammates.common.datatransfer.SessionKey;
import teammates.common.datatransfer.SessionKeyAccessDecision;
import teammates.common.datatransfer.SessionKeyAccessResult;
import teammates.common.datatransfer.SessionKeyValidationResult;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.KeyUtil;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;

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
     * If a valid session key is present, it returns the unregistered student
     * from the authentication context.
     * Otherwise, it retrieves the student from the database linked to the account
     * and course ID.
     */
    public Student getStudentFromAuthContext(AuthContext authContext, String courseId) {
        if (authContext.sessionKeyStudent() != null) {
            return authContext.sessionKeyStudent();
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
     * Instructors cannot authenticate via session key.
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
        } catch (InvalidParametersException e) {
            return new SessionKeyAccessResult(
                    SessionKeyAccessDecision.INVALID_KEY,
                    "This session link is invalid.");
        }
    }

    /**
     * Returns the course join key access result based on the current account and the provided encrypted course join key.
     */
    public CourseJoinKeyAccessResult getCourseJoinKeyAccessResult(Account currentAccount, String encryptedKey) {
        if (encryptedKey == null) {
            return new CourseJoinKeyAccessResult(
                    CourseJoinKeyAccessDecision.INVALID_KEY,
                    "This course join link is invalid.");
        }

        User user;
        try {
            user = validateEncryptedCourseJoinKey(encryptedKey);
        } catch (InvalidParametersException e) {
            return new CourseJoinKeyAccessResult(
                    CourseJoinKeyAccessDecision.INVALID_KEY,
                    "This course join link is invalid.");
        }

        if (user.getAccount() != null) {
            return new CourseJoinKeyAccessResult(CourseJoinKeyAccessDecision.ALREADY_JOINED, null);
        }

        if (currentAccount == null) {
            return new CourseJoinKeyAccessResult(
                    CourseJoinKeyAccessDecision.SIGN_IN_REQUIRED,
                    "Please sign in to join this course.");
        }

        return new CourseJoinKeyAccessResult(CourseJoinKeyAccessDecision.VALID, null);
    }

    /**
     * Validates the provided encrypted course join key and returns the associated user.
     */
    public User validateEncryptedCourseJoinKey(String encryptedKey) throws InvalidParametersException {
        CourseJoinKey joinKey;
        try {
            joinKey = KeyUtil.decryptCourseJoinKey(encryptedKey);
            joinKey.validate();
        } catch (InvalidParametersException | IllegalArgumentException e) {
            throw new InvalidParametersException("Invalid encrypted course join key", e);
        }

        User user = usersLogic.getUser(joinKey.userId());
        if (user == null) {
            throw new InvalidParametersException("Invalid encrypted course join key: no user found");
        }

        if (!joinKey.regKey().equals(user.getRegKey())) {
            throw new InvalidParametersException("Invalid encrypted course join key");
        }

        return user;
    }

    /**
     * Validates the provided encrypted session key and returns the associated student and session key.
     */
    public SessionKeyValidationResult validateEncryptedSessionKey(String encryptedKey)
            throws InvalidParametersException {
        SessionKey sessionKey;
        try {
            sessionKey = KeyUtil.decryptSessionKey(encryptedKey);
            sessionKey.validate();
        } catch (InvalidParametersException | IllegalArgumentException e) {
            throw new InvalidParametersException("Invalid encrypted session key", e);
        }

        Student student = usersLogic.getStudent(sessionKey.userId());
        if (student == null) {
            throw new InvalidParametersException("Invalid encrypted session key: no student found");
        }

        if (!sessionKey.regKey().equals(student.getRegKey())) {
            throw new InvalidParametersException("Invalid encrypted session key");
        }

        return new SessionKeyValidationResult(student, sessionKey);
    }
}

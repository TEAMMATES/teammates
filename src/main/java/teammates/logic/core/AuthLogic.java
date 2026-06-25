package teammates.logic.core;

import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.SessionKey;
import teammates.common.datatransfer.SessionKeyAccessDecision;
import teammates.common.datatransfer.SessionKeyAccessResult;
import teammates.common.datatransfer.SessionKeyValidationResult;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
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
    private AccountsLogic accountsLogic;

    private AuthLogic() {
        // prevent initialization
    }

    /**
     * Initializes the required dependencies.
     */
    public void initLogicDependencies(UsersLogic usersLogic, AccountsLogic accountsLogic) {
        this.usersLogic = usersLogic;
        this.accountsLogic = accountsLogic;
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
     * Gets the access decision for a student session key request.
     */
    public SessionKeyAccessResult getSessionKeyAccessResult(HttpServletRequest req) {
        try {
            SessionKeyValidationResult result = validateEncryptedSessionKey(req);
            Student student = result.student();
            Account currentAccount = getAccountFromRequest(req);

            if (student.getAccount() == null) {
                return new SessionKeyAccessResult(SessionKeyAccessDecision.ALLOW, null);
            }

            if (currentAccount == null) {
                return new SessionKeyAccessResult(
                        SessionKeyAccessDecision.SIGN_IN_REQUIRED,
                        "This session link is associated with an account. Please sign in to continue.");
            }

            if (Objects.equals(currentAccount, student.getAccount())) {
                return new SessionKeyAccessResult(SessionKeyAccessDecision.ALLOW, null);
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

    private Account getAccountFromRequest(HttpServletRequest req) {
        String cookie = HttpRequestHelper.getCookieValueFromRequest(req, Const.SecurityConfig.AUTH_COOKIE_NAME);
        UserInfoCookie uic = UserInfoCookie.fromCookie(cookie);
        if (uic != null && uic.isValid()) {
            return accountsLogic.getAccount(uic.getAccountId());
        }
        return null;
    }

    /**
     * Validates the encrypted session key from the request and returns the associated student and session key.
     */
    public SessionKeyValidationResult validateEncryptedSessionKey(HttpServletRequest req)
            throws UnauthorizedAccessException {
        String encryptedKey = req.getParameter(Const.ParamsNames.REGKEY);
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

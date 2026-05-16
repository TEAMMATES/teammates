package teammates.logic.api;

import java.util.UUID;

import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.UserInfoCookie;

/**
 * Allows mocking of the {@link UserProvision} API used in production.
 *
 * <p>Instead of getting user information from the authentication service,
 * the API will return pre-determined information instead.
 */
public class MockUserProvision extends UserProvision {
    private static final UUID MOCK_ACCOUNT_ID = UUID.randomUUID();
    private AuthContext mockUser = new AuthContext(
            "user.id", MOCK_ACCOUNT_ID, false, false, false, false
    );
    private boolean isLoggedIn;
    private boolean isAutomatedServiceMode;
    private boolean isMaintainer;
    private boolean isAdmin;
    private boolean isInstructor;
    private boolean isStudent;

    private AuthContext loginUser(String userId, boolean isAdmin, boolean isInstructor, boolean isStudent,
            boolean isMaintainer) {
        isLoggedIn = true;
        mockUser = new AuthContext(userId, MOCK_ACCOUNT_ID, isAdmin, isInstructor, isStudent, isMaintainer);
        return mockUser;
    }

    /**
     * Login as a user without admin rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginUser(String userId) {
        return loginUser(userId, false, false, false, false);
    }

    /**
     * Login as a user with admin rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginAsAdmin(String userId) {
        return loginUser(userId, true, false, false, false);
    }

    /**
     * Login as a user with instructor rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginAsInstructor(String userId) {
        return loginUser(userId, false, true, false, false);
    }

    /**
     * Login as a user with student rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginAsStudent(String userId) {
        return loginUser(userId, false, false, true, false);
    }

    /**
     * Login as a user with student and instructor rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginAsStudentInstructor(String userId) {
        return loginUser(userId, false, true, true, false);
    }

    /**
     * Login as a user with maintainer rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginAsMaintainer(String userId) {
        return loginUser(userId, false, false, false, true);
    }

    /**
     * Logs in as an automated service (cron/worker).
     */
    public void loginAsAutomatedService() {
        isAutomatedServiceMode = true;
    }

    public boolean isAutomatedServiceMode() {
        return isAutomatedServiceMode;
    }

    /**
     * Removes the logged-in user information.
     */
    public void logoutUser() {
        isLoggedIn = false;
        isAutomatedServiceMode = false;
    }

    @Override
    public AuthContext getCurrentUserContext(UserInfoCookie uic) {
        return getCurrentLoggedInUserContext(uic);
    }

    @Override
    public AuthContext getCurrentLoggedInUserContext(UserInfoCookie uic) {
        return isLoggedIn ? mockUser : null;
    }

    @Override
    public AuthContext getMasqueradeUserContext(String googleId) {
        return new AuthContext(googleId, MOCK_ACCOUNT_ID, isAdmin, isInstructor, isStudent, isMaintainer);
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setInstructor(boolean isInstructor) {
        this.isInstructor = isInstructor;
    }

    public void setStudent(boolean isStudent) {
        this.isStudent = isStudent;
    }

    public void setMaintainer(boolean isMaintainer) {
        this.isMaintainer = isMaintainer;
    }
}

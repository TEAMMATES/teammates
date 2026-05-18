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
            "user.id", MOCK_ACCOUNT_ID, false, false
    );
    private boolean isLoggedIn;
    private boolean isAutomatedServiceMode;
    private boolean isMaintainer;
    private boolean isAdmin;

    private AuthContext loginUser(String userId, boolean isAdmin, boolean isMaintainer) {
        this.isLoggedIn = true;
        mockUser = new AuthContext(userId, MOCK_ACCOUNT_ID, isAdmin, isMaintainer);
        return mockUser;
    }

    /**
     * Login as a user without admin rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginUser(String userId) {
        return loginUser(userId, false, false);
    }

    /**
     * Login as a user with admin rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginAsAdmin(String userId) {
        return loginUser(userId, true, false);
    }

    /**
     * Login as a user with maintainer rights.
     *
     * @return The auth context after login process
     */
    public AuthContext loginAsMaintainer(String userId) {
        return loginUser(userId, false, true);
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
        return new AuthContext(googleId, MOCK_ACCOUNT_ID, isAdmin, isMaintainer);
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setMaintainer(boolean isMaintainer) {
        this.isMaintainer = isMaintainer;
    }

    /**
     * Returns the UUID used for all mock accounts in tests.
     */
    public UUID getMockAccountId() {
        return MOCK_ACCOUNT_ID;
    }
}

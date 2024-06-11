package teammates.logic.api;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;

/**
 * Allows mocking of the {@link UserProvision} API used in production.
 *
 * <p>Instead of getting user information from the authentication service,
 * the API will return pre-determined information instead.
 */
public class MockUserProvision extends UserProvision {

    private UserInfo mockUser = new UserInfo("user.id");
    private boolean isLoggedIn;

    private UserInfo loginUser(String userId, boolean isAdmin) {
        isLoggedIn = true;
        mockUser.id = userId;
        mockUser.isAdmin = isAdmin;
        return getCurrentUser(null);
    }

    /**
     * Adds a logged-in user without admin rights.
     *
     * @return The user info after login process
     */
    public UserInfo loginUser(String userId) {
        return loginUser(userId, false);
    }

    private UserInfo loginUserWithTransaction(String userId, boolean isAdmin) {
        isLoggedIn = true;
        mockUser.id = userId;
        mockUser.isAdmin = isAdmin;
        return getCurrentUserWithTransaction(null);
    }

    /**
     * Adds a logged-in user without admin rights.
     *
     * @return The user info after login process
     */
    public UserInfo loginUserWithTransaction(String userId) {
        return loginUserWithTransaction(userId, false);
    }

    /**
     * Adds a logged-in user as an admin.
     *
     * @return The user info after login process
     */
    public UserInfo loginAsAdmin(String userId) {
        return loginUser(userId, true);
    }

    /**
     * Adds a logged-in user as an admin.
     *
     * @return The user info after login process
     */
    public UserInfo loginAsAdminWithTransaction(String userId) {
        return loginUserWithTransaction(userId, true);
    }

    /**
     * Removes the logged-in user information.
     */
    public void logoutUser() {
        isLoggedIn = false;
    }

    @Override
    public UserInfo getCurrentLoggedInUser(UserInfoCookie uic) {
        return isLoggedIn ? mockUser : null;
    }

}

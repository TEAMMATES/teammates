package teammates.sqllogic.api;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.logic.api.UserProvision;

/**
 * Allows mocking of the {@link UserProvision} API used in production.
 *
 * <p>Instead of getting user information from the authentication service,
 * the API will return pre-determined information instead.
 */
public class MockUserProvision extends UserProvision {
    private UserInfo mockUser = new UserInfo("user.id");
    private boolean isLoggedIn;

    private UserInfo loginUser(String userId, boolean isAdmin, boolean isInstructor, boolean isStudent,
            boolean isMaintainer) {
        isLoggedIn = true;
        mockUser.id = userId;
        mockUser.isAdmin = isAdmin;
        mockUser.isInstructor = isInstructor;
        mockUser.isStudent = isStudent;
        mockUser.isMaintainer = isMaintainer;
        return mockUser;
    }

    /**
     * Adds a logged-in user without admin rights.
     *
     * @return The user info after login process
     */
    public UserInfo loginUser(String userId) {
        return loginUser(userId, false, false, false, false);
    }

    /**
     * Adds a logged-in user as an admin.
     *
     * @return The user info after login process
     */
    public UserInfo loginAsAdmin(String userId) {
        return loginUser(userId, true, false, false, false);
    }

    /**
     * Adds a logged-in user as an instructor.
     *
     * @return The user info after login process
     */
    public UserInfo loginAsInstructor(String userId) {
        return loginUser(userId, false, true, false, false);
    }

    /**
     * Adds a logged-in user as a student.
     *
     * @return The user info after login process
     */
    public UserInfo loginAsStudent(String userId) {
        return loginUser(userId, false, false, true, false);
    }

    /**
     * Adds a logged-in user as a student instructor.
     *
     * @return The user info after login process
     */
    public UserInfo loginAsStudentInstructor(String userId) {
        return loginUser(userId, false, true, true, false);
    }

    /**
     * Adds a logged-in user as a maintainer.
     *
     * @return The user info after login process
     */
    public UserInfo loginAsMaintainer(String userId) {
        return loginUser(userId, false, false, false, true);
    }

    /**
     * Removes the logged-in user information.
     */
    public void logoutUser() {
        isLoggedIn = false;
    }

    @Override
    public UserInfo getCurrentUser(UserInfoCookie uic) {
        return getCurrentLoggedInUser(uic);
    }

    @Override
    public UserInfo getCurrentLoggedInUser(UserInfoCookie uic) {
        return isLoggedIn ? mockUser : null;
    }

}

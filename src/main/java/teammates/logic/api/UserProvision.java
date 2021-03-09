package teammates.logic.api;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import teammates.common.datatransfer.UserInfo;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.StudentsLogic;

/**
 * Handles logic related to username and user role provisioning.
 */
public class UserProvision {

    private static UserService userService = UserServiceFactory.getUserService();

    private static final AccountsLogic accountsLogic = AccountsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    /**
     * Gets the information of the current logged in user.
     */
    public UserInfo getCurrentUser() {
        UserInfo user = getCurrentLoggedInUser();

        if (user == null) {
            return null;
        }

        String userId = user.id;
        user.isInstructor = accountsLogic.isAccountAnInstructor(userId);
        user.isStudent = studentsLogic.isStudentInAnyCourse(userId);
        return user;
    }

    protected UserInfo getCurrentLoggedInUser() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return null;
        }

        UserInfo userInfo = new UserInfo(user.getNickname());
        userInfo.isAdmin = userService.isUserAdmin();
        return userInfo;
    }

    /**
     * Gets the information of the current masqueraded user.
     */
    public UserInfo getMasqueradeUser(String googleId) {
        UserInfo userInfo = new UserInfo(googleId);
        userInfo.isAdmin = false;
        userInfo.isInstructor = accountsLogic.isAccountAnInstructor(googleId);
        userInfo.isStudent = studentsLogic.isStudentInAnyCourse(googleId);
        return userInfo;
    }

    /**
     * Gets the information of a user who has administrator role only.
     */
    public UserInfo getAdminOnlyUser(String userId) {
        UserInfo userInfo = new UserInfo(userId);
        userInfo.isAdmin = true;
        return userInfo;
    }

}

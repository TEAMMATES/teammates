package teammates.logic.api;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import teammates.common.datatransfer.UserInfo;
import teammates.common.util.Config;
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

    /**
     * Gets the login URL with the specified page as the redirect after logging in (if successful).
     */
    public String getLoginUrl(String redirectPage) {
        UserInfo user = getCurrentLoggedInUser();

        if (user == null) {
            return userService.createLoginURL(redirectPage);
        }
        return redirectPage;
    }

    /**
     * Gets the logout URL with the specified page as the redirect after logging out.
     */
    public String getLogoutUrl(String redirectPage) {
        return userService.createLogoutURL(redirectPage);
    }

    protected UserInfo getCurrentLoggedInUser() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return null;
        }

        UserInfo userInfo = new UserInfo(user.getNickname());
        userInfo.isAdmin = userService.isUserAdmin();
        userInfo.isSeniorDeveloper = this.isUserSeniorDeveloper(userInfo.getId());
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

    /**
     * Checks whether the given id is in the list of senior developers' id.
     */
    public boolean isUserSeniorDeveloper(String id) {
        return Config.SENIOR_DEVELOPERS.contains(id);
    }

}

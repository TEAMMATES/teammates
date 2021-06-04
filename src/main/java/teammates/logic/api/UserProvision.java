package teammates.logic.api;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.StudentsLogic;

/**
 * Handles logic related to username and user role provisioning.
 */
public class UserProvision {

    private static final AccountsLogic accountsLogic = AccountsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    /**
     * Gets the information of the current logged in user.
     */
    public UserInfo getCurrentUser(UserInfoCookie uic) {
        UserInfo user = getCurrentLoggedInUser(uic);

        if (user == null) {
            return null;
        }

        String userId = user.id;
        user.isInstructor = accountsLogic.isAccountAnInstructor(userId);
        user.isStudent = studentsLogic.isStudentInAnyCourse(userId);
        return user;
    }

    protected UserInfo getCurrentLoggedInUser(UserInfoCookie uic) {
        if (uic == null || !uic.isValid()) {
            return null;
        }

        UserInfo userInfo = new UserInfo(uic.getUserId());
        userInfo.isAdmin = uic.isAdmin();
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

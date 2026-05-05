package teammates.logic.api;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.logic.core.UsersLogic;

/**
 * Handles logic related to username and user role provisioning.
 */
public class UserProvision {

    private static final UserProvision instance = new UserProvision();

    private final UsersLogic usersLogic = UsersLogic.inst();

    UserProvision() {
        // prevent initialization
    }

    public static UserProvision inst() {
        return instance;
    }

    /**
     * Gets the information of the current logged in user.
     */
    public UserInfo getCurrentUser(UserInfoCookie uic) {
        UserInfo user = getCurrentLoggedInUser(uic);

        if (user == null) {
            return null;
        }

        String userId = user.id;
        user.isAdmin = Config.getAppAdmins().contains(userId);
        user.isInstructor = usersLogic.isInstructorInAnyCourse(userId);
        user.isStudent = usersLogic.isStudentInAnyCourse(userId);
        user.isMaintainer = Config.getAppMaintainers().contains(userId);
        return user;
    }

    /**
     * Gets the current logged in user.
     */
    UserInfo getCurrentLoggedInUser(UserInfoCookie uic) {
        if (uic == null || !uic.isValid()) {
            return null;
        }

        return new UserInfo(uic.getUserId());
    }

    /**
     * Gets the information of the current masqueraded user.
     */
    public UserInfo getMasqueradeUser(String googleId) {
        UserInfo userInfo = new UserInfo(googleId);
        userInfo.isAdmin = false;
        userInfo.isInstructor = usersLogic.isInstructorInAnyCourse(googleId);
        userInfo.isStudent = usersLogic.isStudentInAnyCourse(googleId);
        userInfo.isMaintainer = Config.getAppMaintainers().contains(googleId);
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
     * User principal for verified cron/worker requests: not a human app admin; {@link UserInfo#isAutomatedService} only.
     */
    public UserInfo getAutomatedServiceUser(String serviceId) {
        UserInfo userInfo = new UserInfo(serviceId);
        userInfo.isAutomatedService = true;
        return userInfo;
    }

}

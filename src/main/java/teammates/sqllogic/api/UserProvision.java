package teammates.sqllogic.api;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.sqllogic.core.UsersLogic;

import java.util.UUID;

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

        UUID userId = user.accountId;
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

        return new UserInfo(uic.getAccountId());
    }

    /**
     * Gets the information of the current masqueraded user.
     */
    public UserInfo getMasqueradeUser(UUID accountId) {
        UserInfo userInfo = new UserInfo(accountId);
        userInfo.isAdmin = false;
        userInfo.isInstructor = usersLogic.isInstructorInAnyCourse(accountId);
        userInfo.isStudent = usersLogic.isStudentInAnyCourse(accountId);
        userInfo.isMaintainer = Config.getAppMaintainers().contains(accountId);
        return userInfo;
    }

    /**
     * Gets the information of a user who has administrator role only.
     */
    public UserInfo getAdminOnlyUser(UUID userId) {
        UserInfo userInfo = new UserInfo(userId);
        userInfo.isAdmin = true;
        return userInfo;
    }

}

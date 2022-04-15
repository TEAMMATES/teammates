package teammates.logic.api;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;

/**
 * Handles logic related to username and user role provisioning.
 */
public class UserProvision {

    private static final UserProvision instance = new UserProvision();

    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();

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
        user.isAdmin = Config.APP_ADMINS.contains(userId);
        user.isInstructor = instructorsLogic.isInstructorInAnyCourse(userId);
        user.isStudent = studentsLogic.isStudentInAnyCourse(userId);
        user.isMaintainer = Config.APP_MAINTAINERS.contains(user.getId());
        return user;
    }

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
        userInfo.isInstructor = instructorsLogic.isInstructorInAnyCourse(googleId);
        userInfo.isStudent = studentsLogic.isStudentInAnyCourse(googleId);
        userInfo.isMaintainer = Config.APP_MAINTAINERS.contains(googleId);
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

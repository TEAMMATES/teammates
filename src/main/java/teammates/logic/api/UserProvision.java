package teammates.logic.api;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.HibernateUtil;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.sqllogic.core.UsersLogic;

/**
 * Handles logic related to username and user role provisioning.
 */
public class UserProvision {

    private static final UserProvision instance = new UserProvision();

    private final UsersLogic usersLogic = UsersLogic.inst();
    private final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();

    @SuppressWarnings("PMD.UnnecessaryConstructor")
    public UserProvision() {
        // TODO: change constructor to private & remove PMD suppression after migration
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
        user.isInstructor = usersLogic.isInstructorInAnyCourse(userId)
                || instructorsLogic.isInstructorInAnyCourse(userId);
        user.isStudent = usersLogic.isStudentInAnyCourse(userId)
                || studentsLogic.isStudentInAnyCourse(userId);
        user.isMaintainer = Config.APP_MAINTAINERS.contains(user.getId());
        return user;
    }

    /**
     * Gets the information of the current logged in user, with an SQL transaction.
     */
    public UserInfo getCurrentUserWithTransaction(UserInfoCookie uic) {
        HibernateUtil.beginTransaction();
        UserInfo userInfo = getCurrentUser(uic);
        HibernateUtil.commitTransaction();
        return userInfo;
    }

    // TODO: method visibility to package-private after migration
    /**
     * Gets the current logged in user.
     */
    public UserInfo getCurrentLoggedInUser(UserInfoCookie uic) {
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
        userInfo.isInstructor = usersLogic.isInstructorInAnyCourse(googleId)
                || instructorsLogic.isInstructorInAnyCourse(googleId);
        userInfo.isStudent = usersLogic.isInstructorInAnyCourse(googleId)
                || studentsLogic.isStudentInAnyCourse(googleId);
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

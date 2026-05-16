package teammates.logic.api;

import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.Account;

/**
 * Handles logic related to username and user role provisioning.
 */
public class UserProvision {

    private static final UserProvision instance = new UserProvision();

    private final UsersLogic usersLogic = UsersLogic.inst();

    private final AccountsLogic accountsLogic = AccountsLogic.inst();

    UserProvision() {
        // prevent initialization
    }

    public static UserProvision inst() {
        return instance;
    }

    /**
     * Gets the information of the current logged in user.
     */
    public AuthContext getCurrentUserContext(UserInfoCookie uic) {
        AuthContext user = getCurrentLoggedInUserContext(uic);

        if (user == null) {
            return null;
        }

        String userId = user.id();
        return new AuthContext(
                userId,
                user.accountId(),
                Config.getAppAdmins().contains(userId),
                usersLogic.isInstructorInAnyCourse(userId),
                usersLogic.isStudentInAnyCourse(userId),
                Config.getAppMaintainers().contains(userId));
    }

    /**
     * Gets the current logged in user.
     */
    AuthContext getCurrentLoggedInUserContext(UserInfoCookie uic) {
        if (uic == null || !uic.isValid()) {
            return null;
        }

        return new AuthContext(uic.getUserId(), uic.getAccountId(),
                false, false, false, false);
    }

    /**
     * Gets the information of the current masqueraded user.
     */
    public AuthContext getMasqueradeUserContext(String googleId) {
        Account account = accountsLogic.getAccountForGoogleId(googleId);
        return new AuthContext(
                googleId,
                account == null ? null : account.getId(),
                false,
                usersLogic.isInstructorInAnyCourse(googleId),
                usersLogic.isStudentInAnyCourse(googleId),
                Config.getAppMaintainers().contains(googleId));
    }

    /**
     * Gets the information of a user who has administrator role only.
     */
    public AuthContext getAdminOnlyUserContext(String userId) {
        // Only used for testing. To be removed in the future.
        Account account = userId == null ? null : accountsLogic.getAccountForGoogleId(userId);
        return new AuthContext(userId, account == null ? null : account.getId(), true, true, true, true);
    }

    /**
     * Gets the information of a user from an AuthContext.
     */
    public UserInfo getUserInfo(AuthContext authContext) {
        if (authContext == null) {
            return null;
        }

        UserInfo userInfo = new UserInfo(authContext.id(), authContext.accountId());
        userInfo.isAdmin = authContext.isAdmin();
        userInfo.isInstructor = authContext.isInstructor();
        userInfo.isStudent = authContext.isStudent();
        userInfo.isMaintainer = authContext.isMaintainer();
        return userInfo;
    }
}

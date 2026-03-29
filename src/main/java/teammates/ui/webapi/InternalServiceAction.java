package teammates.ui.webapi;

/**
 * Actions callable by verified internal jobs (cron/worker) and by human application administrators.
 * <p>Application administrators using {@code /webapi} routes are included: authorization is
 * {@link teammates.common.datatransfer.UserInfo#isAdmin} or {@link teammates.common.datatransfer.UserInfo#isInternalService}.
 */
abstract class InternalServiceAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.canAccessAsAdminOrInternalService()) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

}

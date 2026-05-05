package teammates.ui.webapi;

import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Actions callable by verified automated jobs (cron/worker) and by human application administrators.
 *
 * <p>Application administrators using {@code /webapi} routes are included: authorization is
 * {@link teammates.common.datatransfer.UserInfo#isAdmin} or
 * {@link teammates.common.datatransfer.UserInfo#isAutomatedService}.
 */
abstract class AutomatedServiceAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!canAccessAsAdminOrAutomatedService()) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    boolean canAccessAsAdminOrAutomatedService() {
        return userInfo.isAdmin || userInfo.isAutomatedService;
    }

}

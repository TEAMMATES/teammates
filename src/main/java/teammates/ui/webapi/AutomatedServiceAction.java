package teammates.ui.webapi;

import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Actions callable by verified automated jobs (cron/worker) and by human application administrators.
 *
 * <p>Application administrators using {@code /webapi} routes are included: authorization is
 * {@link teammates.common.datatransfer.UserInfo#isAdmin} or
 * {@link teammates.ui.webapi.AuthType#AUTOMATED_SERVICE}.
 */
abstract class AutomatedServiceAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.AUTOMATED_SERVICE;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!canAccessAsAdminOrAutomatedService()) {
            throw new UnauthorizedAccessException(
                    "Admin or automated service privilege is required to access this resource."
            );
        }
    }

    boolean canAccessAsAdminOrAutomatedService() {
        return authType == AuthType.AUTOMATED_SERVICE
                || userInfo != null && userInfo.isAdmin;
    }

}

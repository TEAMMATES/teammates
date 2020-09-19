package teammates.ui.webapi;

import teammates.common.exception.UnauthorizedAccessException;

/**
 * An action that is permitted only for administrators.
 */
public abstract class AdminOnlyAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

}

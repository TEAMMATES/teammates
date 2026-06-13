package teammates.ui.webapi;

import teammates.ui.exception.UnauthorizedAccessException;

/**
 * An action that is permitted only for administrators.
 */
abstract class AdminOnlyAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!requestContext.isAdmin()) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

}

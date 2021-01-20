package teammates.ui.webapi;

import teammates.common.exception.UnauthorizedAccessException;

public class GetResponseStatsAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    JsonResult execute() {
        return new JsonResult("HELLO");
    }

    @Override
    void checkSpecificAccessControl() {
        //Only allows admins to call this api
        if (userInfo.isAdmin) {
            return;
        }
        throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
    }
}

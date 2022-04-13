package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.ui.output.ReadNotificationsData;

/**
 * Action: Gets read notifications in account entity.
 */
public class GetReadNotificationsAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Any user can create a read status for notification.
    }

    @Override
    public ActionResult execute() {
        AccountAttributes accountAttributes =
                logic.getAccount(userInfo.getId());
        ReadNotificationsData output = new ReadNotificationsData(accountAttributes);
        return new JsonResult(output);
    }
}

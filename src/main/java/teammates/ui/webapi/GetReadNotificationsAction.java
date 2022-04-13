package teammates.ui.webapi;

import java.util.List;

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
        List<String> readNotifications =
                logic.getReadNotificationsId(userInfo.getId());
        ReadNotificationsData output = new ReadNotificationsData(readNotifications);
        return new JsonResult(output);
    }
}

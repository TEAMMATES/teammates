package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;

/**
 * Action: Deletes a notification by its ID.
 */
public class DeleteNotificationAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        UUID notificationId = getUuidRequestParamValue(Const.ParamsNames.NOTIFICATION_ID);
        sqlLogic.deleteNotification(notificationId);
        return new JsonResult("Notification has been deleted.");
    }
}

package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.NotificationData;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Action: Gets a notification by ID.
 */
public class GetNotificationAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        UUID notificationId = getUuidRequestParamValue(Const.ParamsNames.NOTIFICATION_ID);

        Notification notification = sqlLogic.getNotification(notificationId);

        if (notification == null) {
            throw new EntityNotFoundException("Notification does not exist.");
        }

        return new JsonResult(new NotificationData(notification));
    }
}

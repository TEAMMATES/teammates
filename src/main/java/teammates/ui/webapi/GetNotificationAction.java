package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.ui.output.NotificationData;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Action: Gets a notification by ID.
 */
public class GetNotificationAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String notificationId = getNonNullRequestParamValue(Const.ParamsNames.NOTIFICATION_ID);

        NotificationAttributes notification = logic.getNotification(notificationId);

        if (notification == null) {
            throw new EntityNotFoundException("Notification does not exist.");
        }

        return new JsonResult(new NotificationData(notification));
    }
}

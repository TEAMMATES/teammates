package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.ui.output.NotificationData;
import teammates.ui.output.NotificationsData;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Action: Get a list of notifications.
 */
public class GetNotificationAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Any user can get notifications as long as its parameters are valid
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String targetUser = getRequestParamValue(Const.ParamsNames.NOTIFICATION_TARGET_USER);
        // TODO: Use isFetchingAll to decide whether to fetch unread notification only.
        // boolean isFetchingAll = Boolean.parseBoolean(
        //     getRequestParamValue(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL));

        List<NotificationAttributes> notificationAttributes;
        if (targetUser == null && userInfo.isAdmin) {
            // if the admin wants to retrieve all notifications
            notificationAttributes =
                    logic.getAllNotifications();
        } else {
            String targetUserErrorMessage = FieldValidator.getInvalidityInfoForNotificationTargetUser(targetUser);
            if (!targetUserErrorMessage.isEmpty()) {
                throw new InvalidHttpRequestBodyException(targetUserErrorMessage);
            }
            notificationAttributes =
                    logic.getNotificationsByTargetUser(NotificationTargetUser.valueOf(targetUser));
        }

        NotificationsData responseData = new NotificationsData(notificationAttributes);

        if (!userInfo.isAdmin) {
            responseData.getNotifications().forEach(NotificationData::hideInformationForNonAdmin);
        }
        return new JsonResult(responseData);
    }
}

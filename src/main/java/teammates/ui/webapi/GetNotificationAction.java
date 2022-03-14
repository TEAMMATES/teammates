package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.ui.output.NotificationData;
import teammates.ui.output.NotificationsData;

/**
 * Action: Get a list of notifications.
 */
public class GetNotificationAction extends Action {

    private static final String INVALID_TARGET_USER = "Target user can only be STUDENT or INSTRUCTOR.";
    private static final String UNAUTHORIZED_ACCESS = "You are not allowed to view this resource!";

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (userInfo.isAdmin) {
            return;
        }
        String targetUserString = getRequestParamValue(Const.ParamsNames.NOTIFICATION_TARGET_USER);
        String targetUserErrorMessage = FieldValidator.getInvalidityInfoForNotificationTargetUser(targetUserString);
        if (!targetUserErrorMessage.isEmpty()) {
            throw new InvalidHttpParameterException(targetUserErrorMessage);
        }
        NotificationTargetUser targetUser = NotificationTargetUser.valueOf(targetUserString);
        if (targetUser == NotificationTargetUser.INSTRUCTOR && userInfo.isStudent
                || targetUser == NotificationTargetUser.STUDENT && userInfo.isInstructor) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
        }
    }

    @Override
    public JsonResult execute() {
        String targetUserString = getRequestParamValue(Const.ParamsNames.NOTIFICATION_TARGET_USER);
        // TODO: Use isFetchingAll to decide whether to fetch unread notification only.
        // boolean isFetchingAll = Boolean.parseBoolean(
        //     getRequestParamValue(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL));

        List<NotificationAttributes> notificationAttributes;
        if (targetUserString == null && userInfo.isAdmin) {
            // if the admin wants to retrieve all notifications
            notificationAttributes = logic.getAllNotifications();
        } else {
            String targetUserErrorMessage = FieldValidator.getInvalidityInfoForNotificationTargetUser(targetUserString);
            if (!targetUserErrorMessage.isEmpty()) {
                throw new InvalidHttpParameterException(targetUserErrorMessage);
            }
            NotificationTargetUser targetUser = NotificationTargetUser.valueOf(targetUserString);
            if (targetUser == NotificationTargetUser.GENERAL) {
                throw new InvalidHttpParameterException(INVALID_TARGET_USER);
            }
            notificationAttributes =
                    logic.getActiveNotificationsByTargetUser(targetUser);
        }

        NotificationsData responseData = new NotificationsData(notificationAttributes);

        if (!userInfo.isAdmin) {
            responseData.getNotifications().forEach(NotificationData::hideInformationForNonAdmin);
        }
        return new JsonResult(responseData);
    }
}

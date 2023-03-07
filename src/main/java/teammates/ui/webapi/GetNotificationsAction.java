package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.NotificationsData;

/**
 * Action: Gets a list of notifications.
 */
public class GetNotificationsAction extends Action {

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
        if (targetUser == NotificationTargetUser.INSTRUCTOR && !userInfo.isInstructor
                || targetUser == NotificationTargetUser.STUDENT && !userInfo.isStudent) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
        }
    }

    @Override
    public JsonResult execute() {
        String targetUserString = getRequestParamValue(Const.ParamsNames.NOTIFICATION_TARGET_USER);
        List<Notification> notifications;

        if (targetUserString == null && userInfo.isAdmin) {
            // if request is from admin and targetUser is not specified, retrieve all notifications
            notifications = sqlLogic.getAllNotifications();
            return new JsonResult(new NotificationsData(notifications));
        } else {
            // retrieve active notification for specified target user
            String targetUserErrorMessage = FieldValidator.getInvalidityInfoForNotificationTargetUser(targetUserString);
            if (!targetUserErrorMessage.isEmpty()) {
                throw new InvalidHttpParameterException(targetUserErrorMessage);
            }
            NotificationTargetUser targetUser = NotificationTargetUser.valueOf(targetUserString);
            if (targetUser == NotificationTargetUser.GENERAL) {
                throw new InvalidHttpParameterException(INVALID_TARGET_USER);
            }
            notifications =
                    sqlLogic.getActiveNotificationsByTargetUser(targetUser);
        }

        boolean isFetchingAll = false;
        if (getRequestParamValue(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL) != null) {
            isFetchingAll = getBooleanRequestParamValue(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL);
        }

        if (isFetchingAll) {
            return new JsonResult(new NotificationsData(notifications));
        }

        // Filter unread notifications
        List<UUID> readNotifications = sqlLogic.getReadNotificationsId(userInfo.getId());
        notifications = notifications
                .stream()
                .filter(n -> !readNotifications.contains(n.getId()))
                .collect(Collectors.toList());

        if (userInfo.isAdmin) {
            return new JsonResult(new NotificationsData(notifications));
        }

        // Update shown attribute once a non-admin user fetches unread notifications
        for (Notification n : notifications) {
            if (n.isShown()) {
                continue;
            }
            n.setShown();
        }
        return new JsonResult(new NotificationsData(notifications));
    }
}

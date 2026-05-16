package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.entity.Account;
import teammates.storage.entity.Notification;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
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
        if (authContext.isAdmin()) {
            return;
        }
        String targetUserString = getRequestParamValue(Const.ParamsNames.NOTIFICATION_TARGET_USER);
        String targetUserErrorMessage = FieldValidator.getInvalidityInfoForNotificationTargetUser(targetUserString);
        if (!targetUserErrorMessage.isEmpty()) {
            throw new InvalidHttpParameterException(targetUserErrorMessage);
        }
        NotificationTargetUser targetUser = NotificationTargetUser.valueOf(targetUserString);
        if (targetUser == NotificationTargetUser.INSTRUCTOR && !authContext.isInstructor()
                || targetUser == NotificationTargetUser.STUDENT && !authContext.isStudent()) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
        }
    }

    @Override
    public JsonResult execute() {
        String targetUserString = getRequestParamValue(Const.ParamsNames.NOTIFICATION_TARGET_USER);
        List<Notification> notifications;

        if (targetUserString == null && authContext.isAdmin()) {
            // if request is from admin and targetUser is not specified, retrieve all notifications
            notifications = logic.getAllNotifications();
            return new JsonResult(new NotificationsData(notifications));
        }

        // retrieve active notification for specified target user
        String targetUserErrorMessage = FieldValidator.getInvalidityInfoForNotificationTargetUser(targetUserString);
        if (!targetUserErrorMessage.isEmpty()) {
            throw new InvalidHttpParameterException(targetUserErrorMessage);
        }
        NotificationTargetUser targetUser = NotificationTargetUser.valueOf(targetUserString);
        if (targetUser == NotificationTargetUser.GENERAL) {
            throw new InvalidHttpParameterException(INVALID_TARGET_USER);
        }
        notifications = logic.getActiveNotificationsByTargetUser(targetUser);

        boolean isFetchingAll = false;
        if (getRequestParamValue(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL) != null) {
            isFetchingAll = getBooleanRequestParamValue(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL);
        }

        if (isFetchingAll) {
            return new JsonResult(new NotificationsData(notifications));
        }

        Account account = logic.getAccountForGoogleId(authContext.id());
        if (account == null) {
            // This should not happen as the user is authenticated
            return new JsonResult("Account not found", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        notifications = logic.getUnreadActiveNotificationsByTargetUser(
                List.of(targetUser, NotificationTargetUser.GENERAL), account.getId(), Instant.now());

        if (authContext.isAdmin()) {
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

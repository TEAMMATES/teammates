package teammates.ui.webapi;

import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.entity.Notification;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.NotificationsData;

/**
 * Action: Gets a list of notifications.
 */
public class GetNotificationsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (authContext.isAdmin()) {
            return;
        }

        for (NotificationTargetUser targetUser : getTargetUsersFromRequest()) {
            if (targetUser == NotificationTargetUser.STUDENT) {
                gateKeeper.verifyStudentInAnyCourse(logic.getAccountForGoogleId(getCurrentUserGoogleId()));
            }

            if (targetUser == NotificationTargetUser.INSTRUCTOR) {
                gateKeeper.verifyInstructorInAnyCourse(logic.getAccountForGoogleId(getCurrentUserGoogleId()));
            }
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

        // retrieve active notification for specified target users
        List<NotificationTargetUser> targetUsers = getTargetUsersFromRequest();
        notifications = logic.getActiveNotificationsByTargetUsers(targetUsers);

        return new JsonResult(new NotificationsData(notifications));
    }

    private List<NotificationTargetUser> getTargetUsersFromRequest() {
        return Arrays.stream(getNonNullRequestParamValues(Const.ParamsNames.NOTIFICATION_TARGET_USER))
                .map(this::parseTargetUser)
                .toList();
    }

    private NotificationTargetUser parseTargetUser(String targetUserString) {
        if (targetUserString == null) {
            throw new InvalidHttpParameterException(
                    String.format("The [%s] HTTP parameter is null.", Const.ParamsNames.NOTIFICATION_TARGET_USER));
        }
        String targetUserErrorMessage = FieldValidator.getInvalidityInfoForNotificationTargetUser(targetUserString);
        if (!targetUserErrorMessage.isEmpty()) {
            throw new InvalidHttpParameterException(targetUserErrorMessage);
        }
        return NotificationTargetUser.valueOf(targetUserString);
    }
}

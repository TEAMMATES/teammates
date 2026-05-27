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

        if (!getBooleanRequestParamValue(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE)) {
            throw new UnauthorizedAccessException("Only admins can fetch non-active notifications.");
        }

        List<NotificationTargetUser> targetUsers = getTargetUsersFromRequest();

        for (NotificationTargetUser targetUser : targetUsers) {
            if (targetUser == NotificationTargetUser.STUDENT) {
                gateKeeper.verifyStudentInAnyCourse(getCurrentAccount());
            }

            if (targetUser == NotificationTargetUser.INSTRUCTOR) {
                gateKeeper.verifyInstructorInAnyCourse(getCurrentAccount());
            }
        }
    }

    @Override
    public JsonResult execute() {
        boolean isFetchingActive = getBooleanRequestParamValue(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ACTIVE);
        List<NotificationTargetUser> targetUsers = getTargetUsersFromRequest();

        List<Notification> notifications = logic.getNotificationsByTargetUsers(targetUsers, isFetchingActive);
        return new JsonResult(new NotificationsData(notifications));
    }

    private List<NotificationTargetUser> getTargetUsersFromRequest() {
        String[] targetUserStrings = getNonNullRequestParamValues(Const.ParamsNames.NOTIFICATION_TARGET_USER);

        return Arrays.stream(targetUserStrings)
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

package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
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
        List<NotificationAttributes> notificationAttributes;

        if (targetUserString == null && userInfo.isAdmin) {
            // if request is from admin and targetUser is not specified, retrieve all notifications
            notificationAttributes = logic.getAllNotifications();
            return new JsonResult(new NotificationsData(notificationAttributes));
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
            notificationAttributes =
                    logic.getActiveNotificationsByTargetUser(targetUser);
        }

        boolean isFetchingAll = false;
        if (getRequestParamValue(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL) != null) {
            isFetchingAll = getBooleanRequestParamValue(Const.ParamsNames.NOTIFICATION_IS_FETCHING_ALL);
        }

        if (isFetchingAll) {
            return new JsonResult(new NotificationsData(notificationAttributes));
        }

        // Filter unread notifications
        List<String> readNotifications = logic.getReadNotificationsId(userInfo.getId());
        notificationAttributes = notificationAttributes
                .stream()
                .filter(n -> !readNotifications.contains(n.getNotificationId()))
                .collect(Collectors.toList());

        if (userInfo.isAdmin) {
            return new JsonResult(new NotificationsData(notificationAttributes));
        }

        // Update shown attribute once a non-admin user fetches unread notifications
        for (NotificationAttributes n : notificationAttributes) {
            if (n.isShown()) {
                continue;
            }
            try {
                NotificationAttributes.UpdateOptions newNotification =
                        NotificationAttributes.updateOptionsBuilder(n.getNotificationId())
                                .withShown()
                                .build();
                logic.updateNotification(newNotification);
            } catch (InvalidParametersException e) {
                throw new InvalidHttpParameterException(e);
            } catch (EntityDoesNotExistException ednee) {
                throw new EntityNotFoundException(ednee);
            }
        }
        return new JsonResult(new NotificationsData(notificationAttributes));
    }
}

package teammates.ui.webapi;

import java.time.Instant;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.NotificationData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.NotificationUpdateRequest;

/**
 * Action: Updates a new notification banner.
 */
public class UpdateNotificationAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String notificationId = getNonNullRequestParamValue(Const.ParamsNames.NOTIFICATION_ID);
        NotificationUpdateRequest notificationRequest = getAndValidateRequestBody(NotificationUpdateRequest.class);

        Instant startTime = Instant.ofEpochMilli(notificationRequest.getStartTimestamp());
        Instant endTime = Instant.ofEpochMilli(notificationRequest.getEndTimestamp());

        Notification newNotification = new Notification(startTime, endTime, notificationRequest.getStyle(),
                notificationRequest.getTargetUser(), notificationRequest.getTitle(), notificationRequest.getMessage());
        newNotification.setNotificationId(UUID.fromString(notificationId));

        try {
            return new JsonResult(new NotificationData(sqlLogic.updateNotification(newNotification)));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }
    }
}

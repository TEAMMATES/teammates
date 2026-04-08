package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnexpectedServerException;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.NotificationData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.NotificationCreateRequest;

/**
 * Action: Creates a new notification banner.
 */
public class CreateNotificationAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        NotificationCreateRequest notificationRequest = getAndValidateRequestBody(NotificationCreateRequest.class);

        Instant startTime = Instant.ofEpochMilli(notificationRequest.getStartTimestamp());
        Instant endTime = Instant.ofEpochMilli(notificationRequest.getEndTimestamp());

        Notification newNotification = new Notification(startTime, endTime, notificationRequest.getStyle(),
                notificationRequest.getTargetUser(), notificationRequest.getTitle(), notificationRequest.getMessage());

        try {
            return new JsonResult(new NotificationData(sqlLogic.createNotification(newNotification)));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityAlreadyExistsException e) {
            // Should not happen since UUID is usually unique
            throw new UnexpectedServerException(e.getMessage(), e);
        }
    }
}

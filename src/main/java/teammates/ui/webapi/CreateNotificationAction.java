package teammates.ui.webapi;

import java.time.Instant;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.NotificationData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.NotificationCreateRequest;

/**
 * Action: Creates a new notification banner.
 */
public class CreateNotificationAction extends AdminOnlyAction {
    private static final Logger log = Logger.getLogger();

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
            log.severe("Unexpected error", e);
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

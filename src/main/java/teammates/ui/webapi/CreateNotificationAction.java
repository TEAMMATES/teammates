package teammates.ui.webapi;

import java.time.Instant;
import java.util.UUID;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;
import teammates.ui.request.CreateNotificationRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Action: Creates a new notification banner.
 */
public class CreateNotificationAction extends AdminOnlyAction {
    private static final Logger log = Logger.getLogger();

    @Override
    public ActionResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        CreateNotificationRequest notificationRequest = getAndValidateRequestBody(CreateNotificationRequest.class);

        Instant startTime = Instant.ofEpochMilli(notificationRequest.getStartTimestamp());
        Instant endTime = Instant.ofEpochMilli(notificationRequest.getEndTimestamp());

        NotificationAttributes newNotification = NotificationAttributes.builder(UUID.randomUUID().toString())
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withType(notificationRequest.getNotificationType())
                .withTargetUser(notificationRequest.getTargetUser())
                .withTitle(notificationRequest.getTitle())
                .withMessage(notificationRequest.getMessage())
                .build();

        try {
            logic.createNotification(newNotification);
            return new JsonResult("Notification has been created successfully", HttpStatus.SC_OK);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityAlreadyExistsException e) {
            // tell user entity already exists
            // when will this happen - if UUID colldies?
            // todo?  InvalidOperationException
            log.severe("Unexpected error", e);
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes.UpdateOptions;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
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

        UpdateOptions newNotification = NotificationAttributes.updateOptionsBuilder(notificationId)
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withStyle(notificationRequest.getStyle())
                .withTargetUser(notificationRequest.getTargetUser())
                .withTitle(notificationRequest.getTitle())
                .withMessage(notificationRequest.getMessage())
                .build();

        try {
            return new JsonResult(new NotificationData(logic.updateNotification(newNotification)));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }
    }
}

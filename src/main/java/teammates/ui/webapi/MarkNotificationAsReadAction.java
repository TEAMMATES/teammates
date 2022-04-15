package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.ui.output.ReadNotificationsData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.MarkNotificationAsReadRequest;

/**
 * Action: Marks a notification as read in account entity.
 */
public class MarkNotificationAsReadAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Any user can create a read status for notification.
    }

    @Override
    public ActionResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        MarkNotificationAsReadRequest readNotificationCreateRequest =
                getAndValidateRequestBody(MarkNotificationAsReadRequest.class);
        String notificationId = readNotificationCreateRequest.getNotificationId();
        Instant endTime = Instant.ofEpochMilli(readNotificationCreateRequest.getEndTimestamp());

        try {
            List<String> readNotifications =
                    logic.updateReadNotifications(userInfo.getId(), notificationId, endTime);
            ReadNotificationsData output = new ReadNotificationsData(readNotifications);
            return new JsonResult(output);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }
    }
}

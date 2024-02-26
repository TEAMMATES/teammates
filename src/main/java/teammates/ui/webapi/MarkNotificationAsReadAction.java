package teammates.ui.webapi;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        UUID notificationId = UUID.fromString(readNotificationCreateRequest.getNotificationId());
        Instant endTime = Instant.ofEpochMilli(readNotificationCreateRequest.getEndTimestamp());

        try {
            List<UUID> readNotifications =
                    sqlLogic.updateReadNotifications(userInfo.getId(), notificationId, endTime);
            ReadNotificationsData output = new ReadNotificationsData(
                    readNotifications.stream().map(UUID::toString).collect(Collectors.toList()));
            return new JsonResult(output);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }
    }
}

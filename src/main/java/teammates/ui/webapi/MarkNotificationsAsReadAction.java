package teammates.ui.webapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.ui.output.ReadNotificationsData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.MarkNotificationsAsReadRequest;

/**
 * Action: Marks multiple notifications as read in account entity.
 */
public class MarkNotificationsAsReadAction extends Action {

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
        MarkNotificationsAsReadRequest request = getAndValidateRequestBody(MarkNotificationsAsReadRequest.class);

        try {
            List<UUID> readNotifications = new ArrayList<>();

            for (MarkNotificationsAsReadRequest.NotificationReadStatus notificationStatus : request.getNotifications()) {
                UUID notificationId = UUID.fromString(notificationStatus.getNotificationId());
                Instant endTime = Instant.ofEpochMilli(notificationStatus.getEndTimestamp());

                readNotifications.addAll(sqlLogic.updateReadNotifications(userInfo.getId(), notificationId, endTime));
            }

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

package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.storage.entity.Account;
import teammates.storage.entity.ReadNotification;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.exception.UnexpectedServerException;
import teammates.ui.output.ReadNotificationData;
import teammates.ui.output.ReadNotificationsData;
import teammates.ui.request.MarkNotificationAsReadRequest;

/**
 * Action: Marks a notification as read in account entity.
 */
public class MarkNotificationAsReadAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Any user can create a read status for notification.
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        MarkNotificationAsReadRequest readNotificationCreateRequest =
                getAndValidateRequestBody(MarkNotificationAsReadRequest.class);

        Account account = logic.getAccountForGoogleId(getCurrentUserGoogleId());
        if (account == null) {
            // This should not happen as the user is authenticated
            throw new UnexpectedServerException("Account not found");
        }

        if (readNotificationCreateRequest.getNotificationIds() != null) {
            List<ReadNotification> readNotifications = new ArrayList<>();
            try {
                for (String idStr : readNotificationCreateRequest.getNotificationIds()) {
                    UUID notificationId = UUID.fromString(idStr);
                    readNotifications.add(logic.createReadNotification(account.getId(), notificationId));
                }
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
            List<UUID> readNotificationIds = readNotifications.stream()
                    .map(rn -> rn.getNotification().getId())
                    .toList();
            return new JsonResult(new ReadNotificationsData(readNotificationIds));
        } else {
            UUID notificationId = UUID.fromString(readNotificationCreateRequest.getNotificationId());
            ReadNotification readNotification;
            try {
                readNotification = logic.createReadNotification(account.getId(), notificationId);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
            return new JsonResult(new ReadNotificationData(readNotification));
        }
    }
}

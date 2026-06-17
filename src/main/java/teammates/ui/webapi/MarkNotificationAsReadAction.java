package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.storage.entity.Account;
import teammates.storage.entity.ReadNotification;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.ReadNotificationData;
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
        UUID notificationId = UUID.fromString(readNotificationCreateRequest.getNotificationId());
        Account account = getCurrentAccount();

        try {
            ReadNotification readNotification = logic.createReadNotification(account.getId(), notificationId);
            return new JsonResult(new ReadNotificationData(readNotification));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }
}

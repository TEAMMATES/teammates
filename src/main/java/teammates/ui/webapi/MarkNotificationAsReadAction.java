package teammates.ui.webapi;

import java.util.UUID;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.storage.entity.Account;
import teammates.storage.entity.ReadNotification;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.ReadNotificationData;
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

        Account account = logic.getAccountForGoogleId(authContext.id());
        if (account == null) {
            // This should not happen as the user is authenticated
            return new JsonResult("Account not found", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        ReadNotification readNotification;
        try {
            readNotification = logic.createReadNotification(account.getId(), notificationId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
        ReadNotificationData output = new ReadNotificationData(readNotification);

        return new JsonResult(output);
    }
}

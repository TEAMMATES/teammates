package teammates.ui.webapi;

import java.time.Instant;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;
import teammates.ui.output.AccountData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.ReadNotificationCreateRequest;

/**
 * Action: Creates a read notification in account entity.
 */
public class CreateReadNotificationAction extends Action {
    private static final Logger log = Logger.getLogger();

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
        ReadNotificationCreateRequest readNotificationCreateRequest =
                getAndValidateRequestBody(ReadNotificationCreateRequest.class);
        String notificationId = readNotificationCreateRequest.getNotificationId();
        Instant endTime = Instant.ofEpochMilli(readNotificationCreateRequest.getEndTimestamp());

        try {
            if (!logic.doesNotificationExists(notificationId)) {
                throw new InvalidParametersException("Invalid notification id.");
            }
            AccountAttributes accountAttributes =
                    logic.updateReadNotifications(userInfo.getId(), notificationId, endTime);
            AccountData output = new AccountData(accountAttributes);
            return new JsonResult(output);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException e) {
            // Should not be thrown as existence of account request has been validated before.
            log.severe("Unexpected error", e);
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

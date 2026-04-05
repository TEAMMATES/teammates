package teammates.ui.webapi;

import java.util.UUID;

import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.ReadNotification;
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

        Account account = sqlLogic.getAccountForGoogleId(userInfo.getId());
        ReadNotification readNotification = sqlLogic.createReadNotification(account.getId(), notificationId);
        ReadNotificationData output = new ReadNotificationData(readNotification);

        return new JsonResult(output);
    }
}

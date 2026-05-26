package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.storage.entity.Account;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.exception.UnexpectedServerException;
import teammates.ui.output.ReadNotificationsData;

/**
 * Action: Gets read notifications from account entity.
 */
public class GetReadNotificationsAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Any user can get the read notifications for their account.
    }

    @Override
    public ActionResult execute() {
        Account account = logic.getAccountForGoogleId(getCurrentUserGoogleId());
        if (account == null) {
            // This should not happen as the user is authenticated
            throw new UnexpectedServerException("Account not found");
        }
        List<UUID> readNotifications =
                logic.getReadNotificationsByAccountId(account.getId()).stream()
                        .map(n -> n.getNotification().getId())
                        .toList();
        ReadNotificationsData output = new ReadNotificationsData(readNotifications);
        return new JsonResult(output);
    }
}

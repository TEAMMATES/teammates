package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import org.apache.http.HttpStatus;

import teammates.logic.entity.Account;
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
        Account account = logic.getAccountForGoogleId(userInfo.getId());
        if (account == null) {
            // This should not happen as the user is authenticated
            return new JsonResult("Account not found", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        List<UUID> readNotifications =
                logic.getReadNotificationsByAccountId(account.getId()).stream()
                        .map(n -> n.getNotification().getId())
                        .toList();
        ReadNotificationsData output = new ReadNotificationsData(readNotifications);
        return new JsonResult(output);
    }
}

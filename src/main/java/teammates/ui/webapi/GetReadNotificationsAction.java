package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        List<UUID> readNotifications =
                sqlLogic.getReadNotificationsId(userInfo.getId());
        ReadNotificationsData output = new ReadNotificationsData(
                readNotifications.stream().map(UUID::toString).collect(Collectors.toList()));
        return new JsonResult(output);
    }
}

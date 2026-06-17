package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.NotificationData;

/**
 * Tests for {@link GetNotificationAction}.
 */
public class GetNotificationActionTest extends BaseActionTest<GetNotificationAction, NotificationData> {

    @Test(groups = GroupNames.ACTION)
    public void getNotificationAction_existingNotification_returnsNotificationData() {
        var notification = given.notification("notification");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_ID, notification.id().toString())
                .withAdminAuth();

        NotificationData result = execute(request);

        assertEquals(notification.id(), result.getNotificationId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getNotificationAction_notificationDoesNotExist_throwsEntityNotFoundException() {
        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_ID, given.uuid("nonexistent").toString())
                .withAdminAuth();

        assertActionThrows(EntityNotFoundException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getNotificationAction_nonAdminUser_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var notification = given.notification("notification");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_ID, notification.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}

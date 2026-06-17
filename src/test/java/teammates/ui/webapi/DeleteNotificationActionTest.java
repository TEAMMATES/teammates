package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link DeleteNotificationAction}.
 */
public class DeleteNotificationActionTest extends BaseActionTest<DeleteNotificationAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void deleteNotificationAction_existingNotification_deletesNotification() {
        var adminAccount = given.account("admin", a -> a.admin());
        var notification = given.notification("notification");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_ID, notification.id().toString())
                .withAdminAuth(adminAccount.id());

        execute(request);

        assertNull(inTransaction(() -> Logic.inst().getNotification(notification.id())));
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteNotificationAction_nonAdminUser_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var notification = given.notification("notification");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_ID, notification.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}

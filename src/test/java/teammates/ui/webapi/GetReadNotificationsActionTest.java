package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.test.GroupNames;
import teammates.ui.output.ReadNotificationsData;

/**
 * Tests for {@link GetReadNotificationsAction}.
 */
public class GetReadNotificationsActionTest extends BaseActionTest<GetReadNotificationsAction, ReadNotificationsData> {

    @Test(groups = GroupNames.ACTION)
    public void getReadNotificationsAction_accountWithReadNotifications_returnsReadNotificationIds() {
        var account = given.account("account");
        var notification = given.notification("notification");
        given.readNotification("readNotification", rn -> rn.account(account.alias()).notification(notification.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withCookie(getAuthCookie(account.id()));

        ReadNotificationsData result = execute(request);

        List<UUID> readNotifications = result.getReadNotifications();
        assertEquals(1, readNotifications.size());
        assertEquals(notification.id(), readNotifications.get(0));
    }

    @Test(groups = GroupNames.ACTION)
    public void getReadNotificationsAction_accountWithNoReadNotifications_returnsEmptyList() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withCookie(getAuthCookie(account.id()));

        ReadNotificationsData result = execute(request);

        assertTrue(result.getReadNotifications().isEmpty());
    }
}

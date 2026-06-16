package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.ReadNotificationData;
import teammates.ui.request.MarkNotificationAsReadRequest;

/**
 * Tests for {@link MarkNotificationAsReadAction}.
 */
public class MarkNotificationAsReadActionTest extends BaseActionTest<MarkNotificationAsReadAction, ReadNotificationData> {

    @Test(groups = GroupNames.ACTION)
    public void markNotificationAsReadAction_validRequest_createsReadNotification() {
        var account = given.account("account");
        var notification = given.notification("notification");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withCookie(getAuthCookie(account.id()))
                .withRequest(new MarkNotificationAsReadRequest(notification.id().toString()));

        ReadNotificationData result = execute(request);

        assertEquals(account.id(), result.getAccountId());
        assertEquals(notification.id(), result.getNotificationId());
    }

    @Test(groups = GroupNames.ACTION)
    public void markNotificationAsReadAction_notificationDoesNotExist_throwsEntityNotFoundException() {
        var account = given.account("account");
        persistGivenData(given);

        String nonExistentNotificationId = given.uuid("nonexistent").toString();
        RequestContext request = new RequestContext()
                .withCookie(getAuthCookie(account.id()))
                .withRequest(new MarkNotificationAsReadRequest(nonExistentNotificationId));

        assertActionThrows(EntityNotFoundException.class, request);
    }
}

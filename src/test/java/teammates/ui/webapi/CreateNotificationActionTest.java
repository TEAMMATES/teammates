package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.NotificationData;
import teammates.ui.request.NotificationCreateRequest;

/**
 * Tests for {@link CreateNotificationAction}.
 */
public class CreateNotificationActionTest extends BaseActionTest<CreateNotificationAction, NotificationData> {

    @Test(groups = GroupNames.ACTION)
    public void createNotificationAction_validRequest_createsNotification() {
        var adminAccount = given.account("admin", a -> a.admin());
        persistGivenData(given);

        NotificationCreateRequest createRequest = new NotificationCreateRequest();
        Instant now = Instant.now();
        createRequest.setStartTimestamp(now.minus(1, ChronoUnit.HOURS).toEpochMilli());
        createRequest.setEndTimestamp(now.plus(1, ChronoUnit.HOURS).toEpochMilli());
        createRequest.setStyle(NotificationStyle.INFO);
        createRequest.setTargetUser(NotificationTargetUser.GENERAL);
        createRequest.setTitle("Test Notification");
        createRequest.setMessage("<p>Test message</p>");

        RequestContext request = new RequestContext()
                .withCookie(getAuthCookie(adminAccount.id()))
                .withRequest(createRequest);

        NotificationData result = execute(request);

        assertNotNull(result.getNotificationId());
        assertEquals("Test Notification", result.getTitle());
        assertEquals(NotificationStyle.INFO, result.getStyle());
        assertEquals(NotificationTargetUser.GENERAL, result.getTargetUser());
    }

    @Test(groups = GroupNames.ACTION)
    public void createNotificationAction_nonAdminUser_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        persistGivenData(given);

        NotificationCreateRequest createRequest = new NotificationCreateRequest();
        Instant now = Instant.now();
        createRequest.setStartTimestamp(now.minus(1, ChronoUnit.HOURS).toEpochMilli());
        createRequest.setEndTimestamp(now.plus(1, ChronoUnit.HOURS).toEpochMilli());
        createRequest.setStyle(NotificationStyle.INFO);
        createRequest.setTargetUser(NotificationTargetUser.GENERAL);
        createRequest.setTitle("Test Notification");
        createRequest.setMessage("<p>Test message</p>");

        RequestContext request = new RequestContext()
                .withCookie(getAuthCookie(account.id()))
                .withRequest(createRequest);

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}

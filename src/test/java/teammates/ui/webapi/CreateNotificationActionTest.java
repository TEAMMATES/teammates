package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpRequestBodyException;
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

        RequestContext request = new RequestContext()
                .withAdminAuth(adminAccount.id())
                .withRequest(buildDefaultCreateRequest());

        NotificationData result = execute(request);

        assertNotNull(result.getNotificationId());
        assertEquals("Test Notification", result.getTitle());
        assertEquals(NotificationStyle.INFO, result.getStyle());
        assertEquals(NotificationTargetUser.GENERAL, result.getTargetUser());
    }

    @Test(groups = GroupNames.ACTION)
    public void createNotificationAction_endTimeBeforeStartTime_throwsInvalidHttpRequestBodyException() {
        var adminAccount = given.account("admin", a -> a.admin());
        persistGivenData(given);

        NotificationCreateRequest createRequest = buildDefaultCreateRequest();
        // Swap: end is before start
        Instant now = Instant.now();
        createRequest.setStartTimestamp(now.plus(2, ChronoUnit.HOURS).toEpochMilli());
        createRequest.setEndTimestamp(now.plus(1, ChronoUnit.HOURS).toEpochMilli());

        RequestContext request = new RequestContext()
                .withAdminAuth(adminAccount.id())
                .withRequest(createRequest);

        assertActionThrows(InvalidHttpRequestBodyException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void createNotificationAction_nonAdminUser_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withRequest(buildDefaultCreateRequest());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    private NotificationCreateRequest buildDefaultCreateRequest() {
        NotificationCreateRequest createRequest = new NotificationCreateRequest();
        Instant now = Instant.now();
        createRequest.setStartTimestamp(now.minus(1, ChronoUnit.HOURS).toEpochMilli());
        createRequest.setEndTimestamp(now.plus(1, ChronoUnit.HOURS).toEpochMilli());
        createRequest.setStyle(NotificationStyle.INFO);
        createRequest.setTargetUser(NotificationTargetUser.GENERAL);
        createRequest.setTitle("Test Notification");
        createRequest.setMessage("<p>Test message</p>");
        return createRequest;
    }
}

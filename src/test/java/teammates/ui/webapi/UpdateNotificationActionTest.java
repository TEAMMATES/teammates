package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.NotificationData;
import teammates.ui.request.NotificationUpdateRequest;

/**
 * Tests for {@link UpdateNotificationAction}.
 */
public class UpdateNotificationActionTest extends BaseActionTest<UpdateNotificationAction, NotificationData> {

    @Test(groups = GroupNames.ACTION)
    public void updateNotificationAction_existingNotification_returnsUpdatedNotificationData() {
        var adminAccount = given.account("admin", a -> a.admin());
        var notification = given.notification("notification");
        persistGivenData(given);

        NotificationUpdateRequest updateRequest = new NotificationUpdateRequest();
        Instant now = Instant.now();
        updateRequest.setStartTimestamp(now.minus(1, ChronoUnit.HOURS).toEpochMilli());
        updateRequest.setEndTimestamp(now.plus(2, ChronoUnit.HOURS).toEpochMilli());
        updateRequest.setStyle(NotificationStyle.WARNING);
        updateRequest.setTargetUser(NotificationTargetUser.INSTRUCTOR);
        updateRequest.setTitle("Updated Title");
        updateRequest.setMessage("<p>Updated message</p>");

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_ID, notification.id().toString())
                .withCookie(getAuthCookie(adminAccount.id()))
                .withRequest(updateRequest);

        NotificationData result = execute(request);

        assertEquals(notification.id(), result.getNotificationId());
        assertEquals("Updated Title", result.getTitle());
        assertEquals(NotificationStyle.WARNING, result.getStyle());
        assertEquals(NotificationTargetUser.INSTRUCTOR, result.getTargetUser());
    }

    @Test(groups = GroupNames.ACTION)
    public void updateNotificationAction_notificationDoesNotExist_throwsEntityNotFoundException() {
        var adminAccount = given.account("admin", a -> a.admin());
        persistGivenData(given);

        NotificationUpdateRequest updateRequest = new NotificationUpdateRequest();
        Instant now = Instant.now();
        updateRequest.setStartTimestamp(now.minus(1, ChronoUnit.HOURS).toEpochMilli());
        updateRequest.setEndTimestamp(now.plus(1, ChronoUnit.HOURS).toEpochMilli());
        updateRequest.setStyle(NotificationStyle.INFO);
        updateRequest.setTargetUser(NotificationTargetUser.GENERAL);
        updateRequest.setTitle("Title");
        updateRequest.setMessage("<p>Message</p>");

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_ID, given.uuid("nonexistent").toString())
                .withCookie(getAuthCookie(adminAccount.id()))
                .withRequest(updateRequest);

        assertActionThrows(EntityNotFoundException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void updateNotificationAction_nonAdminUser_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var notification = given.notification("notification");
        persistGivenData(given);

        NotificationUpdateRequest updateRequest = new NotificationUpdateRequest();
        Instant now = Instant.now();
        updateRequest.setStartTimestamp(now.minus(1, ChronoUnit.HOURS).toEpochMilli());
        updateRequest.setEndTimestamp(now.plus(1, ChronoUnit.HOURS).toEpochMilli());
        updateRequest.setStyle(NotificationStyle.INFO);
        updateRequest.setTargetUser(NotificationTargetUser.GENERAL);
        updateRequest.setTitle("Title");
        updateRequest.setMessage("<p>Message</p>");

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_ID, notification.id().toString())
                .withCookie(getAuthCookie(account.id()))
                .withRequest(updateRequest);

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}

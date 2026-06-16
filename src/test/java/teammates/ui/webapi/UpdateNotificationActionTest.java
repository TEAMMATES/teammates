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

        NotificationUpdateRequest updateRequest = buildDefaultUpdateRequest();
        updateRequest.setStyle(NotificationStyle.WARNING);
        updateRequest.setTargetUser(NotificationTargetUser.INSTRUCTOR);
        updateRequest.setTitle("Updated Title");

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

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_ID, given.uuid("nonexistent").toString())
                .withCookie(getAuthCookie(adminAccount.id()))
                .withRequest(buildDefaultUpdateRequest());

        assertActionThrows(EntityNotFoundException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void updateNotificationAction_nonAdminUser_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var notification = given.notification("notification");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.NOTIFICATION_ID, notification.id().toString())
                .withCookie(getAuthCookie(account.id()))
                .withRequest(buildDefaultUpdateRequest());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    private NotificationUpdateRequest buildDefaultUpdateRequest() {
        NotificationUpdateRequest request = new NotificationUpdateRequest();
        Instant now = Instant.now();
        request.setStartTimestamp(now.minus(1, ChronoUnit.HOURS).toEpochMilli());
        request.setEndTimestamp(now.plus(1, ChronoUnit.HOURS).toEpochMilli());
        request.setStyle(NotificationStyle.INFO);
        request.setTargetUser(NotificationTargetUser.GENERAL);
        request.setTitle("Default Title");
        request.setMessage("<p>Default message</p>");
        return request;
    }
}

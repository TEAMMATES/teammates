package teammates.ui.webapi;

import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.NotificationType;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.ui.output.NotificationData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.NotificationCreateRequest;

/**
 * SUT: {@link CreateNotificationAction}.
 */
public class CreateNotificationActionTest extends BaseActionTest<CreateNotificationAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @Override
    protected void testExecute() throws Exception {
        NotificationAttributes notif = typicalBundle.notifications.get("notification1");

        long startTime = notif.getStartTime().toEpochMilli();
        long endTime = notif.getEndTime().toEpochMilli();
        NotificationType type = notif.getType();
        NotificationTargetUser targetUser = notif.getTargetUser();
        String title = notif.getTitle();
        String message = notif.getMessage();
        String invalidTitle = "";

        loginAsAdmin();
        ______TS("Typical Case: Add notification successfully");
        NotificationCreateRequest req = buildCreateRequest(startTime, endTime, type, targetUser, title, message);
        CreateNotificationAction action = getAction(req);
        NotificationData res = (NotificationData) action.execute().getOutput();

        NotificationAttributes createdNotification = logic.getNotification(res.getNotificationId());

        // check that notification returned has same properties as notification created
        assertEquals(createdNotification.getStartTime().toEpochMilli(), res.getStartTimestamp());
        assertEquals(createdNotification.getEndTime().toEpochMilli(), res.getEndTimestamp());
        assertEquals(createdNotification.getType(), res.getNotificationType());
        assertEquals(createdNotification.getTargetUser(), res.getTargetUser());
        assertEquals(createdNotification.getTitle(), res.getTitle());
        assertEquals(createdNotification.getTitle(), res.getMessage());

        // check DB correctly processed request
        assertEquals(startTime, createdNotification.getStartTime().toEpochMilli());
        assertEquals(endTime, createdNotification.getEndTime().toEpochMilli());
        assertEquals(type, createdNotification.getType());
        assertEquals(targetUser, createdNotification.getTargetUser());
        assertEquals(title, createdNotification.getTitle());
        assertEquals(message, createdNotification.getTitle());

        ______TS("Parameters cannot be null");
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(
                buildCreateRequest(startTime, endTime, null, targetUser, title, message));
        assertEquals("Notification type cannot be null", ex.getMessage());

        ex = verifyHttpRequestBodyFailure(buildCreateRequest(startTime, endTime, type, null, title, message));
        assertEquals("Notification target user cannot be null", ex.getMessage());

        ex = verifyHttpRequestBodyFailure(buildCreateRequest(startTime, endTime, type, targetUser, null, message));
        assertEquals("Notification title cannot be null", ex.getMessage());

        ex = verifyHttpRequestBodyFailure(buildCreateRequest(startTime, endTime, type, targetUser, title, null));
        assertEquals("Notification message cannot be null", ex.getMessage());

        ______TS("Timestamps should be greater than 0");
        ex = verifyHttpRequestBodyFailure(buildCreateRequest(-1, endTime, type, targetUser, title, message));
        assertEquals("Start timestamp should be greater than zero", ex.getMessage());

        ex = verifyHttpRequestBodyFailure(buildCreateRequest(startTime, -1, type, targetUser, title, message));
        assertEquals("End timestamp should be greater than zero", ex.getMessage());

        ______TS("Invalid parameter should throw an error");
        verifyHttpParameterFailure(buildCreateRequest(startTime, endTime, type, targetUser, invalidTitle, message));
    }

    @Override
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }

    private NotificationCreateRequest buildCreateRequest(long startTimestamp,
            long endTimestamp,
            NotificationType notificationType,
            NotificationTargetUser targetUser,
            String title,
            String message) {

        NotificationCreateRequest req = new NotificationCreateRequest();

        req.setStartTimestamp(startTimestamp);
        req.setEndTimestamp(endTimestamp);
        req.setNotificationType(notificationType);
        req.setTargetUser(targetUser);
        req.setTitle(title);
        req.setMessage(message);

        return req;
    }
}

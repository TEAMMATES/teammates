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
    private static final String TEST_NOTIFICATION = "notification1";
    private final NotificationAttributes testNotificationAttribute = typicalBundle.notifications.get(TEST_NOTIFICATION);

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
        long startTime = testNotificationAttribute.getStartTime().toEpochMilli();
        long endTime = testNotificationAttribute.getEndTime().toEpochMilli();
        NotificationType type = testNotificationAttribute.getType();
        NotificationTargetUser targetUser = testNotificationAttribute.getTargetUser();
        String title = testNotificationAttribute.getTitle();
        String message = testNotificationAttribute.getMessage();
        String invalidTitle = "";

        loginAsAdmin();
        ______TS("Typical Case: Add notification successfully");
        NotificationCreateRequest req = getTypicalCreateRequest();
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
        req = getTypicalCreateRequest();
        req.setNotificationType(null);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(req);
        assertEquals("Notification type cannot be null", ex.getMessage());

        req = getTypicalCreateRequest();
        req.setTargetUser(null);
        ex = verifyHttpRequestBodyFailure(req);
        assertEquals("Notification target user cannot be null", ex.getMessage());

        req = getTypicalCreateRequest();
        req.setTitle(null);
        ex = verifyHttpRequestBodyFailure(req);
        assertEquals("Notification title cannot be null", ex.getMessage());

        req = getTypicalCreateRequest();
        req.setMessage(null);
        ex = verifyHttpRequestBodyFailure(req);
        assertEquals("Notification message cannot be null", ex.getMessage());

        ______TS("Timestamps should be greater than 0");
        req = getTypicalCreateRequest();
        req.setStartTimestamp(-1);
        ex = verifyHttpRequestBodyFailure(req);
        assertEquals("Start timestamp should be greater than zero", ex.getMessage());

        req = getTypicalCreateRequest();
        req.setEndTimestamp(-1);
        ex = verifyHttpRequestBodyFailure(req);
        assertEquals("End timestamp should be greater than zero", ex.getMessage());

        ______TS("Invalid parameter should throw an error");
        req = getTypicalCreateRequest();
        req.setTitle(invalidTitle);
        verifyHttpParameterFailure(req);
    }

    @Override
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }

    private NotificationCreateRequest getTypicalCreateRequest() {
        NotificationCreateRequest req = new NotificationCreateRequest();

        req.setStartTimestamp(testNotificationAttribute.getStartTime().toEpochMilli());
        req.setEndTimestamp(testNotificationAttribute.getEndTime().toEpochMilli());
        req.setNotificationType(testNotificationAttribute.getType());
        req.setTargetUser(testNotificationAttribute.getTargetUser());
        req.setTitle(testNotificationAttribute.getTitle());
        req.setMessage(testNotificationAttribute.getMessage());

        return req;
    }
}

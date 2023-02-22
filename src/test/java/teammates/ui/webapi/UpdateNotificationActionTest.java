package teammates.ui.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.ui.output.NotificationData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.NotificationUpdateRequest;

/**
 * SUT: {@link UpdateNotificationAction}.
 */
@Ignore
public class UpdateNotificationActionTest extends BaseActionTest<UpdateNotificationAction> {
    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION;
    }

    @Override
    String getRequestMethod() {
        return PUT;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        NotificationAttributes testNotificationAttribute = typicalBundle.notifications.get("notification1");

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, testNotificationAttribute.getNotificationId(),
        };
        NotificationUpdateRequest req = getTypicalUpdateRequest();
        NotificationStyle style = req.getStyle();
        NotificationTargetUser targetUser = req.getTargetUser();
        String title = req.getTitle();
        String message = req.getMessage();
        String invalidTitle = "";
        String invalidNotificationId = "InvalidNotificationId";

        loginAsAdmin();

        ______TS("Typical Case: Update notification successfully");
        req = getTypicalUpdateRequest();
        UpdateNotificationAction action = getAction(req, requestParams);
        NotificationData res = (NotificationData) action.execute().getOutput();

        NotificationAttributes updatedNotification = logic.getNotification(res.getNotificationId());

        // Verify that correctly updated in the DB
        assertEquals(req.getStartTimestamp(), updatedNotification.getStartTime().toEpochMilli());
        assertEquals(req.getEndTimestamp(), updatedNotification.getEndTime().toEpochMilli());
        assertEquals(style, updatedNotification.getStyle());
        assertEquals(targetUser, updatedNotification.getTargetUser());
        assertEquals(title, updatedNotification.getTitle());
        assertEquals(message, updatedNotification.getMessage());

        ______TS("Parameters cannot be null");
        req = getTypicalUpdateRequest();
        req.setStyle(null);
        InvalidHttpRequestBodyException ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("Notification style cannot be null", ex.getMessage());

        req = getTypicalUpdateRequest();
        req.setTargetUser(null);
        ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("Notification target user cannot be null", ex.getMessage());

        req = getTypicalUpdateRequest();
        req.setTitle(null);
        ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("Notification title cannot be null", ex.getMessage());

        req = getTypicalUpdateRequest();
        req.setMessage(null);
        ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("Notification message cannot be null", ex.getMessage());

        ______TS("Timestamps should be greater than 0");
        req = getTypicalUpdateRequest();
        req.setStartTimestamp(-1);
        ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("Start timestamp should be greater than zero", ex.getMessage());

        req = getTypicalUpdateRequest();
        req.setEndTimestamp(-1);
        ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("End timestamp should be greater than zero", ex.getMessage());

        ______TS("Start timestamp should not be after end timestamp");
        req = getTypicalUpdateRequest();
        req.setEndTimestamp(req.getStartTimestamp() - 100);
        ex = verifyHttpRequestBodyFailure(req, requestParams);
        assertEquals("The time when the notification will expire for this notification "
                + "cannot be earlier than the time when the notification will be visible.",
                ex.getMessage());

        ______TS("Invalid parameter should throw an error");
        req = getTypicalUpdateRequest();
        req.setTitle(invalidTitle);
        verifyHttpRequestBodyFailure(req, requestParams);

        ______TS("Non-existent notification should throw an error");
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, invalidNotificationId,
        };
        req = getTypicalUpdateRequest();
        verifyEntityNotFound(req, requestParams);

        ______TS("Not enough request parameters should throw an error");
        req = getTypicalUpdateRequest();
        verifyHttpParameterFailure(req);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }

    private NotificationUpdateRequest getTypicalUpdateRequest() {
        NotificationUpdateRequest req = new NotificationUpdateRequest();

        req.setStartTimestamp(Instant.now().toEpochMilli());
        req.setEndTimestamp(Instant.now().plus(5, ChronoUnit.DAYS).toEpochMilli());
        req.setStyle(NotificationStyle.INFO);
        req.setTargetUser(NotificationTargetUser.GENERAL);
        req.setTitle("New notification title");
        req.setMessage("New notification message");

        return req;
    }
}

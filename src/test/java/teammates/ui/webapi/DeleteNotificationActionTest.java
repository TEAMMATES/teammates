package teammates.ui.webapi;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteNotificationAction}.
 */
@Ignore
public class DeleteNotificationActionTest extends BaseActionTest<DeleteNotificationAction> {
    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION;
    }

    @Override
    String getRequestMethod() {
        return DELETE;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        NotificationAttributes testNotificationAttribute = typicalBundle.notifications.get("notification1");

        loginAsAdmin();

        ______TS("Typical Case: Delete notification successfully");
        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, testNotificationAttribute.getNotificationId(),
        };

        DeleteNotificationAction action = getAction(requestParams);
        JsonResult response = getJsonResult(action);
        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Notification has been deleted.", msg.getMessage());

        verifyAbsentInDatabase(testNotificationAttribute);

        ______TS("Deleting non-existent notification should fail silently");
        String invalidNotificationId = "non-existent notification";
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, invalidNotificationId,
        };

        NotificationAttributes nonExistentNotification = typicalBundle.notifications.get("notification1");
        nonExistentNotification.setNotificationId(invalidNotificationId);

        verifyAbsentInDatabase(nonExistentNotification);

        action = getAction(requestParams);
        response = getJsonResult(action);
        msg = (MessageOutput) response.getOutput();
        assertEquals("Notification has been deleted.", msg.getMessage());

        ______TS("Notification ID cannot be null");
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, null,
        };

        verifyHttpParameterFailure(requestParams);

        ______TS("Not enough request parameters should throw an error");
        verifyHttpParameterFailure();

    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }
}

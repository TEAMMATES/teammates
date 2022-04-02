package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteNotificationAction}.
 */
public class DeleteNotificationActionTest extends BaseActionTest<DeleteNotificationAction> {
    private static final String TEST_NOTIFICATION = "notification1";
    private final NotificationAttributes testNotificationAttribute = typicalBundle.notifications.get(TEST_NOTIFICATION);

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION;
    }

    @Override
    String getRequestMethod() {
        return DELETE;
    }

    @Override
    protected void testExecute() throws Exception {
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

        ______TS("Notification ID cannot be null");
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, null,
        };

        verifyHttpParameterFailure(requestParams);

        ______TS("Deleting non-existent notification");
        // Deleting a non-existent notificiation will not throw an error and fail silently
        requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, "non-existent notification",
        };

        verifyAbsentInDatabase(testNotificationAttribute);

        action = getAction(requestParams);
        response = getJsonResult(action);
        msg = (MessageOutput) response.getOutput();
        assertEquals("Notification has been deleted.", msg.getMessage());
    }

    @Override
    protected void testAccessControl() throws Exception {
        verifyOnlyAdminCanAccess();
    }
}

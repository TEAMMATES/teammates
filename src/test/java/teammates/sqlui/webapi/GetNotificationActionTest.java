package teammates.sqlui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.NotificationData;

/**
 * SUT: {@link GetNotificationAction}.
 */
@Ignore
public class GetNotificationActionTest extends BaseActionTest<GetNotificationAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    public void baseClassSetup() {
        loginAsAdmin();
    }

    @Test
    @Override
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    @Override
    protected void testExecute() {
        // See independent test cases
    }

    @Test
    protected void testExecute_withValidNotificationId_shouldReturnData() {
        NotificationData expected = new NotificationData(typicalBundle.notifications.get("notification1"));
        NotificationData output = (NotificationData) getJsonResult(
                getAction(Const.ParamsNames.NOTIFICATION_ID, "notification1")).getOutput();

        assertEquals(expected, output);
    }

    @Test
    protected void testExecute_withInvalidNotificationId_shouldThrowError() {
        assertEquals("Notification does not exist.",
                assertThrows(EntityNotFoundException.class,
                        getAction(Const.ParamsNames.NOTIFICATION_ID, "invalid-notif")::execute).getMessage());

        assertEquals("The [notificationid] HTTP parameter is null.",
                assertThrows(InvalidHttpParameterException.class,
                        getAction(Const.ParamsNames.NOTIFICATION_ID, null, new String[] {})::execute).getMessage());
    }

}

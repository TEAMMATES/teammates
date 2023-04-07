package teammates.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.ui.output.NotificationData;

/**
 * SUT: {@link GetNotificationAction}.
 */
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
        ______TS("Success: Get existing notification");
        NotificationAttributes notification = typicalBundle.notifications.get("notification1");
        NotificationData expected = new NotificationData(notification);

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, "notification1",
        };

        GetNotificationAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationData output = (NotificationData) jsonResult.getOutput();
        verifyNotificationEquals(expected, output);
    }

    @Test
    protected void testExecute_withInvalidNotificationId_shouldThrowError() {
        ______TS("Failure: Notification does not exist");

        GetNotificationAction action = getAction(Const.ParamsNames.NOTIFICATION_ID, "invalid-notif");
        EntityNotFoundException enfe = assertThrows(EntityNotFoundException.class, action::execute);
        assertEquals("Notification does not exist.", enfe.getMessage());

        ______TS("Failure: Notification id is null");
        GetNotificationAction action2 = getAction(Const.ParamsNames.NOTIFICATION_ID, null, new String[] {});
        InvalidHttpParameterException ihpe = assertThrows(InvalidHttpParameterException.class, action2::execute);
        assertEquals("The [notificationid] HTTP parameter is null.", ihpe.getMessage());
    }

    private void verifyNotificationEquals(NotificationData expected, NotificationData actual) {
        assertEquals(expected.getNotificationId(), actual.getNotificationId());
        assertEquals(expected.getStyle(), actual.getStyle());
        assertEquals(expected.getTargetUser(), actual.getTargetUser());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEquals(expected.getStartTimestamp(), actual.getStartTimestamp());
        assertEquals(expected.getEndTimestamp(), actual.getEndTimestamp());
    }

}

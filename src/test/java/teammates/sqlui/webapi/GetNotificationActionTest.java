package teammates.sqlui.webapi;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.NotificationData;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.GetNotificationAction;
import teammates.ui.webapi.InvalidHttpParameterException;
import teammates.ui.webapi.JsonResult;

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
    protected void testExecute_withValidNotificationId_shouldReturnData() {
        Notification testNotification = getTypicalNotificationWithId();
        NotificationData expected = new NotificationData(testNotification);

        String[] requestParams = new String[] {
                Const.ParamsNames.NOTIFICATION_ID, String.valueOf(testNotification.getId()),
        };

        when(mockLogic.getNotification(testNotification.getId())).thenReturn(testNotification);

        GetNotificationAction action = getAction(requestParams);
        JsonResult jsonResult = getJsonResult(action);

        NotificationData output = (NotificationData) jsonResult.getOutput();
        verifyNotificationEquals(expected, output);

        reset(mockLogic);
    }

    @Test
    protected void testExecute_nonExistentNotification_shouldThrowError() {
        GetNotificationAction action = getAction(Const.ParamsNames.NOTIFICATION_ID, UUID.randomUUID().toString());
        EntityNotFoundException enfe = assertThrows(EntityNotFoundException.class, action::execute);

        assertEquals("Notification does not exist.", enfe.getMessage());
    }

    @Test
    protected void testExecute_notificationIdIsNull_shouldThrowError() {
        GetNotificationAction action = getAction(Const.ParamsNames.NOTIFICATION_ID, null, new String[] {});
        InvalidHttpParameterException ihpe = assertThrows(InvalidHttpParameterException.class, action::execute);

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

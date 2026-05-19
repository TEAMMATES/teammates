package teammates.ui.webapi;

import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Notification;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
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
        EntityNotFoundException enfe = Assertions.assertThrows(EntityNotFoundException.class, action::execute);

        Assertions.assertEquals("Notification does not exist.", enfe.getMessage());
    }

    @Test
    protected void testExecute_notificationIdIsNull_shouldThrowError() {
        String[] submissionParams = new String[] { Const.ParamsNames.NOTIFICATION_ID, null };
        GetNotificationAction action = getAction(submissionParams);
        InvalidHttpParameterException ihpe = Assertions.assertThrows(InvalidHttpParameterException.class, action::execute);

        Assertions.assertEquals("The [notificationid] HTTP parameter is null.", ihpe.getMessage());
    }

    private void verifyNotificationEquals(NotificationData expected, NotificationData actual) {
        Assertions.assertEquals(expected.getNotificationId(), actual.getNotificationId());
        Assertions.assertEquals(expected.getStyle(), actual.getStyle());
        Assertions.assertEquals(expected.getTargetUser(), actual.getTargetUser());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getMessage(), actual.getMessage());
        Assertions.assertEquals(expected.getStartTimestamp(), actual.getStartTimestamp());
        Assertions.assertEquals(expected.getEndTimestamp(), actual.getEndTimestamp());
    }
}

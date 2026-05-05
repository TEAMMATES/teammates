package teammates.ui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.ui.output.ReadNotificationData;
import teammates.ui.request.MarkNotificationAsReadRequest;

/**
 * SUT: {@link MarkNotificationAsReadAction}.
 */
public class MarkNotificationAsReadActionTest extends BaseActionTest<MarkNotificationAsReadAction> {
    Account account;
    Notification testNotification;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION_READ;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    void setUp() {
        account = getTypicalAccount();
        testNotification = getTypicalNotificationWithId();
        loginAsInstructor(account.getGoogleId());
    }

    @Test
    protected void testExecute_markNotificationAsRead_shouldSucceed() throws EntityDoesNotExistException {
        ReadNotification readNotification = new ReadNotification();
        account.addReadNotification(readNotification);
        testNotification.addReadNotification(readNotification);

        when(mockLogic.createReadNotification(
                account.getId(),
                testNotification.getId()
        )).thenReturn(readNotification);
        when(mockLogic.getAccountForGoogleId(account.getGoogleId())).thenReturn(account);

        MarkNotificationAsReadRequest reqBody = new MarkNotificationAsReadRequest(
                testNotification.getId().toString());

        MarkNotificationAsReadAction action = getAction(reqBody);
        JsonResult actionOutput = getJsonResult(action);

        ReadNotificationData response = (ReadNotificationData) actionOutput.getOutput();
        assertEquals(testNotification.getId(), response.getNotificationId());
        assertEquals(account.getId(), response.getAccountId());
        assertEquals(readNotification.getId(), response.getReadNotificationId());
    }

    @Test
    protected void testExecute_markInvalidNotificationAsRead_shouldThrowIllegalArgumentError() {
        MarkNotificationAsReadRequest reqBody =
                new MarkNotificationAsReadRequest("invalid id");

        MarkNotificationAsReadAction action = getAction(reqBody);

        assertThrows(IllegalArgumentException.class, action::execute);
    }
}

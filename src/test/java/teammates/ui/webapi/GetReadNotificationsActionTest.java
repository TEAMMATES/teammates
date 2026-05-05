package teammates.ui.webapi;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.ReadNotification;
import teammates.ui.output.ReadNotificationsData;

/**
 * SUT: {@link GetReadNotificationsAction}.
 */
public class GetReadNotificationsActionTest extends BaseActionTest<GetReadNotificationsAction> {
    /** Number of read notifications used for testing. */
    public static final int READ_NOTIFICATION_COUNT = 2;

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION_READ;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    protected void testExecute_getReadNotificationsAsInstructor_shouldSucceed() {
        Account account = getTypicalAccount();
        List<ReadNotification> testReadNotifications = new ArrayList<>();

        loginAsInstructor(account.getGoogleId());
        for (int i = 0; i < READ_NOTIFICATION_COUNT; i++) {
            ReadNotification readNotification = new ReadNotification();
            account.addReadNotification(readNotification);
            getTypicalNotificationWithId().addReadNotification(readNotification);
            testReadNotifications.add(readNotification);
        }

        when(mockLogic.getAccountForGoogleId(account.getGoogleId())).thenReturn(account);
        when(mockLogic.getReadNotificationsByAccountId(account.getId())).thenReturn(testReadNotifications);

        GetReadNotificationsAction action = getAction();
        JsonResult jsonResult = getJsonResult(action);

        ReadNotificationsData output = (ReadNotificationsData) jsonResult.getOutput();

        List<UUID> readNotificationsData = output.getReadNotifications();

        assertEquals(READ_NOTIFICATION_COUNT, readNotificationsData.size());
        readNotificationsData.forEach(notificationId ->
                assertTrue(testReadNotifications.stream()
                        .anyMatch(readNotification -> readNotification.getNotification().getId().equals(notificationId))));
    }
}

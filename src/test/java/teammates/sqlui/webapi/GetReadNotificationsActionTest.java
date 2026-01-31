package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.ReadNotificationsData;
import teammates.ui.webapi.GetReadNotificationsAction;
import teammates.ui.webapi.JsonResult;

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
        Instructor instructor = getTypicalInstructor();

        List<UUID> testNotificationIds = createTestNotifications(READ_NOTIFICATION_COUNT);

        when(mockLogic.getReadNotificationsId(instructor.getGoogleId())).thenReturn(testNotificationIds);

        ReadNotificationsData data = action();
        verification(data, testNotificationIds);

    }
    private List<UUID> createTestNotifications(int count) {
        List<UUID> testNotificationIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            testNotificationIds.add(UUID.randomUUID());
        }
        return testNotificationIds;
    }
    private ReadNotificationsData action() {
        GetReadNotificationsAction action = getAction();
        JsonResult jsonResult = getJsonResult(action);
        return (ReadNotificationsData) jsonResult.getOutput();
    }

    private void verification(ReadNotificationsData in, List<UUID> ids) {
        List<String> readNotificationsData = in.getReadNotifications();
        readNotificationsData.forEach(notificationId -> assertTrue(ids.contains(UUID.fromString(notificationId))));
        assertEquals(READ_NOTIFICATION_COUNT, readNotificationsData.size());
    }

}

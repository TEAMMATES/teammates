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
        List<Notification> testNotifications = new ArrayList<>();

        loginAsInstructor(instructor.getGoogleId());
        for (int i = 0; i < READ_NOTIFICATION_COUNT; i++) {
            testNotifications.add(getTypicalNotificationWithId());
        }

        List<UUID> testNotificationIds = testNotifications.stream().map(Notification::getId).collect(Collectors.toList());
        when(mockLogic.getReadNotificationsId(instructor.getGoogleId())).thenReturn(testNotificationIds);

        GetReadNotificationsAction action = getAction();
        JsonResult jsonResult = getJsonResult(action);

        ReadNotificationsData output = (ReadNotificationsData) jsonResult.getOutput();

        List<String> readNotificationsData = output.getReadNotifications();

        readNotificationsData.forEach(notificationId ->
                assertTrue(testNotificationIds.contains(UUID.fromString(notificationId))));
        assertEquals(READ_NOTIFICATION_COUNT, readNotificationsData.size());
    }
}

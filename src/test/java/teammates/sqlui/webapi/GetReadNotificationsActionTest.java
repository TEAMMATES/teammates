package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.ReadNotificationsData;
import teammates.ui.webapi.GetReadNotificationsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetReadNotificationsAction}.
 */
public class GetReadNotificationsActionTest extends BaseActionTest<GetReadNotificationsAction> {

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
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());

        List<NotificationAttributes> notificationAttributesList = List.of(
                typicalBundle.notifications.get("notification1"),
                typicalBundle.notifications.get("notification3"));
        List<Notification> testNotifications = new ArrayList<>();
        for (NotificationAttributes notificationAttributes : notificationAttributesList) {
            testNotifications.add(new Notification(
                    notificationAttributes.getStartTime(),
                    notificationAttributes.getEndTime(),
                    notificationAttributes.getStyle(),
                    notificationAttributes.getTargetUser(),
                    notificationAttributes.getTitle(),
                    notificationAttributes.getMessage()));
        }
        List<UUID> testNotificationIds = testNotifications.stream().map(Notification::getId).collect(Collectors.toList());
        when(mockLogic.getReadNotificationsId(instructor.getGoogleId())).thenReturn(testNotificationIds);

        GetReadNotificationsAction action = getAction();
        JsonResult jsonResult = getJsonResult(action);

        ReadNotificationsData output = (ReadNotificationsData) jsonResult.getOutput();

        List<String> readNotificationsData = output.getReadNotifications();

        readNotificationsData.forEach(notificationId ->
                assertTrue(testNotificationIds.contains(UUID.fromString(notificationId))));
        assertEquals(2, readNotificationsData.size());
    }
}

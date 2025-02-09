package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Notification;
import teammates.ui.output.ReadNotificationsData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.MarkNotificationAsReadRequest;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.MarkNotificationAsReadAction;

/**
 * SUT: {@link MarkNotificationAsReadAction}.
 */
public class MarkNotificationAsReadActionTest extends BaseActionTest<MarkNotificationAsReadAction> {
    InstructorAttributes instructor;
    String instructorId;
    NotificationAttributes notification;
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
        instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        instructorId = instructor.getGoogleId();
        notification = typicalBundle.notifications.get("notification5");
        loginAsInstructor(instructorId);
        testNotification = new Notification(
                notification.getStartTime(),
                notification.getEndTime(),
                notification.getStyle(),
                notification.getTargetUser(),
                notification.getTitle(),
                notification.getMessage());
    }

    @Test
    protected void testExecute_markNotificationAsRead_shouldSucceed() throws Exception {
        when(mockLogic.updateReadNotifications(
                instructorId,
                testNotification.getId(),
                notification.getEndTime()
        )).thenReturn(List.of(testNotification.getId()));

        MarkNotificationAsReadRequest reqBody = new MarkNotificationAsReadRequest(
                testNotification.getId().toString(), testNotification.getEndTime().toEpochMilli());

        MarkNotificationAsReadAction action = getAction(reqBody);
        JsonResult actionOutput = getJsonResult(action);

        ReadNotificationsData response = (ReadNotificationsData) actionOutput.getOutput();
        List<String> readNotifications = response.getReadNotifications();

        assertTrue(readNotifications.contains(testNotification.getId().toString()));
    }

    @Test
    protected void testExecute_markNonExistentNotificationAsRead_shouldFail() {
        MarkNotificationAsReadRequest reqBody =
                new MarkNotificationAsReadRequest("invalid id", notification.getEndTime().toEpochMilli());

        MarkNotificationAsReadAction action = getAction(reqBody);

        assertThrows(IllegalArgumentException.class, () -> action.execute());
    }

    @Test
    protected void testExecute_notificationEndTimeIsZero_shouldFail() {
        MarkNotificationAsReadRequest reqBody =
                new MarkNotificationAsReadRequest(testNotification.getId().toString(), 0L);
        verifyHttpRequestBodyFailure(reqBody);
    }

    @Test
    protected void testExecute_markExpiredNotificationAsRead_shouldFail() throws Exception {
        when(mockLogic.updateReadNotifications(
                instructorId,
                testNotification.getId(),
                testNotification.getEndTime()
        )).thenThrow(new InvalidParametersException("Trying to mark an expired notification as read."));

        MarkNotificationAsReadRequest reqBody = new MarkNotificationAsReadRequest(
                testNotification.getId().toString(),
                testNotification.getEndTime().toEpochMilli());

        MarkNotificationAsReadAction action = getAction(reqBody);

        assertThrows(InvalidHttpRequestBodyException.class, () -> action.execute());
    }
}

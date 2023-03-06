package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.ui.output.ReadNotificationsData;
import teammates.ui.request.MarkNotificationAsReadRequest;

/**
 * SUT: {@link MarkNotificationAsReadAction}.
 */
@Ignore
public class MarkNotificationAsReadActionTest extends BaseActionTest<MarkNotificationAsReadAction> {

    @Override
    String getActionUri() {
        return Const.ResourceURIs.NOTIFICATION_READ;
    }

    @Override
    String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();
        NotificationAttributes notification = typicalBundle.notifications.get("notification5");
        loginAsInstructor(instructorId);

        ______TS("Typical success case: mark a notification as read");
        MarkNotificationAsReadRequest reqBody = new MarkNotificationAsReadRequest(
                notification.getNotificationId(), notification.getEndTime().toEpochMilli());
        MarkNotificationAsReadAction action = getAction(reqBody);
        JsonResult actionOutput = getJsonResult(action);
        ReadNotificationsData response = (ReadNotificationsData) actionOutput.getOutput();
        List<String> readNotifications = response.getReadNotifications();
        assertTrue(readNotifications.contains(notification.getNotificationId()));

        ______TS("Invalid case: mark non-existent notification as read");
        reqBody = new MarkNotificationAsReadRequest("invalid id", notification.getEndTime().toEpochMilli());
        verifyEntityNotFound(reqBody);

        ______TS("Invalid case: notification end time is zero");
        reqBody = new MarkNotificationAsReadRequest(notification.getNotificationId(), 0L);
        verifyHttpRequestBodyFailure(reqBody);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyAnyLoggedInUserCanAccess();
    }
}

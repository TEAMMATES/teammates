package teammates.ui.webapi;

import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.Const;
import teammates.ui.output.AccountData;
import teammates.ui.request.ReadNotificationCreateRequest;

/**
 * SUT: {@link CreateReadNotificationAction}.
 */
public class CreateReadNotificationActionTest extends BaseActionTest<CreateReadNotificationAction> {

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

        ______TS("Typical case: add a read notification successfully");
        ReadNotificationCreateRequest reqBody = new ReadNotificationCreateRequest(
                notification.getNotificationId(), notification.getEndTime().toEpochMilli());
        CreateReadNotificationAction action = getAction(reqBody);
        JsonResult actionOutput = getJsonResult(action);
        AccountData response = (AccountData) actionOutput.getOutput();
        Map<String, Long> readNotifications = response.getReadNotifications();
        assertTrue(readNotifications.containsKey(notification.getNotificationId()));

        ______TS("Invalid case: Invalid notification id provided");
        reqBody = new ReadNotificationCreateRequest(
                "invalid id", notification.getEndTime().toEpochMilli());
        verifyHttpRequestBodyFailure(reqBody);

        ______TS("Invalid case: Invalid endTime.");
        reqBody = new ReadNotificationCreateRequest(
                notification.getNotificationId(), null);
        verifyHttpRequestBodyFailure(reqBody);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyAnyLoggedInUserCanAccess();
    }
}

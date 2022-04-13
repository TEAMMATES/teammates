package teammates.ui.webapi;

import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.ReadNotificationsData;

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
    @Override
    protected void testExecute() {
        ______TS("Typical success case: User request to fetch read notifications");
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor.getGoogleId());
        GetReadNotificationsAction action = getAction();
        JsonResult jsonResult = getJsonResult(action);

        ReadNotificationsData output = (ReadNotificationsData) jsonResult.getOutput();

        Map<String, Long> readNotificationsData = output.getReadNotifications();
        assertNotNull(readNotificationsData.get("notification1"));
        assertNotNull(readNotificationsData.get("notification3"));
    }

    @Test
    @Override
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }
}

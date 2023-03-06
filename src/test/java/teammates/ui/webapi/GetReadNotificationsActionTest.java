package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.ReadNotificationsData;

/**
 * SUT: {@link GetReadNotificationsAction}.
 */
@Ignore
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

        List<String> readNotificationsData = output.getReadNotifications();

        assertTrue(readNotificationsData.contains("notification1"));
        assertTrue(readNotificationsData.contains("notification3"));
        assertEquals(2, readNotificationsData.size());
    }

    @Test
    @Override
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }
}

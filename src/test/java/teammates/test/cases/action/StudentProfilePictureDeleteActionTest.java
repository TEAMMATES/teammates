package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentProfilePictureDeleteAction;

/**
 * SUT: {@link StudentProfilePictureDeleteAction}.
 */
public class StudentProfilePictureDeleteActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.STUDENT_PROFILE_PICTURE_DELETE;
    }

    @Override
    protected StudentProfilePictureDeleteAction getAction(String... params) {
        return (StudentProfilePictureDeleteAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testExecuteAndPostProcess() throws Exception {
        AccountAttributes student = typicalBundle.accounts.get("student2InCourse1");
        String expectedUrl = getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, false, student.googleId);
        gaeSimulation.loginAsStudent(student.googleId);
        StudentProfilePictureDeleteAction action = getAction(new String[0]);
        RedirectResult result = getRedirectResult(action);

        assertEquals(expectedUrl, result.getDestinationWithParams());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyAnyRegisteredUserCanAccess(new String[0]);
    }
}

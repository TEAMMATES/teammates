package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.InstructorsDb;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseJoinAction;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;

/**
 * SUT: {@link InstructorCourseJoinAction}.
 */
public class InstructorCourseJoinActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_JOIN;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorsDb instrDb = new InstructorsDb();
        // Reassign to let "key" variable in "instructor" not to be null
        instructor = instrDb.getInstructorForGoogleId(instructor.courseId, instructor.googleId);
        String invalidEncryptedKey = StringHelper.encrypt("invalidKey");

        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("Invalid key, redirect for confirmation again");

        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidEncryptedKey
        };

        InstructorCourseJoinAction confirmAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(confirmAction);

        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_JOIN_CONFIRMATION
                + "?error=false&user=idOfInstructor1OfCourse1"
                + "&key=" + invalidEncryptedKey, pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        String expectedLogSegment = "Action Instructor Clicked Join Link"
                + "<br>Google ID: " + instructor.googleId
                + "<br>Key: " + invalidEncryptedKey;
        AssertHelper.assertContains(expectedLogSegment, confirmAction.getLogMessage());

        ______TS("Already registered instructor, redirect straight to authentication page");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(instructor.key)
        };

        confirmAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(confirmAction);

        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED
                        + "?key=" + StringHelper.encrypt(instructor.key)
                        + "&error=false&user=idOfInstructor1OfCourse1", redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals("", redirectResult.getStatusMessage());

        expectedLogSegment = "Action Instructor Clicked Join Link"
                + "<br>Google ID: " + instructor.googleId
                + "<br>Key: " + StringHelper.encrypt(instructor.key);
        AssertHelper.assertContains(expectedLogSegment, confirmAction.getLogMessage());

        ______TS("Typical case: unregistered instructor, redirect to confirmation page");

        instructor = new InstructorAttributes(null, instructor.courseId, "New Instructor", "ICJAT.instr@email.com");
        InstructorsLogic.inst().createInstructor(instructor);
        instructor.googleId = "ICJAT.instr";

        AccountAttributes newInstructorAccount = new AccountAttributes(
                instructor.googleId, instructor.name, false,
                instructor.email, "TEAMMATES Test Institute 5");
        AccountsLogic.inst().createAccount(newInstructorAccount);

        InstructorAttributes newInstructor = instrDb.getInstructorForEmail(instructor.courseId, instructor.email);

        gaeSimulation.loginUser(instructor.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(newInstructor.key)
        };

        confirmAction = getAction(submissionParams);
        pageResult = getShowPageResult(confirmAction);

        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_JOIN_CONFIRMATION
                     + "?error=false&user=ICJAT.instr"
                     + "&key=" + StringHelper.encrypt(newInstructor.key),
                     pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        expectedLogSegment = "Action Instructor Clicked Join Link"
                + "<br>Google ID: " + instructor.googleId
                + "<br>Key: " + StringHelper.encrypt(newInstructor.key);
        AssertHelper.assertContains(expectedLogSegment, confirmAction.getLogMessage());
    }

    @Override
    protected InstructorCourseJoinAction getAction(String... params) {
        return (InstructorCourseJoinAction) gaeSimulation.getActionObject(getActionUri(), params);
    }
}

package teammates.test.cases.action;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseInstructorCopyAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link teammates.ui.controller.InstructorCourseInstructorCopyAction}.
 */
public class InstructorCourseInstructorCopyActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_COPY;
    }

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    protected String getPageResultDestination(
            String parentUri, String courseId, String userId, boolean isError) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.COURSE_ID, courseId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        return pageDestination;
    }

    @BeforeMethod
    public void refreshTestData() {
        super.prepareTestData();
    }

    @Override
    @Test
    public void testAccessControl() {
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
        verifyUnaccessibleWithoutModifyInstructorPrivilege(params);
    }

    @Override
    protected InstructorCourseInstructorCopyAction getAction(String... params) {
        return (InstructorCourseInstructorCopyAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {

        InstructorAttributes instructor3OfCourse1n2 = typicalBundle.instructors.get("instructor3OfCourse2");

        gaeSimulation.loginAsInstructor(instructor3OfCourse1n2.googleId);

        ______TS("Typical case: add two instructors from Course1 to Course2");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse2",
                Const.ParamsNames.INSTRUCTOR_EMAIL + "-0", instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID + "-0", instructor1OfCourse1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL + "-1", instructor2OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID + "-1", instructor2OfCourse1.getCourseId(),
        };

        InstructorCourseInstructorCopyAction a = getAction(params);
        RedirectResult rr = getRedirectResult(a);

        verifySpecifiedTasksAdded(a, Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 2);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                        instructor3OfCourse1n2.courseId,
                        instructor3OfCourse1n2.googleId,
                        false),
                rr.getDestinationWithParams());

        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseInstructorCopy|||"
                + "instructorCourseInstructorCopy|||true|||Instructor|||"
                + "Instructor 3 of Course 1 and 2|||idOfInstructor3|||instr3@course1n2.tmt|||"
                + "Added Instructor for Course: <span class=\"bold\">[idOfTypicalCourse2]</span>.<br>"
                + "<span class=\"bold\">instructor1@course1.tmt:</span> Instructor1 Course1"
                + "Added Instructor for Course: <span class=\"bold\">[idOfTypicalCourse2]</span>.<br>"
                + "<span class=\"bold\">instructor2@course1.tmt:</span> Instructor2 Course1|||"
                + "/page/instructorCourseInstructorCopy";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Success with error: copy the same instructor from two different courses");

        InstructorAttributes instructorInChargeOfCopy =
                typicalBundle.instructors.get("instructorInChargeOfCopyInstructorOfCourseCopyInstructorTo");
        gaeSimulation.loginAsInstructor(instructorInChargeOfCopy.googleId);

        InstructorAttributes instructorToBeCopiedOfCourse1CopyInstructorFrom =
                typicalBundle.instructors.get("instructorToBeCopiedOfCourse1CopyInstructorFrom");
        InstructorAttributes instructorToBeCopiedOfCourse2CopyInstructorFrom =
                typicalBundle.instructors.get("instructorToBeCopiedOfCourse2CopyInstructorFrom");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfCourseCopyInstructorTo",
                Const.ParamsNames.INSTRUCTOR_EMAIL + "-0", instructorToBeCopiedOfCourse1CopyInstructorFrom.getEmail(),
                Const.ParamsNames.COURSE_ID + "-0", instructorToBeCopiedOfCourse1CopyInstructorFrom.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL + "-1", instructorToBeCopiedOfCourse2CopyInstructorFrom.getEmail(),
                Const.ParamsNames.COURSE_ID + "-1", instructorToBeCopiedOfCourse2CopyInstructorFrom.getCourseId(),
        };

        a = getAction(params);
        rr = getRedirectResult(a);

        verifySpecifiedTasksAdded(a, Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME, 1);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                        instructorInChargeOfCopy.courseId,
                        instructorInChargeOfCopy.googleId,
                        true),
                rr.getDestinationWithParams());

        expectedLogMessage = "TEAMMATESLOG|||instructorCourseInstructorCopy|||"
                + "instructorCourseInstructorCopy|||true|||Instructor|||"
                + "Instructor In Charge Of Copying Other Instructors|||"
                + "idOfInstructorInChargeOfCopyingInstructors|||instructorInChargeOfCopyingInstructors@copy.tmt|||"
                + "Servlet Action Failure : Trying to create a Instructor that exists: "
                + "idOfCourseCopyInstructorTo/instructorToBeCopied@copy.tmt|||/page/instructorCourseInstructorCopy";

        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Error: Indicate no instructors to be copied to Course1");

        InstructorAttributes instructor1Course1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1Course1.googleId);

        params = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1"
        };

        a = getAction(params);
        rr = getRedirectResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                        instructor1Course1.courseId,
                        instructor1Course1.googleId,
                        true),
                rr.getDestinationWithParams());

        expectedLogMessage = "TEAMMATESLOG|||instructorCourseInstructorCopy|||"
                + "instructorCourseInstructorCopy|||true|||Instructor|||"
                + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "|||/page/instructorCourseInstructorCopy";

        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Masquerade mode: copy an instructor");

        gaeSimulation.loginAsInstructor(instructor3OfCourse1n2.googleId);

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");

        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        params = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse2",
                Const.ParamsNames.INSTRUCTOR_EMAIL + "-0", helperOfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID + "-0", helperOfCourse1.getCourseId(),
        };

        params = addUserIdToParams(instructor3OfCourse1n2.googleId, params);

        a = getAction(params);
        rr = getRedirectResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE,
                        instructor3OfCourse1n2.courseId,
                        instructor3OfCourse1n2.googleId,
                        false),
                rr.getDestinationWithParams());

        expectedLogMessage = "TEAMMATESLOG|||instructorCourseInstructorCopy|||"
                + "instructorCourseInstructorCopy|||true|||Instructor(M)|||"
                + "Instructor 3 of Course 1 and 2|||idOfInstructor3|||instr3@course1n2.tmt|||"
                + "Added Instructor for Course: <span class=\"bold\">[idOfTypicalCourse2]</span>.<br>"
                + "<span class=\"bold\">helper@course1.tmt:</span> Helper Course1|||"
                + "/page/instructorCourseInstructorCopy";

        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedLogMessage, a.getLogMessage(), adminUserId);
    }
}

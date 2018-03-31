package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseStudentDeleteAllAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorCourseStudentDeleteAllAction}.
 */
public class InstructorCourseStudentDeleteAllActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE_ALL;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("success: delete all students");
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        InstructorCourseStudentDeleteAllAction action = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(action);

        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE, redirectResult.destination);
        assertFalse(redirectResult.isError);
        assertEquals(Const.StatusMessages.STUDENTS_DELETED, redirectResult.getStatusMessage());

        AssertHelper.assertLogMessageEquals("TEAMMATESLOG|||instructorCourseStudentDeleteAll|||"
                + "instructorCourseStudentDeleteAll|||true|||Instructor|||Instructor 1 of Course 1|||"
                + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||All the Students in Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span> are deleted.|||"
                + "/page/instructorCourseStudentDeleteAll", action.getLogMessage());

    }

    @Override
    protected InstructorCourseStudentDeleteAllAction getAction(String... params) {
        return (InstructorCourseStudentDeleteAllAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        verifyUnaccessibleWithoutModifyStudentPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}

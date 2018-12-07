package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseStudentDetailsPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorCourseStudentDetailsPageData;

/**
 * SUT: {@link InstructorCourseStudentDetailsPageAction}.
 */
public class InstructorCourseStudentDetailsPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String instructorId = instructor1OfCourse1.googleId;
        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Invalid parameters");

        //no parameters
        verifyAssumptionFailure();

        //null student email
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
        };
        verifyAssumptionFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };
        verifyAssumptionFailure(invalidParams);

        ______TS("Typical case, view student detail");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };

        InstructorCourseStudentDetailsPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS,
                        false,
                        "idOfInstructor1OfCourse1"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorCourseStudentDetailsPageData pageData = (InstructorCourseStudentDetailsPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(student1InCourse1.name, pageData.getStudentInfoTable().getName());
        assertEquals(student1InCourse1.email, pageData.getStudentInfoTable().getEmail());
        assertEquals(student1InCourse1.section, pageData.getStudentInfoTable().getSection());
        assertEquals(student1InCourse1.team, pageData.getStudentInfoTable().getTeam());
        assertEquals(student1InCourse1.comments, pageData.getStudentInfoTable().getComments());
        assertEquals(student1InCourse1.course, pageData.getStudentInfoTable().getCourseId());

        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseStudentDetailsPage|||instructorCourseStudentDetailsPage"
                + "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1"
                + "|||instr1@course1.tmt|||instructorCourseStudentDetails Page Load<br>Viewing details for Student "
                + "<span class=\"bold\">student1InCourse1@gmail.tmt</span> in Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                + "|||/page/instructorCourseStudentDetailsPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
    }

    @Override
    protected InstructorCourseStudentDetailsPageAction getAction(String... params) {
        return (InstructorCourseStudentDetailsPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.email
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutViewStudentInSectionsPrivilege(submissionParams);
    }

}

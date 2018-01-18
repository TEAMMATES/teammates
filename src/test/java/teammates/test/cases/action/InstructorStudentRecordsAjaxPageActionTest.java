package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorStudentRecordsAjaxPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorStudentRecordsAjaxPageData;

/**
 * SUT: {@link InstructorStudentRecordsAjaxPageAction}.
 */
public class InstructorStudentRecordsAjaxPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_AJAX_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");
        StudentAttributes student = typicalBundle.students.get("student2InCourse1");
        String instructorId = instructor.googleId;

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Typical case: specific session name");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session"
        };

        InstructorStudentRecordsAjaxPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS_AJAX, false, "idOfInstructor3"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorStudentRecordsAjaxPageData data = (InstructorStudentRecordsAjaxPageData) r.data;
        assertEquals(1, data.getResultsTables().size());

    }

    @Override
    protected InstructorStudentRecordsAjaxPageAction getAction(String... params) {
        return (InstructorStudentRecordsAjaxPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");
        StudentAttributes student = typicalBundle.students.get("student2InCourse1");
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session"
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);

        ______TS("Instructor cannot view sections without View-Student-In-Sections privilege");

        instructor = typicalBundle.instructors.get("helperOfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_EMAIL, student.email,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "First feedback session"
        };

        InstructorStudentRecordsAjaxPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);
        a = getAction(submissionParams);
        r = getShowPageResult(a);

        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_STUDENT_RECORDS_AJAX, false, "idOfHelperOfCourse1"),
                r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorStudentRecordsAjaxPageData data = (InstructorStudentRecordsAjaxPageData) r.data;
        assertEquals(0, data.getResultsTables().size());
    }

}

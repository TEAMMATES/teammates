package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseEnrollPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorCourseEnrollPageData;

/**
 * SUT: {@link InstructorCourseEnrollPageAction}.
 */
public class InstructorCourseEnrollPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        visitEnrollPage_withInvalidRequestParams_throwsException();
        visitEnrollPage_forCourseWithoutResponses_noWarningMessage();
        visitEnrollPage_forCourseWithResponses_hasWarningMessage();
        visitEnrollPage_inMasqueradeMode();
    }

    private void visitEnrollPage_withInvalidRequestParams_throwsException() {
        ______TS("Not enough parameters");

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor4");
        gaeSimulation.loginAsInstructor(instructor.getGoogleId());

        verifyAssumptionFailure();
    }

    private void visitEnrollPage_forCourseWithoutResponses_noWarningMessage() {
        ______TS("Typical case 1: open the enroll page of a course without existing feedback responses");

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor4");
        gaeSimulation.loginAsInstructor(instructor.getGoogleId());

        String courseId = instructor.getCourseId();
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };
        InstructorCourseEnrollPageAction enrollPageAction = getAction(submissionParams);

        ShowPageResult pageResult = getShowPageResult(enrollPageAction);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, false, "idOfInstructor4"),
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        InstructorCourseEnrollPageData pageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());
        assertNull(pageData.getEnrollStudents());

        String expectedLogSegment = String.format(
                Const.StatusMessages.ADMIN_LOG_INSTRUCTOR_COURSE_ENROLL_PAGE_LOAD, courseId);
        AssertHelper.assertContains(expectedLogSegment, enrollPageAction.getLogMessage());
    }

    private void visitEnrollPage_forCourseWithResponses_hasWarningMessage() {
        ______TS("Typical case 2: open the enroll page of a course with existing feedback responses");

        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.getGoogleId());

        String courseId = instructor.getCourseId();
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };
        InstructorCourseEnrollPageAction enrollPageAction = getAction(submissionParams);

        ShowPageResult pageResult = getShowPageResult(enrollPageAction);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, false, "idOfInstructor1OfCourse1"),
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals(Const.StatusMessages.COURSE_ENROLL_POSSIBLE_DATA_LOSS, pageResult.getStatusMessage());

        InstructorCourseEnrollPageData pageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());
        assertNull(pageData.getEnrollStudents());

        String expectedLogSegment = String.format(
                Const.StatusMessages.ADMIN_LOG_INSTRUCTOR_COURSE_ENROLL_PAGE_LOAD, courseId);
        AssertHelper.assertContains(expectedLogSegment, enrollPageAction.getLogMessage());
    }

    private void visitEnrollPage_inMasqueradeMode() {
        ______TS("Masquerade mode");

        gaeSimulation.loginAsAdmin("admin.user");

        InstructorAttributes instructorToMasquerade = typicalBundle.instructors.get("instructor4");
        String instructorId = instructorToMasquerade.googleId;
        String courseId = instructorToMasquerade.courseId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId
        };
        InstructorCourseEnrollPageAction enrollPageAction = getAction(submissionParams);

        ShowPageResult pageResult = getShowPageResult(enrollPageAction);
        assertEquals(
                getPageResultDestination(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, false, "idOfInstructor4"),
                pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        InstructorCourseEnrollPageData pageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());
        assertNull(pageData.getEnrollStudents());

        String expectedLogSegment = String.format(
                Const.StatusMessages.ADMIN_LOG_INSTRUCTOR_COURSE_ENROLL_PAGE_LOAD, courseId);
        AssertHelper.assertContains(expectedLogSegment, enrollPageAction.getLogMessage());
    }

    @Override
    protected InstructorCourseEnrollPageAction getAction(String... params) {
        return (InstructorCourseEnrollPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalBundle.instructors.get("instructor1OfCourse1").courseId
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyUnaccessibleWithoutModifyStudentPrivilege(submissionParams);
    }
}

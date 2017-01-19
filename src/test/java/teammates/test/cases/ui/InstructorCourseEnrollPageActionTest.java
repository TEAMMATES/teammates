package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseEnrollPageAction;
import teammates.ui.controller.InstructorCourseEnrollPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorCourseEnrollPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        visitEnrollPage_withInvalidRequestParams_throwsException();
        visitEnrollPage_forCourseWithoutResponses_noWarningMessage();
        visitEnrollPage_forCourseWithResponses_hasWarningMessage();
        visitEnrollPage_inMasqueradeMode();
    }

    private void visitEnrollPage_withInvalidRequestParams_throwsException() {
        ______TS("Not enough parameters");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor4");
        gaeSimulation.loginAsInstructor(instructor.getGoogleId());

        verifyAssumptionFailure();
    }

    private void visitEnrollPage_forCourseWithoutResponses_noWarningMessage() {
        ______TS("Typical case 1: open the enroll page of a course without existing feedback responses");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor4");
        gaeSimulation.loginAsInstructor(instructor.getGoogleId());

        String courseId = instructor.getCourseId();
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };
        InstructorCourseEnrollPageAction enrollPageAction = getAction(submissionParams);
        
        ShowPageResult pageResult = getShowPageResult(enrollPageAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL + "?error=false&user=idOfInstructor4",
                     pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());

        InstructorCourseEnrollPageData pageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());
        assertEquals(null, pageData.getEnrollStudents());

        String expectedLogSegment = String.format(
                Const.StatusMessages.ADMIN_LOG_INSTRUCTOR_COURSE_ENROLL_PAGE_LOAD, courseId);
        AssertHelper.assertContains(expectedLogSegment, enrollPageAction.getLogMessage());
    }

    private void visitEnrollPage_forCourseWithResponses_hasWarningMessage() {
        ______TS("Typical case 2: open the enroll page of a course with existing feedback responses");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.getGoogleId());

        String courseId = instructor.getCourseId();
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };
        InstructorCourseEnrollPageAction enrollPageAction = getAction(submissionParams);

        ShowPageResult pageResult = getShowPageResult(enrollPageAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL + "?error=false&user=idOfInstructor1OfCourse1",
                     pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals(Const.StatusMessages.COURSE_ENROLL_POSSIBLE_DATA_LOSS, pageResult.getStatusMessage());

        InstructorCourseEnrollPageData pageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());
        assertEquals(null, pageData.getEnrollStudents());

        String expectedLogSegment = String.format(
                Const.StatusMessages.ADMIN_LOG_INSTRUCTOR_COURSE_ENROLL_PAGE_LOAD, courseId);
        AssertHelper.assertContains(expectedLogSegment, enrollPageAction.getLogMessage());
    }

    private void visitEnrollPage_inMasqueradeMode() {
        ______TS("Masquerade mode");

        gaeSimulation.loginAsAdmin("admin.user");

        InstructorAttributes instructorToMasquerade = dataBundle.instructors.get("instructor4");
        String instructorId = instructorToMasquerade.googleId;
        String courseId = instructorToMasquerade.courseId;

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructorId,
                Const.ParamsNames.COURSE_ID, courseId
        };
        InstructorCourseEnrollPageAction enrollPageAction = getAction(submissionParams);

        ShowPageResult pageResult = getShowPageResult(enrollPageAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL + "?error=false&user=idOfInstructor4",
                     pageResult.getDestinationWithParams());
        assertFalse(pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        InstructorCourseEnrollPageData pageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());
        assertEquals(null, pageData.getEnrollStudents());

        String expectedLogSegment = String.format(
                Const.StatusMessages.ADMIN_LOG_INSTRUCTOR_COURSE_ENROLL_PAGE_LOAD, courseId);
        AssertHelper.assertContains(expectedLogSegment, enrollPageAction.getLogMessage());
    }

    private InstructorCourseEnrollPageAction getAction(String... params) {
        return (InstructorCourseEnrollPageAction) gaeSimulation.getActionObject(uri, params);
    }
}

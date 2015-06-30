package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

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
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor.googleId;
        String courseId = instructor.courseId;

        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("Not enough parameters");
        verifyAssumptionFailure();

        ______TS("Typical case: open the enroll page");
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId
        };
        InstructorCourseEnrollPageAction enrollPageAction = getAction(submissionParams);
        
        ShowPageResult pageResult = getShowPageResult(enrollPageAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL + "?error=false&user=idOfInstructor1OfCourse1", pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        InstructorCourseEnrollPageData pageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());
        assertEquals(null, pageData.getEnrollStudents());
        
        String expectedLogSegment = "instructorCourseEnroll Page Load<br>"
                + "Enrollment for Course <span class=\"bold\">[" + courseId + "]</span>"; 
        AssertHelper.assertContains(expectedLogSegment, enrollPageAction.getLogMessage());

        ______TS("Masquerade mode");
        instructor = dataBundle.instructors.get("instructor4");
        instructorId = instructor.googleId;
        courseId = instructor.courseId;

        gaeSimulation.loginAsAdmin("admin.user");
        submissionParams = new String[]{
            Const.ParamsNames.COURSE_ID, courseId
        };
        enrollPageAction = getAction(addUserIdToParams(instructorId, submissionParams));
        pageResult = getShowPageResult(enrollPageAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL + "?error=false&user=idOfInstructor4", pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        pageData = (InstructorCourseEnrollPageData) pageResult.data;
        assertEquals(courseId, pageData.getCourseId());
        assertEquals(null, pageData.getEnrollStudents());
        
        expectedLogSegment = "instructorCourseEnroll Page Load<br>"
                + "Enrollment for Course <span class=\"bold\">[" + courseId + "]</span>"; 
        AssertHelper.assertContains(expectedLogSegment, enrollPageAction.getLogMessage());
    }

    private InstructorCourseEnrollPageAction getAction(String... params) throws Exception {
        return (InstructorCourseEnrollPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}

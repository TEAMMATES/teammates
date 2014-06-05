package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseEnrollPageAction;
import teammates.ui.controller.InstructorCourseEnrollPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorCourseEnrollPageActionTest extends BaseActionTest {

    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
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
        assertEquals(courseId, pageData.courseId);
        assertEquals(null, pageData.enrollStudents);
        
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
        assertEquals(courseId, pageData.courseId);
        assertEquals(null, pageData.enrollStudents);
        
        expectedLogSegment = "instructorCourseEnroll Page Load<br>"
                + "Enrollment for Course <span class=\"bold\">[" + courseId + "]</span>"; 
        AssertHelper.assertContains(expectedLogSegment, enrollPageAction.getLogMessage());
    }

    private InstructorCourseEnrollPageAction getAction(String... params) throws Exception {
        return (InstructorCourseEnrollPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}

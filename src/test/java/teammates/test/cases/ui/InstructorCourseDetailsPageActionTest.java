package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorCourseDetailsPageAction;
import teammates.ui.controller.InstructorCourseDetailsPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorCourseDetailsPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor1OfCourse1.googleId);
        
         ______TS("Not enough parameters");
        verifyAssumptionFailure();

        ______TS("Typical Case, Course with at least one student");
        String[] submissionParams = new String[]{
            Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId
        };
        InstructorCourseDetailsPageAction pageAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_DETAILS+"?error=false&user=idOfInstructor1OfCourse1", pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        InstructorCourseDetailsPageData pageData = (InstructorCourseDetailsPageData)pageResult.data;
        assertEquals(4, pageData.instructors.size());

        assertEquals("idOfTypicalCourse1", pageData.courseDetails.course.id);
        assertEquals("Typical Course 1 with 2 Evals", pageData.courseDetails.course.name);
        assertEquals(2, pageData.courseDetails.stats.teamsTotal);
        assertEquals(5, pageData.courseDetails.stats.studentsTotal);
        assertEquals(0, pageData.courseDetails.stats.unregisteredTotal);
        assertEquals(2, pageData.courseDetails.evaluations.size());
        assertEquals(0, pageData.courseDetails.feedbackSessions.size());

        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseDetailsPage|||instructorCourseDetailsPage|||"
        + "true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
        + "instructorCourseDetails Page Load<br>Viewing Course Details for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
        + "|||/page/instructorCourseDetailsPage";
        assertEquals(expectedLogMessage, pageAction.getLogMessage());
        
        ______TS("Masquerade mode, Course with no student");
        gaeSimulation.loginAsAdmin("admin.user");
        InstructorAttributes instructor4 = dataBundle.instructors.get("instructor4");
        submissionParams = new String[]{
            Const.ParamsNames.COURSE_ID, instructor4.courseId
        };
        pageAction = getAction(addUserIdToParams(instructor4.googleId, submissionParams));
        pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_DETAILS+"?error=false&user=idOfInstructor4", pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        pageData = (InstructorCourseDetailsPageData)pageResult.data;
        assertEquals(1, pageData.instructors.size());

        assertEquals("idOfCourseNoEvals", pageData.courseDetails.course.id);
        assertEquals("Typical Course 3 with 0 Evals", pageData.courseDetails.course.name);
        assertEquals(0, pageData.courseDetails.stats.teamsTotal);
        assertEquals(0, pageData.courseDetails.stats.studentsTotal);
        assertEquals(0, pageData.courseDetails.stats.unregisteredTotal);
        assertEquals(0, pageData.courseDetails.evaluations.size());
        assertEquals(0, pageData.courseDetails.feedbackSessions.size());

        expectedLogMessage = "TEAMMATESLOG|||instructorCourseDetailsPage|||instructorCourseDetailsPage|||"
        + "true|||Instructor(M)|||Instructor 4 of CourseNoEvals|||idOfInstructor4|||instr4@coursenoevals.com|||"
        + "instructorCourseDetails Page Load<br>Viewing Course Details for Course <span class=\"bold\">[idOfCourseNoEvals]</span>|||"
        + "/page/instructorCourseDetailsPage";
        assertEquals(expectedLogMessage, pageAction.getLogMessage());
    }

    private InstructorCourseDetailsPageAction getAction(String... params) throws Exception{
            return (InstructorCourseDetailsPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}

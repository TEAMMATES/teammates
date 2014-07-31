package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.test.util.Priority;
import teammates.ui.controller.InstructorCommentsPageAction;
import teammates.ui.controller.InstructorCommentsPageData;
import teammates.ui.controller.ShowPageResult;

@Priority(-1)
public class InstructorCommentsPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        String[] submissionParams = new String[]{};
        
        ______TS("instructor with no courses");
        gaeSimulation.loginAsInstructor(dataBundle.accounts.get("instructorWithoutCourses").googleId);
        InstructorCommentsPageAction action = getAction(submissionParams);
        ShowPageResult result = (ShowPageResult) action.executeAndPostProcess();
        
        AssertHelper.assertContainsRegex(Const.ViewURIs.INSTRUCTOR_COMMENTS, result.getDestinationWithParams());
        assertEquals("TEAMMATESLOG|||instructorCommentsPage|||instructorCommentsPage|||true|||Instructor"
                + "|||Instructor Without Courses|||instructorWithoutCourses|||iwc@yahoo.com|||"
                + "instructorComments Page Load<br>Viewing <span class=\"bold\">instructorWithoutCourses's"
                + "</span> comment records for Course <span class=\"bold\">[]</span>|||/page/instructorCommentsPage", 
                action.getLogMessage());

        InstructorCommentsPageData data = (InstructorCommentsPageData) result.data;
        assertEquals("", data.courseName);
        assertEquals(0, data.coursePaginationList.size());
        assertEquals("javascript:;", data.previousPageLink);
        assertEquals("javascript:;", data.nextPageLink);
        
        ______TS("instructor with courses and comments");
        AccountAttributes instructorWithCoursesAndComments = dataBundle.accounts.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructorWithCoursesAndComments.googleId);
        action = getAction(submissionParams);
        result = (ShowPageResult) action.executeAndPostProcess();
        
        AssertHelper.assertContainsRegex(Const.ViewURIs.INSTRUCTOR_COMMENTS, result.getDestinationWithParams());
        assertEquals("TEAMMATESLOG|||instructorCommentsPage|||instructorCommentsPage|||true|||Instructor"
                + "|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                + "instructorComments Page Load<br>Viewing <span class=\"bold\">idOfInstructor1OfCourse1's"
                + "</span> comment records for Course <span class=\"bold\">[idOfTypicalCourse1]</span>|||/page/instructorCommentsPage", 
                action.getLogMessage());
        
        data = (InstructorCommentsPageData) result.data;
        assertEquals("idOfTypicalCourse1 : Typical Course 1 with 2 Evals", data.courseName);
        assertEquals(1, data.coursePaginationList.size());
        assertEquals("javascript:;", data.previousPageLink);
        assertEquals("javascript:;", data.nextPageLink);
        assertEquals(2, data.comments.get(InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST).size());
        
        ______TS("instructor with courses but without comments");
        gaeSimulation.loginAsInstructor(dataBundle.accounts.get("instructor2OfCourse1").googleId);
        action = getAction(submissionParams);
        result = (ShowPageResult) action.executeAndPostProcess();
        
        AssertHelper.assertContainsRegex(Const.ViewURIs.INSTRUCTOR_COMMENTS, result.getDestinationWithParams());
        assertEquals("TEAMMATESLOG|||instructorCommentsPage|||instructorCommentsPage|||true|||Instructor"
                + "|||Instructor 2 of Course 1|||idOfInstructor2OfCourse1|||instr2@course1.com|||"
                + "instructorComments Page Load<br>Viewing <span class=\"bold\">idOfInstructor2OfCourse1's"
                + "</span> comment records for Course <span class=\"bold\">[idOfTypicalCourse1]</span>|||/page/instructorCommentsPage", 
                action.getLogMessage());
        
        data = (InstructorCommentsPageData) result.data;
        assertEquals("idOfTypicalCourse1 : Typical Course 1 with 2 Evals", data.courseName);
        assertEquals(1, data.coursePaginationList.size());
        assertEquals("javascript:;", data.previousPageLink);
        assertEquals("javascript:;", data.nextPageLink);
        assertEquals(0, data.comments.size());
    }
    
    private InstructorCommentsPageAction getAction(String... params) throws Exception{
        return (InstructorCommentsPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}

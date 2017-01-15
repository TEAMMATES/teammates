package teammates.test.cases.ui;

import java.util.ArrayList;

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
import teammates.ui.template.CoursePagination;

@Priority(-1)
public class InstructorCommentsPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() {
        String[] submissionParams = new String[]{};
        
        ______TS("instructor with no courses");
        gaeSimulation.loginAsInstructor(dataBundle.accounts.get("instructorWithoutCourses").googleId);
        InstructorCommentsPageAction action = getAction(submissionParams);
        ShowPageResult result = (ShowPageResult) action.executeAndPostProcess();
        
        AssertHelper.assertContainsRegex(Const.ViewURIs.INSTRUCTOR_COMMENTS, result.getDestinationWithParams());
        AssertHelper.assertLogMessageEquals(
                "TEAMMATESLOG|||instructorCommentsPage|||instructorCommentsPage|||true|||Instructor"
                        + "|||Instructor Without Courses|||instructorWithoutCourses|||iwc@yahoo.tmt|||"
                        + "instructorComments Page Load<br>Viewing <span class=\"bold\">instructorWithoutCourses's"
                        + "</span> comment records for Course <span class=\"bold\">[]</span>|||"
                        + "/page/instructorCommentsPage",
                action.getLogMessage());

        InstructorCommentsPageData data = (InstructorCommentsPageData) result.data;
        assertEquals("", data.getCourseName());
        CoursePagination actualCoursePagination = data.getCoursePagination();
        assertEquals("javascript:;", actualCoursePagination.getPreviousPageLink());
        assertEquals("javascript:;",
                     actualCoursePagination.getNextPageLink());
        assertEquals(new ArrayList<String>(), actualCoursePagination.getCoursePaginationList());
        assertEquals("", actualCoursePagination.getActiveCourse());
        assertEquals("active", actualCoursePagination.getActiveCourseClass());
        assertEquals(data.getInstructorCommentsLink(), actualCoursePagination.getUserCommentsLink());
        
        ______TS("instructor with courses and comments");
        AccountAttributes instructorWithCoursesAndComments = dataBundle.accounts.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructorWithCoursesAndComments.googleId);
        action = getAction(submissionParams);
        result = (ShowPageResult) action.executeAndPostProcess();
        
        AssertHelper.assertContainsRegex(Const.ViewURIs.INSTRUCTOR_COMMENTS, result.getDestinationWithParams());
        AssertHelper.assertLogMessageEquals(
                "TEAMMATESLOG|||instructorCommentsPage|||instructorCommentsPage|||true|||Instructor"
                        + "|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                        + "instructorComments Page Load<br>Viewing <span class=\"bold\">idOfInstructor1OfCourse1's"
                        + "</span> comment records for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + "|||/page/instructorCommentsPage",
                action.getLogMessage());
        
        data = (InstructorCommentsPageData) result.data;
        assertEquals("idOfTypicalCourse1 : Typical Course 1 with 2 Evals", data.getCourseName());
        actualCoursePagination = data.getCoursePagination();
        assertEquals("javascript:;", actualCoursePagination.getPreviousPageLink());
        assertEquals("javascript:;", actualCoursePagination.getNextPageLink());
        assertEquals(1, actualCoursePagination.getCoursePaginationList().size());
        assertEquals("idOfTypicalCourse1", actualCoursePagination.getActiveCourse());
        assertEquals("active", actualCoursePagination.getActiveCourseClass());
        assertEquals(data.getInstructorCommentsLink(), actualCoursePagination.getUserCommentsLink());
        assertEquals(1, data.getCommentsForStudentsTables().size());
        assertEquals(5, data.getCommentsForStudentsTables().get(0).getRows().size());
        
        
        ______TS("instructor with courses but without comments");
        gaeSimulation.loginAsInstructor(dataBundle.accounts.get("instructor2OfCourse1").googleId);
        action = getAction(submissionParams);
        result = (ShowPageResult) action.executeAndPostProcess();
        
        AssertHelper.assertContainsRegex(Const.ViewURIs.INSTRUCTOR_COMMENTS, result.getDestinationWithParams());
        AssertHelper.assertLogMessageEquals(
                "TEAMMATESLOG|||instructorCommentsPage|||instructorCommentsPage|||true|||Instructor"
                        + "|||Instructor 2 of Course 1|||idOfInstructor2OfCourse1|||instr2@course1.tmt|||"
                        + "instructorComments Page Load<br>Viewing <span class=\"bold\">idOfInstructor2OfCourse1's"
                        + "</span> comment records for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + "|||/page/instructorCommentsPage",
                action.getLogMessage());
        
        data = (InstructorCommentsPageData) result.data;
        assertEquals("idOfTypicalCourse1 : Typical Course 1 with 2 Evals", data.getCourseName());
        actualCoursePagination = data.getCoursePagination();
        assertEquals("javascript:;", actualCoursePagination.getPreviousPageLink());
        assertEquals("javascript:;", actualCoursePagination.getNextPageLink());
        assertEquals(1, actualCoursePagination.getCoursePaginationList().size());
        assertEquals("idOfTypicalCourse1", actualCoursePagination.getActiveCourse());
        assertEquals("active", actualCoursePagination.getActiveCourseClass());
        assertEquals(data.getInstructorCommentsLink(), actualCoursePagination.getUserCommentsLink());
        assertEquals(0, data.getCommentsForStudentsTables().size());
    }
    
    private InstructorCommentsPageAction getAction(String... params) {
        return (InstructorCommentsPageAction) gaeSimulation.getActionObject(uri, params);
    }
}

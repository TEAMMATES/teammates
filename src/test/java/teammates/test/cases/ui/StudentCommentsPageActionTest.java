package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.test.util.Priority;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentCommentsPageAction;
import teammates.ui.controller.StudentCommentsPageData;

// Priority added due to conflict between InstructorStudentListPageActionTest,
// StudentHomePageActionTest, and StudentCommentsPageActionTest.
@Priority(-1)
public class StudentCommentsPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.STUDENT_COMMENTS_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() {
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String studentId = student1InCourse1.googleId;
        String adminUserId = "admin.user";
        
        String[] submissionParams = new String[]{};
        
        ______TS("registered student with comment");
        
        gaeSimulation.loginUser(studentId);
        StudentCommentsPageAction action = getAction(submissionParams);
        ShowPageResult result = (ShowPageResult) action.executeAndPostProcess();
        AssertHelper.assertContainsRegex(Const.ViewURIs.STUDENT_COMMENTS, result.getDestinationWithParams());
        assertFalse(result.isError);
        
        StudentCommentsPageData data = (StudentCommentsPageData) result.data;
        assertEquals(2, data.getCommentsForStudentsTables().size());
        assertEquals(1, data.getCoursePagination().getCoursePaginationList().size());
        
        String expectedLogMessage = "TEAMMATESLOG|||studentCommentsPage|||studentCommentsPage|||true|||Student"
                + "|||Student 1 in course 1|||student1InCourse1|||student1InCourse1@gmail.tmt|||studentComments "
                + "Page Load<br>Viewing <span class=\"bold\">student1InCourse1's</span> comment records for Course "
                        + "<span class=\"bold\">[idOfTypicalCourse1]</span>|||/page/studentCommentsPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        
        ______TS("registered student without comment, masquerade mode");
        
        gaeSimulation.loginAsAdmin(adminUserId);
        studentId = dataBundle.students.get("student2InCourse2").googleId;
        
        action = getAction(addUserIdToParams(studentId, submissionParams));
        result = (ShowPageResult) action.executeAndPostProcess();
        AssertHelper.assertContainsRegex(Const.ViewURIs.STUDENT_COMMENTS, result.getDestinationWithParams());
        assertFalse(result.isError);
        
        data = (StudentCommentsPageData) result.data;
        assertEquals(0, data.getCommentsForStudentsTables().size());
        assertEquals(2, data.getCoursePagination().getCoursePaginationList().size());
        
        expectedLogMessage = "TEAMMATESLOG|||studentCommentsPage|||studentCommentsPage|||true|||Student(M)|||"
                + "Student in two courses|||student2InCourse1|||student2InCourse1@gmail.tmt|||studentComments "
                + "Page Load<br>Viewing <span class=\"bold\">student2InCourse1's</span> comment records for "
                + "Course <span class=\"bold\">[idOfTypicalCourse2]</span>|||/page/studentCommentsPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }
    
    private StudentCommentsPageAction getAction(String... params) {
        return (StudentCommentsPageAction) gaeSimulation.getActionObject(uri, params);
    }
}

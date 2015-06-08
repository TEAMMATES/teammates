package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentFeedbackQuestionSubmissionEditPageAction;

public class StudentFeedbackQuestionSubmissionEditPageActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionsDb
                .getFeedbackQuestion(session1InCourse1.feedbackSessionName, session1InCourse1.courseId, 1);

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("not enough parameters");

        verifyAssumptionFailure();

        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName
        };

        verifyAssumptionFailure(submissionParams);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID,
                feedbackQuestion.getId()
        };

        verifyAssumptionFailure(submissionParams);

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID,
                feedbackQuestion.getId()
        };

        verifyAssumptionFailure(submissionParams);

        ______TS("redirect unregistered user to home ");

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID,
                feedbackQuestion.getId()
        };

        String unregUserId = "unreg.user";
        gaeSimulation.loginUser(unregUserId);
        StudentFeedbackQuestionSubmissionEditPageAction pageAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(pageAction);

        assertTrue(redirectResult.isError);
        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE, redirectResult.destination);
        assertEquals("You are not registered in the course idOfTypicalCourse1", redirectResult.getStatusMessage());

        gaeSimulation.logoutUser();

        ______TS("typical case");

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        pageAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT, pageResult.destination);
        assertFalse(pageResult.isError);

        ______TS("masquerade mode");

        gaeSimulation.loginAsAdmin("admin.user");

        feedbackQuestion = feedbackQuestionsDb
                .getFeedbackQuestion(session1InCourse1.feedbackSessionName, session1InCourse1.courseId, 1);

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session1InCourse1.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME,
                session1InCourse1.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID,
                feedbackQuestion.getId(),
                Const.ParamsNames.USER_ID, student1InCourse1.googleId
        };

        pageAction = getAction(submissionParams);
        pageResult = getShowPageResult(pageAction);

        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT, pageResult.destination);
        assertFalse(pageResult.isError);
    }

    private StudentFeedbackQuestionSubmissionEditPageAction getAction(String... params) throws Exception {
        return (StudentFeedbackQuestionSubmissionEditPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}

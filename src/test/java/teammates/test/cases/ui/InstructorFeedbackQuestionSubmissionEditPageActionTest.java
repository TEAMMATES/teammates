package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.ui.controller.InstructorFeedbackQuestionSubmissionEditPageAction;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackQuestionSubmissionEditPageActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");

        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes q = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 3);

        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("not enough parameters");

        verifyAssumptionFailure();

        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };

        verifyAssumptionFailure(submissionParams);

        ______TS("typical case");

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, q.getId()
        };

        InstructorFeedbackQuestionSubmissionEditPageAction a = getAction(submissionParams);
        ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);

        ______TS("trying to access questions not meant for the user");

        q = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, q.getId()
        };

        a = getAction(submissionParams);

        try {
            r = (ShowPageResult) a.executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Trying to access a question not meant for the user." , e.getMessage());
        }

        ______TS("masquerade mode");

        gaeSimulation.loginAsAdmin("admin.user");

        q = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 3);

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, q.getId(),
                Const.ParamsNames.USER_ID, instructor.googleId
        };

        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);

        ______TS("Closed session");

        instructor = dataBundle.instructors.get("instructor1OfCourse1");
        fs = dataBundle.feedbackSessions.get("closedSession");

        gaeSimulation.loginAsInstructor(instructor.googleId);

        q = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, q.getId()
        };

        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);

        ______TS("Private session");

        instructor = dataBundle.instructors.get("instructor1OfCourse2");
        fs = dataBundle.feedbackSessions.get("session1InCourse2");

        gaeSimulation.loginAsInstructor(instructor.googleId);

        q = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, q.getId()
        };

        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);
    }

    private InstructorFeedbackQuestionSubmissionEditPageAction getAction(String... params) throws Exception {
        return (InstructorFeedbackQuestionSubmissionEditPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}

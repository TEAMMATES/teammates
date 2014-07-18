package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackResponseCommentAddAction;
import teammates.ui.controller.InstructorFeedbackResponseCommentAjaxPageData;

public class InstructorFeedbackResponseCommentAddActionTest extends
        BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeTypicalDataInDatastore();
		restoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD;
    }
    
    @Test
    public void testExcecuteAndPostProcess() throws Exception {
        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();

        FeedbackSessionAttributes session = dataBundle.feedbackSessions
                .get("session1InCourse1");
        
        int questionNumber = 1;
        FeedbackQuestionAttributes question = feedbackQuestionsDb.getFeedbackQuestion(
                session.feedbackSessionName, session.courseId, questionNumber);
        
        String giverEmail = "student1InCourse1@gmail.com";
        String receiverEmail = "student1InCourse1@gmail.com";
        FeedbackResponseAttributes response = feedbackResponsesDb.getFeedbackResponse(question.getId(),
                giverEmail, receiverEmail);
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        ______TS("Unsuccessful case: not enough parameters");
        
        verifyAssumptionFailure();
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response"
        };
        
        verifyAssumptionFailure(submissionParams);
        
        ______TS("typical successful case");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };
        
        InstructorFeedbackResponseCommentAddAction action = getAction(submissionParams);
        AjaxResult result = (AjaxResult) action.executeAndPostProcess();
        InstructorFeedbackResponseCommentAjaxPageData data = 
                (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        
        ______TS("Unsuccessful case: empty comment text");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getId(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };
        
        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        assertEquals("", result.getStatusMessage());
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        assertTrue(data.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, data.errorMessage);
    }
    
    private InstructorFeedbackResponseCommentAddAction getAction(String... params) throws Exception {
        return (InstructorFeedbackResponseCommentAddAction) (gaeSimulation.getActionObject(uri, params));
    }
}

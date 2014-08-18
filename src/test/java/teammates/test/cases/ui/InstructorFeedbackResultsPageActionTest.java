package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.InstructorFeedbackResultsPageAction;
import teammates.ui.controller.InstructorFeedbackResultsPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackResultsPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session2InCourse1");
        String[] paramsWithoutSortType = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName
        };
        String[] paramsWithSortTypeQuestion = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question"
        };
        String[] paramsWithSortTypeGiverRecipientQuestion = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "giver-recipient-question"
        };
        String[] paramsWithSortTypeRecipientGiverQuestion = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-giver-question"
        };
        String[] paramsWithSortTypeGiverQuestionRecipient = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "giver-question-recipient"
        };
        String[] paramsWithSortTypeRecipientQuestionGiver = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-question-giver"
        };
        
        String[] paramsWithSortTypeUndefined = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "undefined"
        };
        
        ______TS("Unsuccessful Case 1: no params");
        
        this.verifyAssumptionFailure();
        this.verifyAssumptionFailure(new String[]{
            Const.ParamsNames.COURSE_ID, session.courseId
        });
        
        ______TS("Successful Case 1: no sortType param");
        
        InstructorFeedbackResultsPageAction action = getAction(paramsWithoutSortType);
        ActionResult result = action.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION +
                "?error=false&user=idOfInstructor1OfCourse1",
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Successful Case 2: sortType question");
        
        action = getAction(paramsWithSortTypeQuestion);
        result = action.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION +
                "?error=false&user=idOfInstructor1OfCourse1",
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Successful Case 3: sortType giver-recipient-question");
        
        action = getAction(paramsWithSortTypeGiverRecipientQuestion);
        result = action.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_RECIPIENT_QUESTION +
                "?error=false&user=idOfInstructor1OfCourse1",
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Successful Case 4: sortType recipient-giver-question");
        
        action = getAction(paramsWithSortTypeRecipientGiverQuestion);
        result = action.executeAndPostProcess();
        
        ______TS("Successful Case 5: sortType giver-question-recipient");
        
        action = getAction(paramsWithSortTypeGiverQuestionRecipient);
        result = action.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_QUESTION_RECIPIENT +
                "?error=false&user=idOfInstructor1OfCourse1",
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Successful Case 6: sortType recipient-question-giver");
        
        action = getAction(paramsWithSortTypeRecipientQuestionGiver);
        result = action.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_QUESTION_GIVER +
                "?error=false&user=idOfInstructor1OfCourse1",
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Successful Case 7: sortType undefined");
        
        action = getAction(paramsWithSortTypeUndefined);
        result = action.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION +
                "?error=false&user=idOfInstructor1OfCourse1",
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Successful Case: filtering of feedbackResponses for access control");
        // accessControl--filtering of the result is tested in FeedbackSessionsLogicTest, 
        // so the test here about filtering is not rigorous
        gaeSimulation.loginAsInstructor(dataBundle.accounts.get("helperOfCourse1").googleId);
        action = getAction(paramsWithSortTypeQuestion);
        result = action.executeAndPostProcess();
        ShowPageResult pageResult = (ShowPageResult)result;
        InstructorFeedbackResultsPageData pageData = (InstructorFeedbackResultsPageData)pageResult.data;
        assertEquals(true, pageData.bundle.responses.isEmpty());
    }
    
    private InstructorFeedbackResultsPageAction getAction(String[] params){
        return (InstructorFeedbackResultsPageAction) gaeSimulation.getActionObject(uri, params);
    }

}

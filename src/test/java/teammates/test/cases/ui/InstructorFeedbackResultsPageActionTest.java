package teammates.test.cases.ui;

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
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
    }

    @Test
    public void testExecuteAndPostProcess() {
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session2InCourse1");
        String[] paramsWithoutSortType = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName()
        };
        String[] paramsWithSortTypeQuestion = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question"
        };
        String[] paramsWithSortTypeGiverRecipientQuestion = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "giver-recipient-question"
        };
        String[] paramsWithSortTypeRecipientGiverQuestion = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-giver-question"
        };
        String[] paramsWithSortTypeGiverQuestionRecipient = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "giver-question-recipient"
        };
        String[] paramsWithSortTypeRecipientQuestionGiver = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-question-giver"
        };
        String[] paramsWithSortTypeUndefined = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "undefined"
        };
        String[] paramsNeedAjax = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "undefined",
                Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX, "true"
        };
        String[] paramsWithStartIndex = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "undefined",
                Const.ParamsNames.FEEDBACK_RESULTS_MAIN_INDEX, "1"
        };
        String[] paramsQuestionNumberOne = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1"
        };
        String[] paramsSectionOneByQuestion = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1"
        };
        String[] paramsSectionOneByGrq = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "giver-recipient-question",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1"
        };
        String[] paramsSectionOneByRgq = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-giver-question",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1"
        };
        String[] paramsNeedHtmlTableAllSections = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-giver-question",
                Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED, "true"
        };
        String[] paramsNeedHtmlTableSectionOne = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-giver-question",
                Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED, "true",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1"
        };
        
        ______TS("Failure case: no params");

        this.verifyAssumptionFailure();
        this.verifyAssumptionFailure(new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId()
        });

        ______TS("Typical case: no sortType param");

        InstructorFeedbackResultsPageAction action = getAction(paramsWithoutSortType);
        ActionResult result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: sortType question");

        action = getAction(paramsWithSortTypeQuestion);
        result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: sortType giver-recipient-question");

        action = getAction(paramsWithSortTypeGiverRecipientQuestion);
        result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_RECIPIENT_QUESTION
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: sortType recipient-giver-question");

        action = getAction(paramsWithSortTypeRecipientGiverQuestion);
        result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: sortType giver-question-recipient");

        action = getAction(paramsWithSortTypeGiverQuestionRecipient);
        result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_QUESTION_RECIPIENT
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Typical case: sortType recipient-question-giver");
        
        action = getAction(paramsWithSortTypeRecipientQuestionGiver);
        result = action.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_QUESTION_GIVER
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: sortType undefined");

        action = getAction(paramsWithSortTypeUndefined);
        result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: feedback result with start index");
        action = getAction(paramsWithStartIndex);
        result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Typical case: feedback result needing ajax");
        action = getAction(paramsNeedAjax);
        result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Typical case: specific question number");
        action = getAction(paramsQuestionNumberOne);
        result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Typical case: view section 1 sortType question");
        action = getAction(paramsSectionOneByQuestion);
        result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Typical case: view section 1 sortType GRQ");
        action = getAction(paramsSectionOneByGrq);
        result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_RECIPIENT_QUESTION
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Typical case: view section 1 sortType RGQ");
        action = getAction(paramsSectionOneByRgq);
        result = action.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION
                     + "?error=false&user=idOfInstructor1OfCourse1",
                     result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Typical case: view HTML table all sections");
        action = getAction(paramsNeedHtmlTableAllSections);
        result = action.executeAndPostProcess();

        assertEquals("?error=false&user=idOfInstructor1OfCourse1", result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Typical case: view HTML table section 1");
        action = getAction(paramsNeedHtmlTableSectionOne);
        result = action.executeAndPostProcess();

        assertEquals("?error=false&user=idOfInstructor1OfCourse1", result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Typical case: filtering of feedbackResponses for access control");
        // accessControl--filtering of the result is tested in FeedbackSessionsLogicTest,
        // so the test here about filtering is not rigorous
        gaeSimulation.loginAsInstructor(dataBundle.accounts.get("helperOfCourse1").googleId);
        action = getAction(paramsWithSortTypeQuestion);
        result = action.executeAndPostProcess();
        ShowPageResult pageResult = (ShowPageResult) result;
        InstructorFeedbackResultsPageData pageData = (InstructorFeedbackResultsPageData) pageResult.data;
        assertTrue(pageData.getBundle().responses.isEmpty());
        
    }

    private InstructorFeedbackResultsPageAction getAction(String[] params) {
        return (InstructorFeedbackResultsPageAction) gaeSimulation.getActionObject(uri, params);
    }

}

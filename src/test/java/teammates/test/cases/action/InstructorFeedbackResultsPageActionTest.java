package teammates.test.cases.action;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackResultsPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorFeedbackResultsPageData;

/**
 * SUT: {@link InstructorFeedbackResultsPageAction}.
 */
public class InstructorFeedbackResultsPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
    }

    @Override
    protected void prepareTestData() {
        // see setup()
    }

    @BeforeMethod
    public void setup() {
        removeAndRestoreTypicalDataBundle();
    }

    @Test(expectedExceptions = UnauthorizedAccessException.class,
            expectedExceptionsMessageRegExp = "Trying to access system using a non-existent feedback session entity")
    public void testExecuteAndPostProcess_accessDeletedSession_shouldNotAccess() throws Exception {
        gaeSimulation.loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session2InCourse1");

        FeedbackSessionsLogic.inst()
                .moveFeedbackSessionToRecycleBin(session.getFeedbackSessionName(), session.getCourseId());

        String[] param = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question",
        };

        InstructorFeedbackResultsPageAction action = getAction(param);
        getShowPageResult(action);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        gaeSimulation.loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session2InCourse1");
        String[] paramsWithoutSortType = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        String[] paramsWithSortTypeQuestion = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question",
        };
        String[] paramsWithSortTypeGiverRecipientQuestion = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "giver-recipient-question",
        };
        String[] paramsWithSortTypeRecipientGiverQuestion = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-giver-question",
        };
        String[] paramsWithSortTypeGiverQuestionRecipient = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "giver-question-recipient",
        };
        String[] paramsWithSortTypeRecipientQuestionGiver = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-question-giver",
        };
        String[] paramsWithSortTypeUndefined = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "undefined",
        };
        String[] paramsNeedAjax = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "undefined",
                Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX, "true",
        };
        String[] paramsWithStartIndex = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "undefined",
                Const.ParamsNames.FEEDBACK_RESULTS_MAIN_INDEX, "1",
        };
        String[] paramsQuestionNumberOne = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
        };
        // either will be chosen by default when a section is selected
        String[] paramsEitherSectionOneByQuestion = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTIONDETAIL, "EITHER",
        };
        String[] paramsFromSectionOneByQuestion = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTIONDETAIL, "GIVER",
        };
        String[] paramsToSectionOneByQuestion = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTIONDETAIL, "EVALUEE",
        };
        String[] paramsBothSectionOneByQuestion = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTIONDETAIL, "BOTH",
        };
        String[] paramsSectionOneByGrq = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "giver-recipient-question",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1",
        };
        String[] paramsSectionOneByRgq = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-giver-question",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1",
        };
        String[] paramsNeedHtmlTableAllSections = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-giver-question",
                Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED, "true",
        };
        String[] paramsNeedHtmlTableSectionOne = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient-giver-question",
                Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED, "true",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTIONDETAIL, "EITHER",
        };
        String[] paramsWithInvalidSectionDetail = {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "question",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION, "Section+1",
                Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTIONDETAIL, "ALL",
        };

        ______TS("Failure case: no params");

        this.verifyAssumptionFailure();
        this.verifyAssumptionFailure(new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
        });

        ______TS("Failure case: params with invalid feedback section detail");

        this.verifyAssumptionFailure(paramsWithInvalidSectionDetail);

        ______TS("Typical case: no sortType param");

        InstructorFeedbackResultsPageAction action = getAction(paramsWithoutSortType);
        ShowPageResult result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: sortType question");

        action = getAction(paramsWithSortTypeQuestion);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: sortType giver-recipient-question");

        action = getAction(paramsWithSortTypeGiverRecipientQuestion);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_RECIPIENT_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: sortType recipient-giver-question");

        action = getAction(paramsWithSortTypeRecipientGiverQuestion);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: sortType giver-question-recipient");

        action = getAction(paramsWithSortTypeGiverQuestionRecipient);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_QUESTION_RECIPIENT,
                        false, "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: sortType recipient-question-giver");

        action = getAction(paramsWithSortTypeRecipientQuestionGiver);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_QUESTION_GIVER,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: sortType undefined");

        action = getAction(paramsWithSortTypeUndefined);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: feedback result with start index");
        action = getAction(paramsWithStartIndex);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: feedback result needing ajax");
        action = getAction(paramsNeedAjax);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: specific question number");
        action = getAction(paramsQuestionNumberOne);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: view default / either recipient or giver in section 1 sortType question");
        action = getAction(paramsEitherSectionOneByQuestion);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: view giver from section 1 sortType question");
        action = getAction(paramsFromSectionOneByQuestion);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: view recipient to section 1 sortType question");
        action = getAction(paramsToSectionOneByQuestion);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: view both recipient and section from section 1 sortType question");
        action = getAction(paramsBothSectionOneByQuestion);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: view section 1 sortType GRQ");
        action = getAction(paramsSectionOneByGrq);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_RECIPIENT_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: view section 1 sortType RGQ");
        action = getAction(paramsSectionOneByRgq);
        result = getShowPageResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION,
                        false,
                        "idOfInstructor1OfCourse1"),
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);

        ______TS("Typical case: view HTML table all sections");
        action = getAction(paramsNeedHtmlTableAllSections);
        AjaxResult ajaxResult = getAjaxResult(action);

        assertEquals(
                getPageResultDestination("", false, "idOfInstructor1OfCourse1"),
                ajaxResult.getDestinationWithParams());
        assertEquals("", ajaxResult.getStatusMessage());
        assertFalse(ajaxResult.isError);

        ______TS("Typical case: view HTML table either giver or recipient in section 1");
        action = getAction(paramsNeedHtmlTableSectionOne);
        ajaxResult = getAjaxResult(action);

        assertEquals(
                getPageResultDestination("", false, "idOfInstructor1OfCourse1"),
                ajaxResult.getDestinationWithParams());
        assertEquals("", ajaxResult.getStatusMessage());
        assertFalse(ajaxResult.isError);

        ______TS("Typical case: filtering of feedbackResponses for access control");
        // accessControl--filtering of the result is tested in FeedbackSessionsLogicTest,
        // so the test here about filtering is not rigorous
        gaeSimulation.loginAsInstructor(typicalBundle.accounts.get("helperOfCourse1").googleId);
        action = getAction(paramsWithSortTypeQuestion);
        result = getShowPageResult(action);
        InstructorFeedbackResultsPageData pageData = (InstructorFeedbackResultsPageData) result.data;
        assertTrue(pageData.getBundle().responses.isEmpty());

    }

    @Override
    protected InstructorFeedbackResultsPageAction getAction(String... params) {
        return (InstructorFeedbackResultsPageAction) gaeSimulation.getLegacyActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}

package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.ui.output.CommentSearchResultsData;

/**
 * SUT:{@link SearchCommentsAction}.
 */
public class SearchCommentsActionTest extends BaseActionTest<SearchCommentsAction> {

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
        putDocuments(dataBundle);
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_COMMENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() {
        // See test cases below
    }

    @Test
    protected void execute_notEnoughParameters_shouldFail() {
        loginAsInstructor("idOfInstructor1OfCourse1");
        verifyHttpParameterFailure();
    }

    @Test
    protected void execute_searchNoMatch_shouldBeEmpty() {
        loginAsInstructor("idOfInstructor1OfCourse1");
        String[] searchParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "123",
        };
        SearchCommentsAction action = getAction(searchParams);
        JsonResult result = getJsonResult(action);
        CommentSearchResultsData data = (CommentSearchResultsData) result.getOutput();
        assertEquals(0, data.getSearchResults().size());
    }

    @Test
    protected void execute_searchHitComment_shouldSucceed() {
        loginAsInstructor("idOfInstructor1OfCourse1");
        String[] searchParams = new String[] {
                Const.ParamsNames.SEARCH_KEY,
                typicalBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1").commentText,
        };
        SearchCommentsAction action = getAction(searchParams);
        JsonResult result = getJsonResult(action);
        CommentSearchResultsData data = (CommentSearchResultsData) result.getOutput();
        assertEquals(1, data.getSearchResults().size());
    }

    @Test
    protected void execute_searchHitResponse_shouldSucceed() {
        loginAsInstructor("idOfInstructor1OfCourse1");
        String[] searchParams = new String[] {
                Const.ParamsNames.SEARCH_KEY,
                typicalBundle.feedbackResponses.get("response1ForQ1S1C1").responseDetails.getAnswerString(),
        };
        SearchCommentsAction action = getAction(searchParams);
        JsonResult result = getJsonResult(action);
        CommentSearchResultsData data = (CommentSearchResultsData) result.getOutput();
        assertEquals(1, data.getSearchResults().size());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyInstructorsCanAccess();
    }
}

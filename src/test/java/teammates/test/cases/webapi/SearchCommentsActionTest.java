package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.ui.webapi.action.SearchCommentsAction;

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
        // TODO
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAccessibleForAdmin();
        verifyOnlyInstructorsCanAccess();
    }
}

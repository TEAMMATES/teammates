package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.test.TestProperties;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link SearchInstructorsAction}.
 */
public class SearchInstructorsNoServiceActionTest extends BaseActionTest<SearchInstructorsAction> {

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() {
        if (TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] params = new String[] {
                Const.ParamsNames.SEARCH_KEY, "anything",
        };
        SearchInstructorsAction a = getAction(params);
        JsonResult result = getJsonResult(a);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_NOT_IMPLEMENTED, result.getStatusCode());
        assertEquals("Search service is not implemented.", output.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}

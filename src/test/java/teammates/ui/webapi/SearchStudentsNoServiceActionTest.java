package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.test.TestProperties;
import teammates.ui.output.MessageOutput;

/**
 * SUT:{@link SearchStudentsAction}.
 */
public class SearchStudentsNoServiceActionTest extends BaseActionTest<SearchStudentsAction> {

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        if (TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsInstructor("idOfInstructor1OfCourse1");
        String[] params = new String[] {
                Const.ParamsNames.SEARCH_KEY, "anything",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        SearchStudentsAction a = getAction(params);
        JsonResult result = getJsonResult(a);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_NOT_IMPLEMENTED, result.getStatusCode());
        assertEquals("Search service is not implemented.", output.getMessage());

        loginAsAdmin();
        params = new String[] {
                Const.ParamsNames.SEARCH_KEY, "anything",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        a = getAction(params);
        result = getJsonResult(a);
        output = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_NOT_IMPLEMENTED, result.getStatusCode());
        assertEquals("Search service is not implemented.", output.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAccessibleForAdmin();
        verifyOnlyInstructorsCanAccess();
    }
}

package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.SearchSessionsAction;
import teammates.ui.webapi.output.SearchSessionsData;

/**
 * SUT: {@link SearchSessionsAction}.
 */
public class SearchSessionsActionTest extends BaseActionTest<SearchSessionsAction> {

    private final StudentAttributes acc = typicalBundle.students.get("student1InCourse1");

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
        putDocuments(dataBundle);
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSIONS_SEARCH;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        // See test cases below.
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        loginAsAdmin();
        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_searchCourse_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.ADMIN_SEARCH_KEY, acc.getCourse() };
        SearchSessionsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchSessionsData response = (SearchSessionsData) result.getOutput();
        assertTrue(response.getSessions().keySet().contains(acc.getEmail()));
    }

    @Test
    protected void testExecute_searchEmail_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.ADMIN_SEARCH_KEY, acc.getEmail() };
        SearchSessionsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchSessionsData response = (SearchSessionsData) result.getOutput();
        assertTrue(response.getSessions().keySet().contains(acc.getEmail()));
    }

    @Test
    protected void testExecute_searchGoogleId_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.ADMIN_SEARCH_KEY, acc.getGoogleId() };
        SearchSessionsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchSessionsData response = (SearchSessionsData) result.getOutput();
        assertTrue(response.getSessions().keySet().contains(acc.getEmail()));
    }

    @Test
    protected void testExecute_searchName_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.ADMIN_SEARCH_KEY, acc.getName() };
        SearchSessionsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchSessionsData response = (SearchSessionsData) result.getOutput();
        assertTrue(response.getSessions().keySet().contains(acc.getEmail()));
    }

    @Test
    protected void testExecute_searchSection_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.ADMIN_SEARCH_KEY, acc.getSection() };
        SearchSessionsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchSessionsData response = (SearchSessionsData) result.getOutput();
        assertTrue(response.getSessions().keySet().contains(acc.getEmail()));
    }

    @Test
    protected void testExecute_searchTeam_shouldSucceed() {
        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.ADMIN_SEARCH_KEY, acc.getTeam() };
        SearchSessionsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        SearchSessionsData response = (SearchSessionsData) result.getOutput();
        assertTrue(response.getSessions().keySet().contains(acc.getEmail()));
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}

package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const;
import teammates.test.TestProperties;
import teammates.ui.output.AccountRequestsData;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link SearchAccountRequestsAction}.
 */
public class SearchAccountRequestsActionTest extends BaseActionTest<SearchAccountRequestsAction> {

    private final AccountRequestAttributes accountRequest =
            typicalBundle.accountRequests.get("instructor1OfCourse1");

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
        putDocuments(dataBundle);
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_ACCOUNT_REQUESTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() {
        // See test cases below.
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        loginAsAdmin();
        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_searchEmail_shouldSucceed() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, accountRequest.getEmail() };
        SearchAccountRequestsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        AccountRequestsData response = (AccountRequestsData) result.getOutput();
        assertTrue(response.getAccountRequests().stream()
                .filter(i -> i.getName().equals(accountRequest.getName()))
                .findAny()
                .isPresent());
        assertTrue(response.getAccountRequests().get(0).getRegistrationKey() != null);
    }

    @Test
    protected void testExecute_searchInstitute_shouldSucceed() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, accountRequest.getInstitute() };
        SearchAccountRequestsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        AccountRequestsData response = (AccountRequestsData) result.getOutput();
        assertTrue(response.getAccountRequests().stream()
                .filter(i -> i.getName().equals(accountRequest.getName()))
                .findAny()
                .isPresent());
        assertTrue(response.getAccountRequests().get(0).getRegistrationKey() != null);
    }

    @Test
    protected void testExecute_searchName_shouldSucceed() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, accountRequest.getName() };
        SearchAccountRequestsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        AccountRequestsData response = (AccountRequestsData) result.getOutput();
        assertTrue(response.getAccountRequests().stream()
                .filter(i -> i.getName().equals(accountRequest.getName()))
                .findAny()
                .isPresent());
        assertTrue(response.getAccountRequests().get(0).getRegistrationKey() != null);
    }

    @Test
    protected void testExecute_searchNoMatch_shouldBeEmpty() {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, "noMatch" };
        SearchAccountRequestsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        AccountRequestsData response = (AccountRequestsData) result.getOutput();
        assertEquals(0, response.getAccountRequests().size());
    }

    @Test
    public void testExecute_noSearchService_shouldReturn501() {
        if (TestProperties.isSearchServiceActive()) {
            return;
        }

        loginAsAdmin();
        String[] params = new String[] {
                Const.ParamsNames.SEARCH_KEY, "anything",
        };
        SearchAccountRequestsAction a = getAction(params);
        JsonResult result = getJsonResult(a, HttpStatus.SC_NOT_IMPLEMENTED);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals("Full-text search is not available.", output.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}

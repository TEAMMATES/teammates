package teammates.it.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.test.TestProperties;
import teammates.ui.output.AccountRequestsData;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.SearchAccountRequestsAction;

/**
 * SUT: {@link SearchAccountRequestsAction}.
 */
public class SearchAccountRequestsActionIT extends BaseActionIT<SearchAccountRequestsAction> {

    @Override
    @Test
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        putDocuments(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_ACCOUNT_REQUESTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws InvalidParametersException, EntityAlreadyExistsException {
        if (!TestProperties.isSearchServiceActive()) {
            ______TS("Search with SearchService disabled");
            String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, "randomString123" };
            SearchAccountRequestsAction action = getAction(submissionParams);
            JsonResult result = getJsonResult(action, HttpStatus.SC_NOT_IMPLEMENTED);
            MessageOutput output = (MessageOutput) result.getOutput();
            assertEquals("Full-text search is not available.", output.getMessage());
            return;
        }
        AccountRequest accountRequest = typicalBundle.accountRequests.get("instructor1");

        loginAsAdmin();

        ______TS("Search via Email");
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, accountRequest.getEmail() };
        SearchAccountRequestsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action, 200);
        AccountRequestsData response = (AccountRequestsData) result.getOutput();
        assertTrue(response.getAccountRequests().stream()
                .filter(i -> i.getName().equals(accountRequest.getName()))
                .findAny()
                .isPresent());
        assertTrue(response.getAccountRequests().get(0).getRegistrationKey() != null);

        ______TS("Search via Institute");
        submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, accountRequest.getInstitute() };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountRequestsData) result.getOutput();
        assertTrue(response.getAccountRequests().stream()
                .filter(i -> i.getName().equals(accountRequest.getName()))
                .findAny()
                .isPresent());
        assertTrue(response.getAccountRequests().get(0).getRegistrationKey() != null);

        ______TS("Search via Name");
        submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, accountRequest.getName() };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountRequestsData) result.getOutput();
        assertTrue(response.getAccountRequests().stream()
                .filter(i -> i.getName().equals(accountRequest.getName()))
                .findAny()
                .isPresent());
        assertTrue(response.getAccountRequests().get(0).getRegistrationKey() != null);

        ______TS("Search Duplicate Name");
        submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, "Instructor" };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountRequestsData) result.getOutput();
        assertTrue(response.getAccountRequests().get(0).getRegistrationKey() != null);
        assertEquals(11, response.getAccountRequests().size());

        ______TS("Search result with 0 matches");

        submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, "randomString123" };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountRequestsData) result.getOutput();
        assertEquals(0, response.getAccountRequests().size());
    }
}

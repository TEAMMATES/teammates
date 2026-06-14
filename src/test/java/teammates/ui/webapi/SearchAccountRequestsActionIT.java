package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.test.GroupNames;
import teammates.ui.output.AccountRequestsData;

/**
 * SUT: {@link SearchAccountRequestsAction}.
 */
public class SearchAccountRequestsActionIT extends BaseActionIT<SearchAccountRequestsAction> {
    private DataBundle typicalBundle;

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_ACCOUNT_REQUESTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testExecute() throws InvalidParametersException, EntityAlreadyExistsException {
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
        assertNotNull(response.getAccountRequests().get(0).getAccountRequestId());

        ______TS("Search via Institute");
        submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, accountRequest.getInstitute().getName() };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountRequestsData) result.getOutput();
        assertTrue(response.getAccountRequests().stream()
                .filter(i -> i.getName().equals(accountRequest.getName()))
                .findAny()
                .isPresent());
        assertNotNull(response.getAccountRequests().get(0).getAccountRequestId());

        ______TS("Search via Name");
        submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, accountRequest.getName() };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountRequestsData) result.getOutput();
        assertTrue(response.getAccountRequests().stream()
                .filter(i -> i.getName().equals(accountRequest.getName()))
                .findAny()
                .isPresent());
        assertNotNull(response.getAccountRequests().get(0).getAccountRequestId());

        ______TS("Search Duplicate Name");
        submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, "Instructor" };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountRequestsData) result.getOutput();
        assertNotNull(response.getAccountRequests().get(0).getAccountRequestId());
        assertEquals(11, response.getAccountRequests().size());

        ______TS("Search result with 0 matches");

        submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, "randomString123" };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountRequestsData) result.getOutput();
        assertEquals(0, response.getAccountRequests().size());
    }
}

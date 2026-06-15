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
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Course;
import teammates.test.GroupNames;
import teammates.ui.output.AccountVerificationRequestsData;

/**
 * SUT: {@link SearchAccountVerificationRequestsAction}.
 */
public class SearchAccountVerificationRequestsActionIT extends BaseActionIT<SearchAccountVerificationRequestsAction> {
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
        return Const.ResourceURIs.SEARCH_ACCOUNT_VERIFICATION_REQUESTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testExecute() throws InvalidParametersException, EntityAlreadyExistsException {
        AccountVerificationRequest accountVerificationRequest = typicalBundle.accountVerificationRequests.get("instructor1");

        loginAsAdmin();

        ______TS("Search via Email");
        String[] submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, accountVerificationRequest.getEmail() };
        SearchAccountVerificationRequestsAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action, 200);
        AccountVerificationRequestsData response = (AccountVerificationRequestsData) result.getOutput();
        assertTrue(response.getAccountVerificationRequests().stream()
                .filter(i -> i.getName().equals(accountVerificationRequest.getName()))
                .findAny()
                .isPresent());
        assertNotNull(response.getAccountVerificationRequests().get(0)
                .getAccountVerificationRequestId());

        ______TS("Search via Institute");
        submissionParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, accountVerificationRequest.getInstitute().getName() };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountVerificationRequestsData) result.getOutput();
        assertTrue(response.getAccountVerificationRequests().stream()
                .filter(i -> i.getName().equals(accountVerificationRequest.getName()))
                .findAny()
                .isPresent());
        assertNotNull(response.getAccountVerificationRequests().get(0)
                .getAccountVerificationRequestId());

        ______TS("Search via Name");
        submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, accountVerificationRequest.getName() };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountVerificationRequestsData) result.getOutput();
        assertTrue(response.getAccountVerificationRequests().stream()
                .filter(i -> i.getName().equals(accountVerificationRequest.getName()))
                .findAny()
                .isPresent());
        assertNotNull(response.getAccountVerificationRequests().get(0)
                .getAccountVerificationRequestId());

        ______TS("Search Duplicate Name");
        submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, "Instructor" };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountVerificationRequestsData) result.getOutput();
        assertNotNull(response.getAccountVerificationRequests().get(0)
                .getAccountVerificationRequestId());
        assertEquals(11, response.getAccountVerificationRequests().size());

        ______TS("Search result with 0 matches");

        submissionParams = new String[] { Const.ParamsNames.SEARCH_KEY, "randomString123" };
        action = getAction(submissionParams);
        result = getJsonResult(action, 200);
        response = (AccountVerificationRequestsData) result.getOutput();
        assertEquals(0, response.getAccountVerificationRequests().size());
    }
}

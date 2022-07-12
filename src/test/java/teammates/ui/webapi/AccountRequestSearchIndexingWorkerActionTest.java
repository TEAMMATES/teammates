package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.TaskQueue;
import teammates.test.TestProperties;

/**
 * SUT: {@link AccountRequestSearchIndexingWorkerAction}.
 */
public class AccountRequestSearchIndexingWorkerActionTest
        extends BaseActionTest<AccountRequestSearchIndexingWorkerAction> {

    @Override
    protected String getActionUri() {
        return TaskQueue.ACCOUNT_REQUEST_SEARCH_INDEXING_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        AccountRequestAttributes accountRequest = typicalBundle.accountRequests.get("instructor1OfCourse1");

        ______TS("account request not yet indexed should not be searchable");

        List<AccountRequestAttributes> accountRequestsList =
                logic.searchAccountRequestsInWholeSystem(accountRequest.getEmail());
        assertEquals(0, accountRequestsList.size());

        ______TS("account request indexed should be searchable");

        String[] submissionParams = new String[] {
                ParamsNames.INSTRUCTOR_EMAIL, accountRequest.getEmail(),
                ParamsNames.INSTRUCTOR_INSTITUTE, accountRequest.getInstitute(),
        };

        AccountRequestSearchIndexingWorkerAction action = getAction(submissionParams);
        getJsonResult(action);

        accountRequestsList = logic.searchAccountRequestsInWholeSystem(accountRequest.getEmail());
        assertEquals(1, accountRequestsList.size());
        assertEquals(accountRequest.getName(), accountRequestsList.get(0).getName());
    }

    @Override
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }
}

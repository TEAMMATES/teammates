package teammates.it.ui.webapi;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.test.TestProperties;
import teammates.ui.webapi.AccountRequestSearchIndexingWorkerAction;

/**
 * SUT: {@link AccountRequestSearchIndexingWorkerAction}.
 */
public class AccountRequestSearchIndexingWorkerActionIT extends BaseActionIT<AccountRequestSearchIndexingWorkerAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.ACCOUNT_REQUEST_SEARCH_INDEXING_WORKER_URL;
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

        AccountRequest accountRequest = typicalBundle.accountRequests.get("instructor1");
        UUID accountRequestId = accountRequest.getId();

        ______TS("account request not yet indexed should not be searchable");

        List<AccountRequest> accountRequestsList =
                logic.searchAccountRequestsInWholeSystem(accountRequest.getEmail());
        assertEquals(0, accountRequestsList.size());

        ______TS("account request indexed should be searchable");

        String[] submissionParams = new String[] {
                ParamsNames.ACCOUNT_REQUEST_ID, accountRequestId.toString(),
        };

        AccountRequestSearchIndexingWorkerAction action = getAction(submissionParams);
        getJsonResult(action);

        accountRequestsList = logic.searchAccountRequestsInWholeSystem(accountRequest.getEmail());
        assertEquals(1, accountRequestsList.size());
        assertEquals(accountRequest.getName(), accountRequestsList.get(0).getName());
    }

    @Override
    @Test
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }

}

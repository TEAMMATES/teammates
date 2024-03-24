package teammates.it.ui.webapi;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.ui.output.AccountRequestData;
import teammates.ui.output.AccountRequestsData;
import teammates.ui.webapi.GetAccountRequestsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetAccountRequestsAction}.
 */
public class GetAccountRequestsActionIT extends BaseActionIT<GetAccountRequestsAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT_REQUESTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() {
        ______TS("No pending account requests initially");

        GetAccountRequestsAction action = getAction();
        JsonResult result = getJsonResult(action);
        AccountRequestsData data = (AccountRequestsData) result.getOutput();
        List<AccountRequestData> arData = data.getAccountRequests();

        assertEquals(0, arData.size());

        ______TS("Get 2 pending account requests, ignore 1 approved account request");
        AccountRequest approvedAccountRequest1 = typicalBundle.accountRequests.get("instructor2");
        approvedAccountRequest1.setStatus(AccountRequestStatus.APPROVED);

        AccountRequest accountRequest1 = typicalBundle.accountRequests.get("instructor1");
        AccountRequest accountRequest2 = typicalBundle.accountRequests.get("instructor1OfCourse2");
        accountRequest1.setStatus(AccountRequestStatus.PENDING);
        accountRequest2.setStatus(AccountRequestStatus.PENDING);

        action = getAction();
        result = getJsonResult(action);
        data = (AccountRequestsData) result.getOutput();
        arData = data.getAccountRequests();

        assertEquals(2, arData.size());

        // account request 1
        assertEquals(arData.get(0).getEmail(), accountRequest1.getEmail());
        assertEquals(arData.get(0).getInstitute(), accountRequest1.getInstitute());
        assertEquals(arData.get(0).getName(), accountRequest1.getName());
        assertEquals(arData.get(0).getRegistrationKey(), accountRequest1.getRegistrationKey());

        // account request 2
        assertEquals(arData.get(1).getEmail(), accountRequest2.getEmail());
        assertEquals(arData.get(1).getInstitute(), accountRequest2.getInstitute());
        assertEquals(arData.get(1).getName(), accountRequest2.getName());
        assertEquals(arData.get(1).getRegistrationKey(), accountRequest2.getRegistrationKey());
    }

    @Override
    @Test
    public void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }
}

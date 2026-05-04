package teammates.it.storage.api;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.it.test.BaseTestCaseWithDatabaseAccess;
import teammates.logic.entity.AccountRequest;
import teammates.storage.api.AccountRequestsDb;

/**
 * SUT: {@link AccountRequestsDb}.
 */
public class AccountRequestsDbIT extends BaseTestCaseWithDatabaseAccess {

    private final AccountRequestsDb accountRequestDb = AccountRequestsDb.inst();

    @Test
    public void testCreateReadDeleteAccountRequest() throws Exception {
        ______TS("Create account request, does not exists, succeeds");

        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        accountRequestDb.createAccountRequest(accountRequest);

        ______TS("Read account request using the given ID");

        AccountRequest actualAccReqEmalAndInstitute = accountRequestDb.getAccountRequest(accountRequest.getId());
        verifyEquals(accountRequest, actualAccReqEmalAndInstitute);

        ______TS("Read account request using the given registration key");

        AccountRequest actualAccReqRegistrationKey =
                accountRequestDb.getAccountRequestByRegistrationKey(accountRequest.getRegistrationKey());
        verifyEquals(accountRequest, actualAccReqRegistrationKey);

        ______TS("Read account request using the given start and end timing");

        List<AccountRequest> actualAccReqCreatedAt =
                accountRequestDb.getAccountRequests(accountRequest.getCreatedAt(), accountRequest.getCreatedAt());
        assertEquals(1, actualAccReqCreatedAt.size());
        verifyEquals(accountRequest, actualAccReqCreatedAt.get(0));

        ______TS("Read account request not found using the outside start and end timing");

        List<AccountRequest> actualAccReqCreatedAtOutside =
                accountRequestDb.getAccountRequests(
                        accountRequest.getCreatedAt().minusMillis(3000),
                        accountRequest.getCreatedAt().minusMillis(2000));
        assertEquals(0, actualAccReqCreatedAtOutside.size());

        ______TS("Create account request, same email address and institute already exist, creates successfully");

        AccountRequest identicalAccountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        assertNotSame(accountRequest, identicalAccountRequest);

        accountRequestDb.createAccountRequest(identicalAccountRequest);
        AccountRequest actualIdenticalAccountRequest =
                accountRequestDb.getAccountRequestByRegistrationKey(identicalAccountRequest.getRegistrationKey());
        verifyEquals(identicalAccountRequest, actualIdenticalAccountRequest);

        ______TS("Delete account request that was created");

        accountRequestDb.deleteAccountRequest(accountRequest);

        AccountRequest actualAccountRequest =
                accountRequestDb.getAccountRequestByRegistrationKey(accountRequest.getRegistrationKey());
        assertNull(actualAccountRequest);
    }

    @Test
    public void testGetAccountRequest_nonExistentAccountRequest_returnsNull() {
        UUID id = UUID.randomUUID();
        AccountRequest actualAccountRequest = accountRequestDb.getAccountRequest(id);
        assertNull(actualAccountRequest);
    }

    @Test
    public void testGetAccountRequest_existingAccountRequest_getsSuccessfully() throws InvalidParametersException {
        AccountRequest expectedAccountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        UUID id = expectedAccountRequest.getId();
        accountRequestDb.createAccountRequest(expectedAccountRequest);
        AccountRequest actualAccountRequest = accountRequestDb.getAccountRequest(id);
        assertEquals(expectedAccountRequest, actualAccountRequest);
    }

}

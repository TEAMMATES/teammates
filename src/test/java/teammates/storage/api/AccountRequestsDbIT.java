package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.storage.entity.AccountRequest;
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.GroupNames;

/**
 * SUT: {@link AccountRequestsDb}.
 */
public class AccountRequestsDbIT extends BaseTestCaseWithDatabaseAccess {

    private final AccountRequestsDb accountRequestDb = AccountRequestsDb.inst();

    @Test(groups = GroupNames.INTEGRATION)
    public void testPersistReadRemoveAccountRequest() {
        ______TS("Create account request, does not exists, succeeds");

        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        inTransaction(() -> accountRequestDb.persistAccountRequest(accountRequest));

        ______TS("Read account request using the given ID");

        AccountRequest actualAccReqEmalAndInstitute =
                inTransaction(() -> accountRequestDb.getAccountRequest(accountRequest.getId()));
        assertEquals(accountRequest, actualAccReqEmalAndInstitute);

        ______TS("Read account request using the given registration key");

        AccountRequest actualAccReqRegistrationKey =
                inTransaction(() -> accountRequestDb.getAccountRequestByRegistrationKey(
                        accountRequest.getRegistrationKey()));
        assertEquals(accountRequest, actualAccReqRegistrationKey);

        ______TS("Read account request using the given start and end timing");

        List<AccountRequest> actualAccReqCreatedAt =
                inTransaction(() -> accountRequestDb.getAccountRequests(
                        accountRequest.getCreatedAt(), accountRequest.getCreatedAt()));
        assertEquals(1, actualAccReqCreatedAt.size());
        assertEquals(accountRequest, actualAccReqCreatedAt.get(0));

        ______TS("Read account request not found using the outside start and end timing");

        List<AccountRequest> actualAccReqCreatedAtOutside =
                inTransaction(() -> accountRequestDb.getAccountRequests(
                        accountRequest.getCreatedAt().minusMillis(3000),
                        accountRequest.getCreatedAt().minusMillis(2000)));
        assertEquals(0, actualAccReqCreatedAtOutside.size());

        ______TS("Create account request, same email address and institute already exist, creates successfully");

        AccountRequest identicalAccountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        assertNotSame(accountRequest, identicalAccountRequest);

        inTransaction(() -> accountRequestDb.persistAccountRequest(identicalAccountRequest));
        AccountRequest actualIdenticalAccountRequest =
                inTransaction(() -> accountRequestDb.getAccountRequestByRegistrationKey(
                        identicalAccountRequest.getRegistrationKey()));
        assertEquals(identicalAccountRequest, actualIdenticalAccountRequest);

        ______TS("Remove account request that was created");

        inTransaction(() -> accountRequestDb.removeAccountRequest(accountRequest));

        AccountRequest actualAccountRequest =
                inTransaction(() -> accountRequestDb.getAccountRequestByRegistrationKey(
                        accountRequest.getRegistrationKey()));
        assertNull(actualAccountRequest);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetAccountRequest_nonExistentAccountRequest_returnsNull() {
        UUID id = UUID.randomUUID();
        AccountRequest actualAccountRequest = inTransaction(() -> accountRequestDb.getAccountRequest(id));
        assertNull(actualAccountRequest);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void testGetAccountRequest_existingAccountRequest_getsSuccessfully() {
        AccountRequest expectedAccountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        UUID id = expectedAccountRequest.getId();
        inTransaction(() -> accountRequestDb.persistAccountRequest(expectedAccountRequest));
        AccountRequest actualAccountRequest = inTransaction(() -> accountRequestDb.getAccountRequest(id));
        assertEquals(expectedAccountRequest, actualAccountRequest);
    }

}

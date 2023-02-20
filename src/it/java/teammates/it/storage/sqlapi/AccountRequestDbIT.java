package teammates.it.storage.sqlapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.AccountRequestDb;
import teammates.storage.sqlentity.AccountRequest;

/**
 * SUT: {@link CoursesDb}.
 */
public class AccountRequestDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final AccountRequestDb accountRequestDb = AccountRequestDb.inst();

    @Test
    public void testCreateReadDeleteAccountRequest() throws Exception {
        ______TS("Create account request, does not exists, succeeds");

        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        accountRequestDb.createAccountRequest(accountRequest);

        ______TS("Read account request using the given email and institute");

        AccountRequest actualAccReqEmalAndInstitute =
                accountRequestDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyEquals(accountRequest, actualAccReqEmalAndInstitute);

        ______TS("Read account request using the given registration key");

        AccountRequest actualAccReqRegistrationKey =
                accountRequestDb.getAccountRequest(accountRequest.getRegistrationKey());
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

        ______TS("Create acccount request, already exists, execption thrown");

        AccountRequest identicalAccountRequest =
                new AccountRequest("test@gmail.com", "name", "institute");
        assertNotSame(accountRequest, identicalAccountRequest);

        assertThrows(EntityAlreadyExistsException.class,
                () -> accountRequestDb.createAccountRequest(identicalAccountRequest));

        ______TS("Delete account request that was created");

        accountRequestDb.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());

        AccountRequest actualAccountRequest =
                accountRequestDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verifyEquals(null, actualAccountRequest);
    }

    @Test
    public void testUpdateAccountRequest() throws Exception {
        ______TS("Update account request, does not exists, exception thrown");

        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestDb.updateAccountRequest(accountRequest));

        ______TS("Update account request, already exists, update successful");

        accountRequestDb.createAccountRequest(accountRequest);
        accountRequest.setName("new account request name");

        accountRequestDb.updateAccountRequest(accountRequest);
        AccountRequest actual = accountRequestDb.getAccountRequest(
                accountRequest.getEmail(), accountRequest.getInstitute());
        verifyEquals(accountRequest, actual);
    }
}

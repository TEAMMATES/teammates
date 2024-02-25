package teammates.it.storage.sqlapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.AccountRequestsDb;
import teammates.storage.sqlentity.AccountRequest;

/**
 * SUT: {@link AccountRequestsDb}.
 */
public class AccountRequestsDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final AccountRequestsDb accountRequestDb = AccountRequestsDb.inst();

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

        ______TS("Create acccount request, already exists, execption thrown");

        AccountRequest identicalAccountRequest =
                new AccountRequest("test@gmail.com", "name", "institute");
        assertNotSame(accountRequest, identicalAccountRequest);

        assertThrows(EntityAlreadyExistsException.class,
                () -> accountRequestDb.createAccountRequest(identicalAccountRequest));

        ______TS("Delete account request that was created");

        accountRequestDb.deleteAccountRequest(accountRequest);

        AccountRequest actualAccountRequest =
                accountRequestDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        assertNull(actualAccountRequest);
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

    @Test
    public void testSqlInjectionInCreateAccountRequestEmailField() throws Exception {
        ______TS("SQL Injection test in email field");

        // Attempt to use SQL commands in email field
        String email = "email'/**/OR/**/1=1/**/@gmail.com";
        AccountRequest accountRequest = new AccountRequest(email, "name", "institute");

        // The system should treat the input as a plain text string
        accountRequestDb.createAccountRequest(accountRequest);
        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        assertEquals(email, actual.getEmail());
    }

    @Test
    public void testSqlInjectionInCreateAccountRequestNameField() throws Exception {
        ______TS("SQL Injection test in name field");

        // Attempt to use SQL commands in name field
        String name = "name'; SELECT * FROM account_requests; --";
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", name, "institute");

        // The system should treat the input as a plain text string
        accountRequestDb.createAccountRequest(accountRequest);
        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        assertEquals(name, actual.getName());
    }

    @Test
    public void testSqlInjectionInCreateAccountRequestInstituteField() throws Exception {
        ______TS("SQL Injection test in institute field");

        // Attempt to use SQL commands in institute field
        String institute = "institute'; DROP TABLE account_requests; --";
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", institute);

        // The system should treat the input as a plain text string
        accountRequestDb.createAccountRequest(accountRequest);
        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getEmail(), institute);
        assertEquals(institute, actual.getInstitute());
    }

    @Test
    public void testSqlInjectionInGetAccountRequest() throws Exception {
        ______TS("SQL Injection test in getAccountRequest");

        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        accountRequestDb.createAccountRequest(accountRequest);

        String instituteInjection = "institute'; DROP TABLE account_requests; --";
        AccountRequest actualInjection = accountRequestDb.getAccountRequest(accountRequest.getEmail(), instituteInjection);
        assertNull(actualInjection);

        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        assertEquals(accountRequest, actual);
    }

    @Test
    public void testSqlInjectionInGetAccountRequestByRegistrationKey() throws Exception {
        ______TS("SQL Injection test in getAccountRequestByRegistrationKey");

        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        accountRequestDb.createAccountRequest(accountRequest);

        String regKeyInjection = "regKey'; DROP TABLE account_requests; --";
        AccountRequest actualInjection = accountRequestDb.getAccountRequestByRegistrationKey(regKeyInjection);
        assertNull(actualInjection);

        AccountRequest actual = accountRequestDb.getAccountRequestByRegistrationKey(accountRequest.getRegistrationKey());
        assertEquals(accountRequest, actual);
    }

    @Test
    public void testSqlInjectionInUpdateAccountRequest() throws Exception {
        ______TS("SQL Injection test in updateAccountRequest");

        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        accountRequestDb.createAccountRequest(accountRequest);

        String nameInjection = "newName'; DROP TABLE account_requests; --";
        accountRequest.setName(nameInjection);
        accountRequestDb.updateAccountRequest(accountRequest);

        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        assertEquals(accountRequest, actual);
    }

    @Test
    public void testSqlInjectionInDeleteAccountRequest() throws Exception {
        ______TS("SQL Injection test in deleteAccountRequest");

        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        accountRequestDb.createAccountRequest(accountRequest);

        String emailInjection = "email'/**/OR/**/1=1/**/@gmail.com";
        String nameInjection = "name'; DROP TABLE account_requests; --";
        String instituteInjection = "institute'; DROP TABLE account_requests; --";
        AccountRequest accountRequestInjection = new AccountRequest(emailInjection, nameInjection, instituteInjection);
        accountRequestDb.deleteAccountRequest(accountRequestInjection);

        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        assertEquals(accountRequest, actual);
    }

    @Test
    public void testSqlInjectionSearchAccountRequestsInWholeSystem() throws Exception {
        ______TS("SQL Injection test in searchAccountRequestsInWholeSystem");

        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        accountRequestDb.createAccountRequest(accountRequest);

        String searchInjection = "institute'; DROP TABLE account_requests; --";
        List<AccountRequest> actualInjection = accountRequestDb.searchAccountRequestsInWholeSystem(searchInjection);
        assertEquals(0, actualInjection.size());

        AccountRequest actual = accountRequestDb.getAccountRequest("test@gmail.com", "institute");
        assertEquals(accountRequest, actual);
    }
}

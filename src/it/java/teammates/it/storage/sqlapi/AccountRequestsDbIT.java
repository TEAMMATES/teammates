package teammates.it.storage.sqlapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
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
    public void testSqlInjectionInEmailField() throws Exception {
        ______TS("SQL Injection test in email field");
    
        // Attempt to use SQL commands in email field
        String email = "name'; DROP TABLE account_requests; --@gmail.com";
        AccountRequest accountRequest = new AccountRequest(email, "name", "institute");
    
        // The regex check should fail and throw an exception
        assertThrows(InvalidParametersException.class,
                () -> accountRequestDb.createAccountRequest(accountRequest));
    }
    
    @Test
    public void testSqlInjectionInNameField() throws Exception {
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
    public void testSqlInjectionInInstituteField() throws Exception {
        ______TS("SQL Injection test in institute field");
    
        // Attempt to use SQL commands in institute field
        String institute = "institute'; DROP TABLE account_requests; --";
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", institute);
    
        // The system should treat the input as a plain text string
        accountRequestDb.createAccountRequest(accountRequest);
        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getEmail(), institute);
        assertEquals(institute, actual.getInstitute());
    }
}

package teammates.it.storage.sqlapi;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
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

    @Test
    public void testUpdateAccountRequest() throws Exception {
        ______TS("Update account request, does not exists, exception thrown");

        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestDb.updateAccountRequest(accountRequest));

        ______TS("Update account request, already exists, update successful");

        accountRequestDb.createAccountRequest(accountRequest);
        accountRequest.setName("new account request name");

        accountRequestDb.updateAccountRequest(accountRequest);
        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getId());
        verifyEquals(accountRequest, actual);
    }

    @Test
    public void testSqlInjectionInCreateAccountRequestEmailField() throws Exception {
        ______TS("SQL Injection test in email field");

        // Attempt to use SQL commands in email field
        String email = "email'/**/OR/**/1=1/**/@gmail.com";
        AccountRequest accountRequest =
                new AccountRequest(email, "name", "institute", AccountRequestStatus.PENDING, "comments");

        // The system should treat the input as a plain text string
        accountRequestDb.createAccountRequest(accountRequest);
        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getId());
        assertEquals(email, actual.getEmail());
    }

    @Test
    public void testSqlInjectionInCreateAccountRequestNameField() throws Exception {
        ______TS("SQL Injection test in name field");

        // Attempt to use SQL commands in name field
        String name = "name'; SELECT * FROM account_requests; --";
        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", name, "institute", AccountRequestStatus.PENDING, "comments");

        // The system should treat the input as a plain text string
        accountRequestDb.createAccountRequest(accountRequest);
        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getId());
        assertEquals(name, actual.getName());
    }

    @Test
    public void testSqlInjectionInCreateAccountRequestInstituteField() throws Exception {
        ______TS("SQL Injection test in institute field");

        // Attempt to use SQL commands in institute field
        String institute = "institute'; DROP TABLE account_requests; --";
        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", institute, AccountRequestStatus.PENDING, "comments");

        // The system should treat the input as a plain text string
        accountRequestDb.createAccountRequest(accountRequest);
        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getId());
        assertEquals(institute, actual.getInstitute());
    }

    @Test
    public void testSqlInjectionInCreateAccountRequestCommentsField() throws Exception {
        ______TS("SQL Injection test in comments field");

        // Attempt to use SQL commands in comments field
        String comments = "comment'; DROP TABLE account_requests; --";
        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, comments);

        // The system should treat the input as a plain text string
        accountRequestDb.createAccountRequest(accountRequest);
        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getId());
        assertEquals(comments, actual.getComments());
    }

    @Test
    public void testSqlInjectionInGetAccountRequestByRegistrationKey() throws Exception {
        ______TS("SQL Injection test in getAccountRequestByRegistrationKey");

        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        accountRequestDb.createAccountRequest(accountRequest);

        String regKeyInjection = "regKey'; DROP TABLE account_requests; --";
        AccountRequest actualInjection = accountRequestDb.getAccountRequestByRegistrationKey(regKeyInjection);
        assertNull(actualInjection);

        AccountRequest actual = accountRequestDb.getAccountRequestByRegistrationKey(accountRequest.getRegistrationKey());
        assertEquals(accountRequest, actual);
    }

    @Test
    public void testSqlInjectionInGetApprovedAccountRequestsForEmail() throws Exception {
        ______TS("SQL Injection test in getApprovedAccountRequestsForEmail");

        String email = "test@gmail.com";
        AccountRequest accountRequest =
                new AccountRequest(email, "name", "institute", AccountRequestStatus.APPROVED, "comments");
        accountRequestDb.createAccountRequest(accountRequest);

        // Attempt to use SQL commands in email field
        String emailInjection = "email'/**/OR/**/1=1/**/@gmail.com";
        List<AccountRequest> actualInjection = accountRequestDb.getApprovedAccountRequestsForEmail(emailInjection);
        // The system should treat the input as a plain text string
        assertEquals(0, actualInjection.size());
    }

    @Test
    public void testSqlInjectionInUpdateAccountRequest() throws Exception {
        ______TS("SQL Injection test in updateAccountRequest");

        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        accountRequestDb.createAccountRequest(accountRequest);

        String nameInjection = "newName'; DROP TABLE account_requests; --";
        accountRequest.setName(nameInjection);
        accountRequestDb.updateAccountRequest(accountRequest);

        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getId());
        assertEquals(accountRequest, actual);
    }

    @Test
    public void testSqlInjectionInDeleteAccountRequest() throws Exception {
        ______TS("SQL Injection test in deleteAccountRequest");

        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        accountRequestDb.createAccountRequest(accountRequest);

        String emailInjection = "email'/**/OR/**/1=1/**/@gmail.com";
        String nameInjection = "name'; DROP TABLE account_requests; --";
        String instituteInjection = "institute'; DROP TABLE account_requests; --";
        AccountRequest accountRequestInjection = new AccountRequest(emailInjection, nameInjection, instituteInjection,
                AccountRequestStatus.PENDING, "comments");
        accountRequestDb.deleteAccountRequest(accountRequestInjection);

        AccountRequest actual = accountRequestDb.getAccountRequest(accountRequest.getId());
        assertEquals(accountRequest, actual);
    }

}

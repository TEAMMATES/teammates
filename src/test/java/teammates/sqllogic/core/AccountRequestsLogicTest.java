package teammates.sqllogic.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.sqlapi.AccountRequestsDb;
import teammates.storage.sqlentity.AccountRequest;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountRequestsLogic}.
 */
public class AccountRequestsLogicTest extends BaseTestCase {

    private AccountRequestsLogic accountRequestsLogic = AccountRequestsLogic.inst();
    private AccountRequestsDb accountRequestsDb;

    @BeforeMethod
    public void setUpMethod() {
        accountRequestsDb = mock(AccountRequestsDb.class);
        accountRequestsLogic.initLogicDependencies(accountRequestsDb);
    }

    @Test
    public void testCreateAccountRequest_typicalRequest_success() throws Exception {
        AccountRequest accountRequest = getTypicalAccountRequest();
        when(accountRequestsDb.createAccountRequest(accountRequest)).thenReturn(accountRequest);
        AccountRequest createdAccountRequest = accountRequestsLogic.createAccountRequest(accountRequest);

        assertEquals(accountRequest, createdAccountRequest);
        verify(accountRequestsDb, times(1)).createAccountRequest(accountRequest);
    }

    @Test
    public void testCreateAccountRequest_requestAlreadyExists_success() throws Exception {
        AccountRequest accountRequest1 = getTypicalAccountRequest();
        AccountRequest accountRequest2 = getTypicalAccountRequest();
        when(accountRequestsDb.createAccountRequest(accountRequest1))
                .thenReturn(accountRequest1);
        when(accountRequestsDb.createAccountRequest(accountRequest2))
                        .thenReturn(accountRequest2);

        accountRequestsLogic.createAccountRequest(accountRequest1);
        accountRequestsLogic.createAccountRequest(accountRequest2);
        verify(accountRequestsDb, times(1)).createAccountRequest(accountRequest1);
        verify(accountRequestsDb, times(1)).createAccountRequest(accountRequest2);
    }

    @Test
    public void testCreateAccountRequest_invalidParams_failure() throws Exception {
        AccountRequest invalidEmailAccountRequest = getTypicalAccountRequest();
        invalidEmailAccountRequest.setEmail("invalid email");
        when(accountRequestsDb.createAccountRequest(invalidEmailAccountRequest))
                .thenThrow(new InvalidParametersException("test exception"));

        assertThrows(InvalidParametersException.class, () -> {
            accountRequestsLogic.createAccountRequest(invalidEmailAccountRequest);
        });
        verify(accountRequestsDb, times(1)).createAccountRequest(invalidEmailAccountRequest);
    }

    @Test
    public void testUpdateAccountRequest_typicalRequest_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequest ar = getTypicalAccountRequest();
        when(accountRequestsDb.updateAccountRequest(ar)).thenReturn(ar);
        AccountRequest updatedAr = accountRequestsLogic.updateAccountRequest(ar);

        assertEquals(ar, updatedAr);
        verify(accountRequestsDb, times(1)).updateAccountRequest(ar);
    }

    @Test
    public void testUpdateAccountRequest_requestNotFound_failure()
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequest arNotFound = getTypicalAccountRequest();
        when(accountRequestsDb.updateAccountRequest(arNotFound)).thenThrow(new EntityDoesNotExistException("test message"));

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsLogic.updateAccountRequest(arNotFound));
        verify(accountRequestsDb, times(1)).updateAccountRequest(any(AccountRequest.class));
    }

    @Test
    public void testDeleteAccountRequest_typicalRequest_success() {
        AccountRequest ar = getTypicalAccountRequest();
        when(accountRequestsDb.getAccountRequest(ar.getId())).thenReturn(ar);
        accountRequestsLogic.deleteAccountRequest(ar.getId());

        verify(accountRequestsDb, times(1)).deleteAccountRequest(any(AccountRequest.class));
    }

    @Test
    public void testDeleteAccountRequest_nonexistentRequest_shouldSilentlyDelete() {
        UUID nonexistentUuid = UUID.fromString("00000000-0000-4000-8000-000000000100");
        accountRequestsLogic.deleteAccountRequest(nonexistentUuid);

        verify(accountRequestsDb, times(1)).deleteAccountRequest(nullable(AccountRequest.class));
    }

    @Test
    public void testGetAccountRequestByRegistrationKey_typicalRequest_success() {
        AccountRequest ar = getTypicalAccountRequest();
        String regkey = "regkey";
        ar.setRegistrationKey(regkey);
        when(accountRequestsDb.getAccountRequestByRegistrationKey(regkey)).thenReturn(ar);
        AccountRequest actualAr =
                accountRequestsLogic.getAccountRequestByRegistrationKey(ar.getRegistrationKey());

        assertEquals(ar, actualAr);
        verify(accountRequestsDb, times(1)).getAccountRequestByRegistrationKey(regkey);
    }

    @Test
    public void testGetAccountRequestByRegistrationKey_nonexistentRequest_shouldReturnNull() throws Exception {
        String nonexistentRegkey = "not_exist";
        when(accountRequestsDb.getAccountRequestByRegistrationKey(nonexistentRegkey)).thenReturn(null);

        assertNull(accountRequestsLogic.getAccountRequestByRegistrationKey(nonexistentRegkey));
        verify(accountRequestsDb, times(1)).getAccountRequestByRegistrationKey(nonexistentRegkey);
    }

    @Test
    public void testResetAccountRequest_typicalRequest_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequest accountRequest = getTypicalAccountRequest();
        accountRequest.setRegisteredAt(Const.TIME_REPRESENTS_NOW);
        when(accountRequestsDb.getAccountRequest(accountRequest.getId()))
                .thenReturn(accountRequest);
        when(accountRequestsDb.updateAccountRequest(accountRequest)).thenReturn(accountRequest);
        accountRequest = accountRequestsLogic.resetAccountRequest(accountRequest.getId());

        assertNull(accountRequest.getRegisteredAt());
        verify(accountRequestsDb, times(1)).getAccountRequest(accountRequest.getId());
    }

    @Test
    public void testResetAccountRequest_nonexistentRequest_failure()
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequest accountRequest = getTypicalAccountRequest();
        accountRequest.setRegisteredAt(Const.TIME_REPRESENTS_NOW);
        when(accountRequestsDb.getAccountRequest(accountRequest.getId()))
                .thenReturn(null);
        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestsLogic.resetAccountRequest(accountRequest.getId()));
        verify(accountRequestsDb, times(1)).getAccountRequest(accountRequest.getId());
        verify(accountRequestsDb, times(0)).updateAccountRequest(nullable(AccountRequest.class));
    }

    @Test
    public void testGetAccountRequest_nonExistentAccountRequest_returnsNull() {
        UUID id = UUID.randomUUID();
        when(accountRequestsDb.getAccountRequest(id)).thenReturn(null);
        AccountRequest actualAccountRequest = accountRequestsLogic.getAccountRequest(id);
        verify(accountRequestsDb).getAccountRequest(id);
        assertNull(actualAccountRequest);
    }

    @Test
    public void testGetAccountRequest_existingAccountRequest_getsSuccessfully() {
        AccountRequest expectedAccountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        UUID id = expectedAccountRequest.getId();
        when(accountRequestsDb.getAccountRequest(id)).thenReturn(expectedAccountRequest);
        AccountRequest actualAccountRequest = accountRequestsLogic.getAccountRequest(id);
        verify(accountRequestsDb).getAccountRequest(id);
        assertEquals(expectedAccountRequest, actualAccountRequest);
    }
}

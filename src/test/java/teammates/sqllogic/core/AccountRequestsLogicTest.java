package teammates.sqllogic.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
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

    private final AccountRequestsLogic arLogic = AccountRequestsLogic.inst();
    private AccountRequestsDb arDb;

    @BeforeMethod
    public void setUpMethod() {
        arDb = mock(AccountRequestsDb.class);
        arLogic.initLogicDependencies(arDb);
    }

    @Test
    public void testCreateAccountRequest_typicalRequest_success() throws Exception {
        AccountRequest accountRequest = getTypicalAccountRequest();
        when(arDb.createAccountRequest(accountRequest)).thenReturn(accountRequest);
        AccountRequest createdAccountRequest = arLogic.createAccountRequest(accountRequest);

        assertEquals(accountRequest, createdAccountRequest);
        verify(arDb, times(1)).createAccountRequest(accountRequest);
    }

    @Test
    public void testCreateAccountRequest_requestAlreadyExists_failure() throws Exception {
        AccountRequest duplicateAccountRequest = getTypicalAccountRequest();
        when(arDb.createAccountRequest(duplicateAccountRequest))
                .thenThrow(new EntityAlreadyExistsException("test exception"));

        assertThrows(EntityAlreadyExistsException.class, () -> {
            arLogic.createAccountRequest(duplicateAccountRequest);
        });
        verify(arDb, times(1)).createAccountRequest(duplicateAccountRequest);
    }

    @Test
    public void testCreateAccountRequest_invalidParams_failure() throws Exception {
        AccountRequest invalidEmailAccountRequest = getTypicalAccountRequest();
        invalidEmailAccountRequest.setEmail("invalid email");
        when(arDb.createAccountRequest(invalidEmailAccountRequest))
                .thenThrow(new InvalidParametersException("test exception"));

        assertThrows(InvalidParametersException.class, () -> {
            arLogic.createAccountRequest(invalidEmailAccountRequest);
        });
        verify(arDb, times(1)).createAccountRequest(invalidEmailAccountRequest);
    }

    @Test
    public void testUpdateAccountRequest_typicalRequest_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequest ar = getTypicalAccountRequest();
        when(arDb.updateAccountRequest(ar)).thenReturn(ar);
        AccountRequest updatedAr = arLogic.updateAccountRequest(ar);

        assertEquals(ar, updatedAr);
        verify(arDb, times(1)).updateAccountRequest(ar);
    }

    @Test
    public void testUpdateAccountRequest_requestNotFound_failure()
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequest arNotFound = getTypicalAccountRequest();
        when(arDb.updateAccountRequest(arNotFound)).thenThrow(new EntityDoesNotExistException("test message"));

        assertThrows(EntityDoesNotExistException.class,
                () -> arLogic.updateAccountRequest(arNotFound));
        verify(arDb, times(1)).updateAccountRequest(any(AccountRequest.class));
    }

    @Test
    public void testDeleteAccountRequest_typicalRequest_success() {
        AccountRequest ar = getTypicalAccountRequest();
        when(arDb.getAccountRequest(ar.getEmail(), ar.getInstitute())).thenReturn(ar);
        arLogic.deleteAccountRequest(ar.getEmail(), ar.getInstitute());

        verify(arDb, times(1)).deleteAccountRequest(any(AccountRequest.class));
    }

    @Test
    public void testDeleteAccountRequest_nonexistentRequest_shouldSilentlyDelete() {
        arLogic.deleteAccountRequest("not_exist", "not_exist");

        verify(arDb, times(1)).deleteAccountRequest(nullable(AccountRequest.class));
    }

    @Test
    public void testGetAccountRequestByRegistrationKey_typicalRequest_success() {
        AccountRequest ar = getTypicalAccountRequest();
        String regkey = "regkey";
        ar.setRegistrationKey(regkey);
        when(arDb.getAccountRequestByRegistrationKey(regkey)).thenReturn(ar);
        AccountRequest actualAr =
                arLogic.getAccountRequestByRegistrationKey(ar.getRegistrationKey());

        assertEquals(ar, actualAr);
        verify(arDb, times(1)).getAccountRequestByRegistrationKey(regkey);
    }

    @Test
    public void testGetAccountRequestByRegistrationKey_nonexistentRequest_shouldReturnNull() throws Exception {
        String nonexistentRegkey = "not_exist";
        when(arDb.getAccountRequestByRegistrationKey(nonexistentRegkey)).thenReturn(null);

        assertNull(arLogic.getAccountRequestByRegistrationKey(nonexistentRegkey));
        verify(arDb, times(1)).getAccountRequestByRegistrationKey(nonexistentRegkey);
    }

    @Test
    public void testGetAccountRequest_typicalRequest_success() {
        AccountRequest expectedAr = getTypicalAccountRequest();
        when(arDb.getAccountRequest(expectedAr.getEmail(), expectedAr.getInstitute())).thenReturn(expectedAr);
        AccountRequest actualAr =
                arLogic.getAccountRequest(expectedAr.getEmail(), expectedAr.getInstitute());

        assertEquals(expectedAr, actualAr);
        verify(arDb, times(1)).getAccountRequest(expectedAr.getEmail(), expectedAr.getInstitute());
    }

    @Test
    public void testGetAccountRequest_nonexistentRequest_shouldReturnNull() {
        String nonexistentEmail = "not-found@test.com";
        String nonexistentInstitute = "not-found";
        when(arDb.getAccountRequest(nonexistentEmail, nonexistentInstitute)).thenReturn(null);

        assertNull(arLogic.getAccountRequest(nonexistentEmail, nonexistentInstitute));
        verify(arDb, times(1)).getAccountRequest(nonexistentEmail, nonexistentInstitute);
    }

    @Test
    public void testResetAccountRequest_typicalRequest_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequest accountRequest = getTypicalAccountRequest();
        accountRequest.setRegisteredAt(Const.TIME_REPRESENTS_NOW);
        when(arDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute()))
                .thenReturn(accountRequest);
        when(arDb.updateAccountRequest(accountRequest)).thenReturn(accountRequest);
        accountRequest = arLogic.resetAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());

        assertNull(accountRequest.getRegisteredAt());
        verify(arDb, times(1)).getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
    }

    @Test
    public void testResetAccountRequest_nonexistentRequest_failure()
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequest accountRequest = getTypicalAccountRequest();
        accountRequest.setRegisteredAt(Const.TIME_REPRESENTS_NOW);
        when(arDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute()))
                .thenReturn(null);
        assertThrows(EntityDoesNotExistException.class,
                () -> arLogic.resetAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute()));
        verify(arDb, times(1)).getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        verify(arDb, times(0)).updateAccountRequest(nullable(AccountRequest.class));
    }
}

package teammates.sqllogic.core;

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
    public void testCreateAccountRequest() throws Exception {
        ______TS("Typical success case");
        AccountRequest accountRequest = getTypicalAccountRequest();

        when(arDb.createAccountRequest(accountRequest)).thenReturn(accountRequest);
        AccountRequest createdAccountRequest = arLogic.createAccountRequest(accountRequest);

        assertEquals(accountRequest, createdAccountRequest);
        verify(arDb, times(1)).createAccountRequest(accountRequest);

        ______TS("Success: Create from name, email, institute");
        when(arDb.createAccountRequest(getTypicalAccountRequest())).thenReturn(accountRequest);
        createdAccountRequest = arLogic.createAccountRequest(
                accountRequest.getName(), accountRequest.getEmail(), accountRequest.getInstitute());
        
        ______TS("Failure: duplicate account request");

        AccountRequest duplicateAccountRequest = getTypicalAccountRequest();

        when(arDb.createAccountRequest(duplicateAccountRequest))
                .thenThrow(new EntityAlreadyExistsException("test exception"));

        assertThrows(EntityAlreadyExistsException.class, () -> {
            arLogic.createAccountRequest(duplicateAccountRequest);
        });

        ______TS("Invalid non-null parameter");
        AccountRequest invalidEmailAccountRequest = getTypicalAccountRequest();
        invalidEmailAccountRequest.setEmail("invalid email");
        when(arDb.createAccountRequest(invalidEmailAccountRequest))
                .thenThrow(new InvalidParametersException("test exception"));

        assertThrows(InvalidParametersException.class, () -> {
            arLogic.createAccountRequest(invalidEmailAccountRequest);
        });
    }

    @Test
    public void testUpdateAccountRequest() throws Exception {
        AccountRequest ar = getTypicalAccountRequest();

        ______TS("Typical success case");
        when(arDb.updateAccountRequest(ar)).thenReturn(ar);
        AccountRequest updatedAr = arLogic.updateAccountRequest(ar);
        assertEquals(ar, updatedAr);

        ______TS("Failure: Account request not found");
        AccountRequest arNotFound = getTypicalAccountRequest();
        when(arDb.updateAccountRequest(arNotFound)).thenThrow(new EntityDoesNotExistException("test message"));

        assertThrows(EntityDoesNotExistException.class,
                () -> arLogic.updateAccountRequest(arNotFound));
    }

    @Test
    public void testDeleteAccountRequest() throws Exception {
        AccountRequest ar = getTypicalAccountRequest();

        ______TS("Silent deletion of non-existent account request");

        arLogic.deleteAccountRequest("not_exist", "not_exist");

        ______TS("Typical success case");

        arLogic.deleteAccountRequest(ar.getEmail(), ar.getInstitute());

        ______TS("Silent deletion of same account request");

        arLogic.deleteAccountRequest(ar.getEmail(), ar.getInstitute());
    }

    @Test
    public void testGetAccountRequestForRegistrationKey() throws Exception {
        AccountRequest ar = getTypicalAccountRequest();
        String regkey = "regkey";
        ar.setRegistrationKey(regkey);
        when(arDb.getAccountRequestByRegistrationKey(regkey)).thenReturn(ar);

        ______TS("typical success case");

        AccountRequest actualAr =
                arLogic.getAccountRequestByRegistrationKey(ar.getRegistrationKey());
        assertEquals(ar, actualAr);

        ______TS("account request not found");
        when(arDb.getAccountRequestByRegistrationKey("not-found")).thenReturn(null);
        assertNull(arLogic.getAccountRequestByRegistrationKey("not-found"));
    }

    @Test
    public void testGetAccountRequest() {
        AccountRequest expectedAr = getTypicalAccountRequest();

        ______TS("Typical success case");

        when(arDb.getAccountRequest(expectedAr.getEmail(), expectedAr.getInstitute())).thenReturn(expectedAr);
        AccountRequest actualAr =
                arLogic.getAccountRequest(expectedAr.getEmail(), expectedAr.getInstitute());
        assertEquals(expectedAr, actualAr);

        ______TS("Failure: Account request not found");

        when(arDb.getAccountRequest("not-found@test.com", "not-found")).thenReturn(null);
        assertNull(arLogic.getAccountRequest("not-found@test.com", "not-found"));
    }

    @Test
    public void testResetAccountRequest_typicalRequest_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        ______TS("Typical success case");
        AccountRequest accountRequest = getTypicalAccountRequest();
        accountRequest.setRegisteredAt(Const.TIME_REPRESENTS_NOW);
        when(arDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute()))
                .thenReturn(accountRequest);
        when(arDb.updateAccountRequest(accountRequest)).thenReturn(accountRequest);
        accountRequest = arLogic.resetAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        assertNull(accountRequest.getRegisteredAt());
    }

    @Test
    public void testResetAccountRequest_nullRequest_failure()
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequest accountRequest = getTypicalAccountRequest();
        accountRequest.setRegisteredAt(Const.TIME_REPRESENTS_NOW);
        when(arDb.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute()))
                .thenReturn(null);
        when(arDb.updateAccountRequest(null)).thenThrow(new EntityDoesNotExistException("test"));
        assertThrows(EntityDoesNotExistException.class,
                () -> arLogic.resetAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute()));
    }
}

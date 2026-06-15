package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountVerificationRequestsDb;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Institute;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountVerificationRequestsLogic}.
 */
public class AccountVerificationRequestsLogicTest extends BaseTestCase {

    private AccountVerificationRequestsLogic accountVerificationRequestsLogic = AccountVerificationRequestsLogic.inst();
    private AccountVerificationRequestsDb accountVerificationRequestsDb;

    @BeforeMethod
    public void setUpMethod() {
        accountVerificationRequestsDb = mock(AccountVerificationRequestsDb.class);
        accountVerificationRequestsLogic.initLogicDependencies(accountVerificationRequestsDb, mock(AccountsLogic.class),
                mock(InstitutesLogic.class));
    }

    @Test
    public void testCreateAccountVerificationRequest_typicalRequest_success() throws Exception {
        AccountVerificationRequest accountVerificationRequest = getTypicalAccountVerificationRequest();
        when(accountVerificationRequestsDb.persistAccountVerificationRequest(accountVerificationRequest)).thenReturn(accountVerificationRequest);
        AccountVerificationRequest createdAccountVerificationRequest = accountVerificationRequestsLogic.createAccountVerificationRequest(accountVerificationRequest);

        assertEquals(accountVerificationRequest, createdAccountVerificationRequest);
        verify(accountVerificationRequestsDb, times(1)).persistAccountVerificationRequest(accountVerificationRequest);
    }

    @Test
    public void testCreateAccountVerificationRequest_requestAlreadyExists_success() throws Exception {
        AccountVerificationRequest accountVerificationRequest1 = getTypicalAccountVerificationRequest();
        AccountVerificationRequest accountVerificationRequest2 = getTypicalAccountVerificationRequest();
        when(accountVerificationRequestsDb.persistAccountVerificationRequest(accountVerificationRequest1))
                .thenReturn(accountVerificationRequest1);
        when(accountVerificationRequestsDb.persistAccountVerificationRequest(accountVerificationRequest2))
                        .thenReturn(accountVerificationRequest2);

        accountVerificationRequestsLogic.createAccountVerificationRequest(accountVerificationRequest1);
        accountVerificationRequestsLogic.createAccountVerificationRequest(accountVerificationRequest2);
        verify(accountVerificationRequestsDb, times(1)).persistAccountVerificationRequest(accountVerificationRequest1);
        verify(accountVerificationRequestsDb, times(1)).persistAccountVerificationRequest(accountVerificationRequest2);
    }

    @Test
    public void testCreateAccountVerificationRequest_invalidParams_failure() {
        AccountVerificationRequest invalidEmailAccountVerificationRequest = getTypicalAccountVerificationRequest();
        invalidEmailAccountVerificationRequest.setEmail("invalid email");

        assertThrows(InvalidParametersException.class, () -> {
            accountVerificationRequestsLogic.createAccountVerificationRequest(invalidEmailAccountVerificationRequest);
        });
        verify(accountVerificationRequestsDb, never()).persistAccountVerificationRequest(invalidEmailAccountVerificationRequest);
    }

    @Test
    public void testUpdateAccountVerificationRequest_typicalRequest_success()
            throws InvalidParametersException {
        AccountVerificationRequest ar = getTypicalAccountVerificationRequest();
        AccountVerificationRequest updatedAr = accountVerificationRequestsLogic.updateAccountVerificationRequest(ar);

        assertEquals(ar, updatedAr);
    }

    @Test
    public void testUpdateAccountVerificationRequest_requestNotFound_failure()
            throws InvalidParametersException {
        AccountVerificationRequest arNotFound = getTypicalAccountVerificationRequest();
        AccountVerificationRequest updated = accountVerificationRequestsLogic.updateAccountVerificationRequest(arNotFound);
        assertEquals(updated, arNotFound);
    }

    @Test
    public void testDeleteAccountVerificationRequest_typicalRequest_success() {
        AccountVerificationRequest ar = getTypicalAccountVerificationRequest();
        when(accountVerificationRequestsDb.getAccountVerificationRequest(ar.getId())).thenReturn(ar);
        accountVerificationRequestsLogic.deleteAccountVerificationRequest(ar.getId());

        verify(accountVerificationRequestsDb, times(1)).removeAccountVerificationRequest(any(AccountVerificationRequest.class));
    }

    @Test
    public void testDeleteAccountVerificationRequest_nonexistentRequest_shouldSilentlyDelete() {
        UUID nonexistentUuid = UUID.fromString("00000000-0000-4000-8000-000000000100");
        accountVerificationRequestsLogic.deleteAccountVerificationRequest(nonexistentUuid);

        verify(accountVerificationRequestsDb, times(1)).removeAccountVerificationRequest(nullable(AccountVerificationRequest.class));
    }

    @Test
    public void testGetAccountVerificationRequest_nonExistentAccountVerificationRequest_returnsNull() {
        UUID id = UUID.randomUUID();
        when(accountVerificationRequestsDb.getAccountVerificationRequest(id)).thenReturn(null);
        AccountVerificationRequest actualAccountVerificationRequest = accountVerificationRequestsLogic.getAccountVerificationRequest(id);
        verify(accountVerificationRequestsDb).getAccountVerificationRequest(id);
        assertNull(actualAccountVerificationRequest);
    }

    @Test
    public void testGetAccountVerificationRequest_existingAccountVerificationRequest_getsSuccessfully() {
        AccountVerificationRequest expectedAccountVerificationRequest =
                new AccountVerificationRequest("test@gmail.com", "name", AccountVerificationRequestStatus.PENDING, "comments");
        new Institute("institute", "SG").addAccountVerificationRequest(expectedAccountVerificationRequest);
        UUID id = expectedAccountVerificationRequest.getId();
        when(accountVerificationRequestsDb.getAccountVerificationRequest(id)).thenReturn(expectedAccountVerificationRequest);
        AccountVerificationRequest actualAccountVerificationRequest = accountVerificationRequestsLogic.getAccountVerificationRequest(id);
        verify(accountVerificationRequestsDb).getAccountVerificationRequest(id);
        assertEquals(expectedAccountVerificationRequest, actualAccountVerificationRequest);
    }
}

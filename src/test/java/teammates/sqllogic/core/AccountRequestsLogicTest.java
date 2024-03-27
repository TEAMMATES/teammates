package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
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

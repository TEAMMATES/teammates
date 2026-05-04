package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

import java.util.List;
import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link AccountRequestDb}.
 */
public class AccountRequestsDbTest extends BaseTestCase {

    private AccountRequestsDb accountRequestDb;

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        accountRequestDb = spy(AccountRequestsDb.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateAccountRequest_typicalCase_success() throws InvalidParametersException {
        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        accountRequestDb.createAccountRequest(accountRequest);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(accountRequest));
    }

    @Test
    public void testGetAccountRequest_nonExistentAccountRequest_returnsNull() {
        UUID id = UUID.randomUUID();
        mockHibernateUtil.when(() -> HibernateUtil.get(AccountRequest.class, id)).thenReturn(null);
        AccountRequest actualAccountRequest = accountRequestDb.getAccountRequest(id);
        mockHibernateUtil.verify(() -> HibernateUtil.get(AccountRequest.class, id));
        assertNull(actualAccountRequest);
    }

    @Test
    public void testGetAccountRequest_existingAccountRequest_getsSuccessfully() {
        AccountRequest expectedAccountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        UUID id = expectedAccountRequest.getId();
        mockHibernateUtil.when(() -> HibernateUtil.get(AccountRequest.class, id)).thenReturn(expectedAccountRequest);
        AccountRequest actualAccountRequest = accountRequestDb.getAccountRequest(id);
        mockHibernateUtil.verify(() -> HibernateUtil.get(AccountRequest.class, id));
        assertEquals(expectedAccountRequest, actualAccountRequest);
    }

    @Test
    public void testDeleteAccountRequest_success() {
        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");

        accountRequestDb.deleteAccountRequest(accountRequest);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(accountRequest));
    }

    @Test
    public void testSearchAccountRequestsInWholeSystem_emptyString_returnsEmptyList() {
        List<AccountRequest> searchResult = accountRequestDb.searchAccountRequestsInWholeSystem("");
        assertTrue(searchResult.isEmpty());
    }
}

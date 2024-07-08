package teammates.storage.sqlapi;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlsearch.AccountRequestSearchManager;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code AccountRequestDb}.
 */
public class AccountRequestsDbTest extends BaseTestCase {

    private AccountRequestsDb accountRequestDb;

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    private AccountRequestSearchManager mockSearchManager;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        mockSearchManager = mock(AccountRequestSearchManager.class);
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
    public void testUpdateAccountRequest_invalidEmail_throwsInvalidParametersException() {
        AccountRequest accountRequestWithInvalidEmail =
                new AccountRequest("testgmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");

        assertThrows(InvalidParametersException.class,
                () -> accountRequestDb.updateAccountRequest(accountRequestWithInvalidEmail));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(accountRequestWithInvalidEmail), never());
    }

    @Test
    public void testUpdateAccountRequest_accountRequestDoesNotExist_throwsEntityDoesNotExistException() {
        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        doReturn(null).when(accountRequestDb).getAccountRequest(accountRequest.getId());

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestDb.updateAccountRequest(accountRequest));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(accountRequest), never());
    }

    @Test
    public void testUpdateAccountRequest_success() throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");
        doReturn(accountRequest).when(accountRequestDb).getAccountRequest(accountRequest.getId());
        mockHibernateUtil.when(() -> HibernateUtil.merge(accountRequest)).thenReturn(accountRequest);

        accountRequestDb.updateAccountRequest(accountRequest);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(accountRequest));
    }

    @Test
    public void testDeleteAccountRequest_success() {
        AccountRequest accountRequest =
                new AccountRequest("test@gmail.com", "name", "institute", AccountRequestStatus.PENDING, "comments");

        accountRequestDb.deleteAccountRequest(accountRequest);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(accountRequest));
    }

    @Test
    public void testSearchAccountRequestsInWholeSystem_emptyString_returnsEmptyList() throws SearchServiceException {
        String testQuery = "";
        doReturn(mockSearchManager).when(accountRequestDb).getSearchManager();

        List<AccountRequest> searchResult = accountRequestDb.searchAccountRequestsInWholeSystem(testQuery);
        assertTrue(searchResult.isEmpty());

        verify(mockSearchManager, never()).searchAccountRequests(testQuery);
    }

    @Test
    public void testSearchAccountRequestsInWholeSystem_success() throws SearchServiceException {
        String testQuery = "TEST";
        doReturn(mockSearchManager).when(accountRequestDb).getSearchManager();

        accountRequestDb.searchAccountRequestsInWholeSystem(testQuery);

        verify(mockSearchManager, times(1)).searchAccountRequests(testQuery);
    }
}

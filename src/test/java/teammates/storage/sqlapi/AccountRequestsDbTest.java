package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
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
    public void testCreateAccountRequest_accountRequestDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        doReturn(null).when(accountRequestDb).getAccountRequest(anyString(), anyString());

        accountRequestDb.createAccountRequest(accountRequest);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(accountRequest));
    }

    @Test
    public void testCreateAccountRequest_accountRequestAlreadyExists_throwsEntityAlreadyExistsException() {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        doReturn(new AccountRequest("test@gmail.com", "name", "institute"))
                .when(accountRequestDb).getAccountRequest(anyString(), anyString());

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> accountRequestDb.createAccountRequest(accountRequest));

        assertEquals(ex.getMessage(), "Trying to create an entity that exists: " + accountRequest.toString());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(accountRequest), never());
    }

    @Test
    public void testUpdateAccountRequest_invalidEmail_throwsInvalidParametersException() {
        AccountRequest accountRequestWithInvalidEmail = new AccountRequest("testgmail.com", "name", "institute");

        assertThrows(InvalidParametersException.class,
                () -> accountRequestDb.updateAccountRequest(accountRequestWithInvalidEmail));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(accountRequestWithInvalidEmail), never());
    }

    @Test
    public void testUpdateAccountRequest_accountRequestDoesNotExist_throwsEntityDoesNotExistException() {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        doReturn(null).when(accountRequestDb).getAccountRequest(anyString(), anyString());

        assertThrows(EntityDoesNotExistException.class,
                () -> accountRequestDb.updateAccountRequest(accountRequest));

        mockHibernateUtil.verify(() -> HibernateUtil.merge(accountRequest), never());
    }

    @Test
    public void testUpdateAccountRequest_success() throws InvalidParametersException, EntityDoesNotExistException {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        doReturn(accountRequest).when(accountRequestDb).getAccountRequest(anyString(), anyString());

        accountRequestDb.updateAccountRequest(accountRequest);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(accountRequest));
    }

    @Test
    public void testDeleteAccountRequest_success() {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");

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

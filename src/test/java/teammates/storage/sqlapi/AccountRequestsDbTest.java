package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code AccountRequestDb}.
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
    public void createAccountRequestDoesNotExist() throws InvalidParametersException, EntityAlreadyExistsException {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        doReturn(null).when(accountRequestDb).getAccountRequest(anyString(), anyString());

        accountRequestDb.createAccountRequest(accountRequest);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(accountRequest));
    }

    @Test
    public void createAccountRequestAlreadyExists() {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        doReturn(new AccountRequest("test@gmail.com", "name", "institute"))
                .when(accountRequestDb).getAccountRequest(anyString(), anyString());

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> accountRequestDb.createAccountRequest(accountRequest));

        assertEquals(ex.getMessage(), "Trying to create an entity that exists: " + accountRequest.toString());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(accountRequest), never());
    }

    @Test
    public void deleteAccountRequest() {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");

        accountRequestDb.deleteAccountRequest(accountRequest);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(accountRequest));
    }
}

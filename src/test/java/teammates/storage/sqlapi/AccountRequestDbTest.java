package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
public class AccountRequestDbTest extends BaseTestCase {

    private AccountRequestDb accountRequestDb;

    private Session session;

    @BeforeMethod
    public void setUp() {
        accountRequestDb = spy(AccountRequestDb.class);
        session = spy(Session.class);
        SessionFactory sessionFactory = spy(SessionFactory.class);

        HibernateUtil.setSessionFactory(sessionFactory);

        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void createAccountRequestDoesNotExist() throws InvalidParametersException, EntityAlreadyExistsException {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        doReturn(null).when(accountRequestDb).getAccountRequest(anyString(), anyString());
        accountRequestDb.createAccountRequest(accountRequest);

        verify(session, times(1)).persist(accountRequest);
    }

    @Test
    public void createAccountRequestAlreadyExists() {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        doReturn(new AccountRequest("test@gmail.com", "name", "institute"))
                .when(accountRequestDb).getAccountRequest(anyString(), anyString());

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> accountRequestDb.createAccountRequest(accountRequest));
        assertEquals(ex.getMessage(), "Trying to create an entity that exists: " + accountRequest.toString());
        verify(session, never()).persist(accountRequest);
    }

    @Test
    public void deleteAccountRequest() {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        AccountRequest returnedAccountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        doReturn(returnedAccountRequest).when(accountRequestDb).getAccountRequest(anyString(), anyString());

        accountRequestDb.deleteAccountRequest(accountRequest);

        verify(session, times(1)).remove(returnedAccountRequest);
    }
}

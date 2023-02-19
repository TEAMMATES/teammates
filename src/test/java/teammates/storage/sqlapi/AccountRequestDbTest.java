package teammates.storage.sqlapi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

    private AccountRequestDb accountRequestDb = AccountRequestDb.inst();

    private Session session;

    @BeforeMethod
    public void setUp() {
        session = mock(Session.class);
        SessionFactory sessionFactory = mock(SessionFactory.class);
        HibernateUtil.setSessionFactory(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void createAccountRequestDoesNotExist() throws InvalidParametersException, EntityAlreadyExistsException {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        when(session.get(AccountRequest.class, accountRequest.getId())).thenReturn(null);

        accountRequestDb.createAccountRequest(accountRequest);

        verify(session, times(1)).persist(accountRequest);
    }

    @Test
    public void createAccountRequestAlreadyExists() {
        AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
        when(session.get(AccountRequest.class, accountRequest.getId())).thenReturn(accountRequest);

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
            () -> accountRequestDb.createAccountRequest(accountRequest));
        assertEquals(ex.getMessage(), "Trying to create an entity that exists: " + accountRequest.toString());
        verify(session, never()).persist(accountRequest);
    }
}

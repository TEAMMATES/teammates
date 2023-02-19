// package teammates.storage.sqlapi;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.doReturn;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.stream.Stream;

// import org.hibernate.Session;
// import org.hibernate.SessionFactory;
// import org.testng.annotations.BeforeMethod;
// import org.testng.annotations.Test;

// import teammates.common.exception.EntityAlreadyExistsException;
// import teammates.common.exception.InvalidParametersException;
// import teammates.common.util.HibernateUtil;
// import teammates.storage.sqlentity.AccountRequest;
// import teammates.test.BaseTestCase;

// import jakarta.persistence.TypedQuery;
// import jakarta.persistence.criteria.CriteriaQuery;

/**
 * SUT: {@code AccountRequestDb}.
 */
// public class AccountRequestDbTest extends BaseTestCase {

//     private AccountRequestDb accountRequestDb = AccountRequestDb.inst();

//     private Session session;
//     private TypedQuery<AccountRequest> query;

//     @BeforeMethod
//     public void setUp() {
//         SessionFactory sessionFactory = mock(SessionFactory.class);
//         query = mock(TypedQuery.class);
//         session = mock(Session.class);

//         HibernateUtil.setSessionFactory(sessionFactory);
//         when(sessionFactory.getCurrentSession()).thenReturn(session);
//         doReturn(query).when(session).createQuery(any(CriteriaQuery.class));
//     }

//     @Test
//     public void createAccountRequestDoesNotExist() throws InvalidParametersException, EntityAlreadyExistsException {
//         AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
//         when(query.getResultStream()).thenReturn(Stream.empty());

//         accountRequestDb.createAccountRequest(accountRequest);

//         verify(session, times(1)).persist(accountRequest);
//     }

//     @Test
//     public void createAccountRequestAlreadyExists() {
//         AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
//         when(query.getResultStream()).thenReturn(Stream.of(accountRequest));

//         EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
//             () -> accountRequestDb.createAccountRequest(accountRequest));
//         assertEquals(ex.getMessage(), "Trying to create an entity that exists: " + accountRequest.toString());
//         verify(session, never()).persist(accountRequest);
//     }

//     @Test
//     public void deleteAccountRequest() {
//         AccountRequest accountRequest = new AccountRequest("test@gmail.com", "name", "institute");
//         when(query.getResultStream()).thenReturn(Stream.of(accountRequest));

//         accountRequestDb.deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());

//         verify(session, times(1)).delete(accountRequest);
//     }
// }

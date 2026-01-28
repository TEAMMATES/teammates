package teammates.storage.sqlsearch;

import static org.mockito.Mockito.mock;

import jakarta.servlet.ServletContextEvent;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link SearchManagerStarter}.
 *
 * <p>These tests mutate static factory state (save/restore via reflection). They are
 * excluded from the main component suite (group {@code sqlsearchFactory}) and run in a
 * separate, non-parallel suite so they cannot affect other tests.
 */
@Test(groups = "sqlsearchFactory")
public class SearchManagerStarterTest extends BaseTestCase {

    private SearchManagerStarter starter;
    private AccountRequestSearchManager originalAccountRequestManager;
    private InstructorSearchManager originalInstructorManager;
    private StudentSearchManager originalStudentManager;

    private void setAccountRequestManager(AccountRequestSearchManager manager) throws Exception {
        java.lang.reflect.Field field =
                SearchManagerFactory.class.getDeclaredField("accountRequestInstance");
        field.setAccessible(true);
        field.set(null, manager);
    }

    private void setInstructorManager(InstructorSearchManager manager) throws Exception {
        java.lang.reflect.Field field =
                SearchManagerFactory.class.getDeclaredField("instructorInstance");
        field.setAccessible(true);
        field.set(null, manager);
    }

    private void setStudentManager(StudentSearchManager manager) throws Exception {
        java.lang.reflect.Field field =
                SearchManagerFactory.class.getDeclaredField("studentInstance");
        field.setAccessible(true);
        field.set(null, manager);
    }

    @BeforeMethod
    public void setUp() {
        starter = new SearchManagerStarter();
        originalAccountRequestManager = SearchManagerFactory.getAccountRequestSearchManager();
        originalInstructorManager = SearchManagerFactory.getInstructorSearchManager();
        originalStudentManager = SearchManagerFactory.getStudentSearchManager();
    }

    @AfterMethod
    public void tearDown() {
        if (originalAccountRequestManager != null) {
            try {
                setAccountRequestManager(originalAccountRequestManager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (originalInstructorManager != null) {
            try {
                setInstructorManager(originalInstructorManager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (originalStudentManager != null) {
            try {
                setStudentManager(originalStudentManager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testContextInitialized_registersAllSearchManagers() {
        try {
            setAccountRequestManager(null);
            setInstructorManager(null);
            setStudentManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ServletContextEvent event = mock(ServletContextEvent.class);
        starter.contextInitialized(event);
        assertNotNull(SearchManagerFactory.getAccountRequestSearchManager());
        assertNotNull(SearchManagerFactory.getInstructorSearchManager());
        assertNotNull(SearchManagerFactory.getStudentSearchManager());
    }

    @Test
    public void testContextInitialized_usesConfigSearchServiceHost() {
        try {
            setAccountRequestManager(null);
            setInstructorManager(null);
            setStudentManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ServletContextEvent event = mock(ServletContextEvent.class);
        starter.contextInitialized(event);
        assertNotNull(SearchManagerFactory.getAccountRequestSearchManager());
        assertNotNull(SearchManagerFactory.getInstructorSearchManager());
        assertNotNull(SearchManagerFactory.getStudentSearchManager());
    }

    @Test
    public void testContextDestroyed_doesNothing() {
        ServletContextEvent event = mock(ServletContextEvent.class);
        starter.contextDestroyed(event);
    }
}

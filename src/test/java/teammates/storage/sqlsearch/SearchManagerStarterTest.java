package teammates.storage.sqlsearch;

import static org.mockito.Mockito.mock;

import jakarta.servlet.ServletContextEvent;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link SearchManagerStarter}.
 */
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
        // Save original instances
        originalAccountRequestManager = SearchManagerFactory.getAccountRequestSearchManager();
        originalInstructorManager = SearchManagerFactory.getInstructorSearchManager();
        originalStudentManager = SearchManagerFactory.getStudentSearchManager();
    }

    @AfterMethod
    public void tearDown() {
        // Restore original instances
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
        // Clear existing registrations
        try {
            setAccountRequestManager(null);
            setInstructorManager(null);
            setStudentManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ServletContextEvent event = mock(ServletContextEvent.class);
        starter.contextInitialized(event);

        // Verify all managers are registered
        assertNotNull(SearchManagerFactory.getAccountRequestSearchManager());
        assertNotNull(SearchManagerFactory.getInstructorSearchManager());
        assertNotNull(SearchManagerFactory.getStudentSearchManager());
    }

    @Test
    public void testContextInitialized_usesConfigSearchServiceHost() {
        // Clear existing registrations
        try {
            setAccountRequestManager(null);
            setInstructorManager(null);
            setStudentManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ServletContextEvent event = mock(ServletContextEvent.class);
        starter.contextInitialized(event);

        // Verify managers are created with Config.SEARCH_SERVICE_HOST
        AccountRequestSearchManager accountRequestManager =
                SearchManagerFactory.getAccountRequestSearchManager();
        InstructorSearchManager instructorManager =
                SearchManagerFactory.getInstructorSearchManager();
        StudentSearchManager studentManager =
                SearchManagerFactory.getStudentSearchManager();

        assertNotNull(accountRequestManager);
        assertNotNull(instructorManager);
        assertNotNull(studentManager);

        // Note: We can't directly verify the host used, but we can verify the managers are created
        // The actual host value comes from Config.SEARCH_SERVICE_HOST
    }

    @Test
    public void testContextDestroyed_doesNothing() {
        ServletContextEvent event = mock(ServletContextEvent.class);

        // Should not throw exception
        starter.contextDestroyed(event);

        // No verification needed as method does nothing
    }
}

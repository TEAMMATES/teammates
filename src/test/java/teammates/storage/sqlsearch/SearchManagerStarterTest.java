package teammates.storage.sqlsearch;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import jakarta.servlet.ServletContextEvent;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link SearchManagerStarter}.
 *
 * <p>These tests mutate static factory state (save/restore via reflection) and run with
 * the component suite. Tear-down restores the original factory state after each test.
 */
public class SearchManagerStarterTest extends BaseTestCase {

    private SearchManagerStarter starter;
    private AccountRequestSearchManager originalAccountRequestManager;
    private InstructorSearchManager originalInstructorManager;
    private StudentSearchManager originalStudentManager;

    private void setFactoryField(String fieldName, Object value) {
        try {
            Field field = SearchManagerFactory.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setAccountRequestManager(AccountRequestSearchManager manager) {
        setFactoryField("accountRequestInstance", manager);
    }

    private void setInstructorManager(InstructorSearchManager manager) {
        setFactoryField("instructorInstance", manager);
    }

    private void setStudentManager(StudentSearchManager manager) {
        setFactoryField("studentInstance", manager);
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
            setAccountRequestManager(originalAccountRequestManager);
        }
        if (originalInstructorManager != null) {
            setInstructorManager(originalInstructorManager);
        }
        if (originalStudentManager != null) {
            setStudentManager(originalStudentManager);
        }
    }

    @Test
    public void testContextInitialized_registersAllSearchManagers() {
        setAccountRequestManager(null);
        setInstructorManager(null);
        setStudentManager(null);
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

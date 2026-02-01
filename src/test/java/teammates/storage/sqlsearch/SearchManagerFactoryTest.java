package teammates.storage.sqlsearch;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.storage.sqlapi.AccountRequestsDb;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.UsersDb;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link SearchManagerFactory}.
 *
 * <p>These tests mutate static factory state (save/restore via reflection) and run with
 * the component suite. Tear-down restores the original factory state after each test.
 */
public class SearchManagerFactoryTest extends BaseTestCase {

    private AccountRequestSearchManager originalAccountRequestManager;
    private InstructorSearchManager originalInstructorManager;
    private StudentSearchManager originalStudentManager;

    private AccountRequestSearchManager createAccountRequestManager() {
        return new AccountRequestSearchManager(null, mock(AccountRequestsDb.class), false);
    }

    private InstructorSearchManager createInstructorManager() {
        return new InstructorSearchManager(null, mock(CoursesDb.class), mock(UsersDb.class), false);
    }

    private StudentSearchManager createStudentManager() {
        return new StudentSearchManager(null, mock(CoursesDb.class), mock(UsersDb.class), false);
    }

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
    public void testRegisterAccountRequestSearchManager_registersInstance() {
        AccountRequestSearchManager manager = createAccountRequestManager();
        setAccountRequestManager(null);
        SearchManagerFactory.registerAccountRequestSearchManager(manager);
        assertEquals(SearchManagerFactory.getAccountRequestSearchManager(), manager);
    }

    @Test
    public void testRegisterAccountRequestSearchManager_doesNotOverwriteExisting() {
        AccountRequestSearchManager firstManager = createAccountRequestManager();
        AccountRequestSearchManager secondManager = createAccountRequestManager();
        setAccountRequestManager(null);
        SearchManagerFactory.registerAccountRequestSearchManager(firstManager);
        SearchManagerFactory.registerAccountRequestSearchManager(secondManager);
        assertEquals(SearchManagerFactory.getAccountRequestSearchManager(), firstManager);
    }

    @Test
    public void testSetAccountRequestSearchManager_overwritesExisting() {
        AccountRequestSearchManager firstManager = createAccountRequestManager();
        AccountRequestSearchManager secondManager = createAccountRequestManager();
        setAccountRequestManager(firstManager);
        setAccountRequestManager(secondManager);
        assertEquals(SearchManagerFactory.getAccountRequestSearchManager(), secondManager);
    }

    @Test
    public void testRegisterInstructorSearchManager_registersInstance() {
        InstructorSearchManager manager = createInstructorManager();
        setInstructorManager(null);
        SearchManagerFactory.registerInstructorSearchManager(manager);
        assertEquals(SearchManagerFactory.getInstructorSearchManager(), manager);
    }

    @Test
    public void testRegisterInstructorSearchManager_doesNotOverwriteExisting() {
        InstructorSearchManager firstManager = createInstructorManager();
        InstructorSearchManager secondManager = createInstructorManager();
        setInstructorManager(null);
        SearchManagerFactory.registerInstructorSearchManager(firstManager);
        SearchManagerFactory.registerInstructorSearchManager(secondManager);
        assertEquals(SearchManagerFactory.getInstructorSearchManager(), firstManager);
    }

    @Test
    public void testSetInstructorSearchManager_overwritesExisting() {
        InstructorSearchManager firstManager = createInstructorManager();
        InstructorSearchManager secondManager = createInstructorManager();
        setInstructorManager(firstManager);
        setInstructorManager(secondManager);
        assertEquals(SearchManagerFactory.getInstructorSearchManager(), secondManager);
    }

    @Test
    public void testRegisterStudentSearchManager_registersInstance() {
        StudentSearchManager manager = createStudentManager();
        setStudentManager(null);
        SearchManagerFactory.registerStudentSearchManager(manager);
        assertEquals(SearchManagerFactory.getStudentSearchManager(), manager);
    }

    @Test
    public void testRegisterStudentSearchManager_doesNotOverwriteExisting() {
        StudentSearchManager firstManager = createStudentManager();
        StudentSearchManager secondManager = createStudentManager();
        setStudentManager(null);
        SearchManagerFactory.registerStudentSearchManager(firstManager);
        SearchManagerFactory.registerStudentSearchManager(secondManager);
        assertEquals(SearchManagerFactory.getStudentSearchManager(), firstManager);
    }

    @Test
    public void testSetStudentSearchManager_overwritesExisting() {
        StudentSearchManager firstManager = createStudentManager();
        StudentSearchManager secondManager = createStudentManager();
        setStudentManager(firstManager);
        setStudentManager(secondManager);
        assertEquals(SearchManagerFactory.getStudentSearchManager(), secondManager);
    }

    @Test
    public void testGetAccountRequestSearchManager_whenNotRegistered_returnsNull() {
        setAccountRequestManager(null);
        assertNull(SearchManagerFactory.getAccountRequestSearchManager());
    }

    @Test
    public void testGetInstructorSearchManager_whenNotRegistered_returnsNull() {
        setInstructorManager(null);
        assertNull(SearchManagerFactory.getInstructorSearchManager());
    }

    @Test
    public void testGetStudentSearchManager_whenNotRegistered_returnsNull() {
        setStudentManager(null);
        assertNull(SearchManagerFactory.getStudentSearchManager());
    }
}

package teammates.storage.sqlsearch;

import static org.mockito.Mockito.mock;

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
 * <p>These tests mutate static factory state (save/restore via reflection). They are
 * excluded from the main component suite (group {@code sqlsearchFactory}) and run in a
 * separate, non-parallel suite so they cannot affect other tests.
 */
@Test(groups = "sqlsearchFactory")
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
    public void testRegisterAccountRequestSearchManager_registersInstance() {
        AccountRequestSearchManager manager = createAccountRequestManager();
        try {
            setAccountRequestManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SearchManagerFactory.registerAccountRequestSearchManager(manager);
        assertEquals(SearchManagerFactory.getAccountRequestSearchManager(), manager);
    }

    @Test
    public void testRegisterAccountRequestSearchManager_doesNotOverwriteExisting() {
        AccountRequestSearchManager firstManager = createAccountRequestManager();
        AccountRequestSearchManager secondManager = createAccountRequestManager();
        try {
            setAccountRequestManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SearchManagerFactory.registerAccountRequestSearchManager(firstManager);
        SearchManagerFactory.registerAccountRequestSearchManager(secondManager);
        assertEquals(SearchManagerFactory.getAccountRequestSearchManager(), firstManager);
    }

    @Test
    public void testSetAccountRequestSearchManager_overwritesExisting() {
        AccountRequestSearchManager firstManager = createAccountRequestManager();
        AccountRequestSearchManager secondManager = createAccountRequestManager();
        try {
            setAccountRequestManager(firstManager);
            setAccountRequestManager(secondManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(SearchManagerFactory.getAccountRequestSearchManager(), secondManager);
    }

    @Test
    public void testRegisterInstructorSearchManager_registersInstance() {
        InstructorSearchManager manager = createInstructorManager();
        try {
            setInstructorManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SearchManagerFactory.registerInstructorSearchManager(manager);
        assertEquals(SearchManagerFactory.getInstructorSearchManager(), manager);
    }

    @Test
    public void testRegisterInstructorSearchManager_doesNotOverwriteExisting() {
        InstructorSearchManager firstManager = createInstructorManager();
        InstructorSearchManager secondManager = createInstructorManager();
        try {
            setInstructorManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SearchManagerFactory.registerInstructorSearchManager(firstManager);
        SearchManagerFactory.registerInstructorSearchManager(secondManager);
        assertEquals(SearchManagerFactory.getInstructorSearchManager(), firstManager);
    }

    @Test
    public void testSetInstructorSearchManager_overwritesExisting() {
        InstructorSearchManager firstManager = createInstructorManager();
        InstructorSearchManager secondManager = createInstructorManager();
        try {
            setInstructorManager(firstManager);
            setInstructorManager(secondManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(SearchManagerFactory.getInstructorSearchManager(), secondManager);
    }

    @Test
    public void testRegisterStudentSearchManager_registersInstance() {
        StudentSearchManager manager = createStudentManager();
        try {
            setStudentManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SearchManagerFactory.registerStudentSearchManager(manager);
        assertEquals(SearchManagerFactory.getStudentSearchManager(), manager);
    }

    @Test
    public void testRegisterStudentSearchManager_doesNotOverwriteExisting() {
        StudentSearchManager firstManager = createStudentManager();
        StudentSearchManager secondManager = createStudentManager();
        try {
            setStudentManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SearchManagerFactory.registerStudentSearchManager(firstManager);
        SearchManagerFactory.registerStudentSearchManager(secondManager);
        assertEquals(SearchManagerFactory.getStudentSearchManager(), firstManager);
    }

    @Test
    public void testSetStudentSearchManager_overwritesExisting() {
        StudentSearchManager firstManager = createStudentManager();
        StudentSearchManager secondManager = createStudentManager();
        try {
            setStudentManager(firstManager);
            setStudentManager(secondManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(SearchManagerFactory.getStudentSearchManager(), secondManager);
    }

    @Test
    public void testGetAccountRequestSearchManager_whenNotRegistered_returnsNull() {
        try {
            setAccountRequestManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertNull(SearchManagerFactory.getAccountRequestSearchManager());
    }

    @Test
    public void testGetInstructorSearchManager_whenNotRegistered_returnsNull() {
        try {
            setInstructorManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertNull(SearchManagerFactory.getInstructorSearchManager());
    }

    @Test
    public void testGetStudentSearchManager_whenNotRegistered_returnsNull() {
        try {
            setStudentManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertNull(SearchManagerFactory.getStudentSearchManager());
    }
}

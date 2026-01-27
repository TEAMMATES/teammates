package teammates.storage.sqlsearch;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link SearchManagerFactory}.
 */
public class SearchManagerFactoryTest extends BaseTestCase {

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
    public void testRegisterAccountRequestSearchManager_registersInstance() {
        AccountRequestSearchManager manager = new AccountRequestSearchManager("", false);

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
        AccountRequestSearchManager firstManager = new AccountRequestSearchManager("", false);
        AccountRequestSearchManager secondManager = new AccountRequestSearchManager("", false);

        try {
            setAccountRequestManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SearchManagerFactory.registerAccountRequestSearchManager(firstManager);
        SearchManagerFactory.registerAccountRequestSearchManager(secondManager);

        // First registration should persist
        assertEquals(SearchManagerFactory.getAccountRequestSearchManager(), firstManager);
    }

    @Test
    public void testSetAccountRequestSearchManager_overwritesExisting() {
        AccountRequestSearchManager firstManager = new AccountRequestSearchManager("", false);
        AccountRequestSearchManager secondManager = new AccountRequestSearchManager("", false);

        try {
            setAccountRequestManager(firstManager);
            setAccountRequestManager(secondManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Setter should overwrite
        assertEquals(SearchManagerFactory.getAccountRequestSearchManager(), secondManager);
    }

    @Test
    public void testRegisterInstructorSearchManager_registersInstance() {
        InstructorSearchManager manager = new InstructorSearchManager("", false);

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
        InstructorSearchManager firstManager = new InstructorSearchManager("", false);
        InstructorSearchManager secondManager = new InstructorSearchManager("", false);

        try {
            setInstructorManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SearchManagerFactory.registerInstructorSearchManager(firstManager);
        SearchManagerFactory.registerInstructorSearchManager(secondManager);

        // First registration should persist
        assertEquals(SearchManagerFactory.getInstructorSearchManager(), firstManager);
    }

    @Test
    public void testSetInstructorSearchManager_overwritesExisting() {
        InstructorSearchManager firstManager = new InstructorSearchManager("", false);
        InstructorSearchManager secondManager = new InstructorSearchManager("", false);

        try {
            setInstructorManager(firstManager);
            setInstructorManager(secondManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Setter should overwrite
        assertEquals(SearchManagerFactory.getInstructorSearchManager(), secondManager);
    }

    @Test
    public void testRegisterStudentSearchManager_registersInstance() {
        StudentSearchManager manager = new StudentSearchManager("", false);

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
        StudentSearchManager firstManager = new StudentSearchManager("", false);
        StudentSearchManager secondManager = new StudentSearchManager("", false);

        try {
            setStudentManager(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SearchManagerFactory.registerStudentSearchManager(firstManager);
        SearchManagerFactory.registerStudentSearchManager(secondManager);

        // First registration should persist
        assertEquals(SearchManagerFactory.getStudentSearchManager(), firstManager);
    }

    @Test
    public void testSetStudentSearchManager_overwritesExisting() {
        StudentSearchManager firstManager = new StudentSearchManager("", false);
        StudentSearchManager secondManager = new StudentSearchManager("", false);

        try {
            setStudentManager(firstManager);
            setStudentManager(secondManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Setter should overwrite
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

package teammates.test.cases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.Common;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.storage.api.AccountsDb;
import teammates.storage.datastore.Datastore;

public class AccountsDbTest extends BaseTestCase {

	private AccountsDb accountsDb = new AccountsDb();
	private static LocalServiceTestHelper helper;
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(AccountsDb.class);
		Datastore.initialize();
		LocalDatastoreServiceTestConfig localDatastore = new LocalDatastoreServiceTestConfig();
		helper = new LocalServiceTestHelper(localDatastore);
		helper.setUp();
	}

	@SuppressWarnings("unused")
	private void ____STUDENT_________________________________________() {
	}
	@Test
	public void testCreateStudent() throws EntityAlreadyExistsException {
		// SUCCESS
		StudentData s = new StudentData();
		s.name = "valid student";
		s.course = "valid-course";
		s.email = "valid-fresh@email.com";
		accountsDb.createStudent(s);
			
		// FAIL : duplicate
		try {
			accountsDb.createStudent(s);
			fail();
		} catch (EntityAlreadyExistsException e) {
			assertContains(AccountsDb.ERROR_CREATE_STUDENT_ALREADY_EXISTS, e.getMessage());
		}
		
		// FAIL : invalid params
		s.course = "invalid id space";
		try {
			accountsDb.createStudent(s);
			fail();
		} catch (AssertionError a) {
			assertEquals(a.getMessage(), StudentData.ERROR_FIELD_COURSE);
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
		
		// Null params check:
		try {
			accountsDb.createStudent(null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetStudent() {
		StudentData s = createNewStudent();
		
		// Get existent
		StudentData retrieved = accountsDb.getStudent(s.course, s.email);
		assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = accountsDb.getStudent("any-course-id", "non-existent@email.com");
		assertNull(retrieved);
		
		// Null params check:
		try {
			accountsDb.getStudent(null, "valid@email.com");
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			accountsDb.getStudent("any-course-id", null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testEditStudent() {
		StudentData s = createNewStudent();
		
		// Edit existent
		accountsDb.editStudent(s.course, s.email, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet", new Text("new profile"));
		
		// Edit non-existent
		try {
			accountsDb.editStudent("non-existent-course", "non@existent.email", "no-name", "non-existent-team", "non.existent.ID", "blah", "blah", new Text("blah"));
			fail();
		} catch (AssertionError a) {
			assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT, a.getMessage());
		}
		
		// Null params check:
		// Only check first 2 params (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
		try {
			accountsDb.editStudent(null, s.email, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet", new Text("new profile"));
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		try {
			accountsDb.editStudent(s.course, null, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet", new Text("new profile"));
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testDeleteStudent() {
		StudentData s = createNewStudent();
		
		// Delete
		accountsDb.deleteStudent(s.course, s.email);
		
		StudentData deleted = accountsDb.getStudent(s.course, s.email);
		assertNull(deleted);
		
		// delete again - should fail silently
		accountsDb.deleteStudent(s.course, s.email);
		
		// Null params check:
		try {
			accountsDb.deleteStudent(null, s.email);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			accountsDb.deleteStudent(s.course, null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@SuppressWarnings("unused")
	private void ____INSTRUCTOR_________________________________________() {
	}
	
	@Test
	public void testCreateInstructor() throws EntityAlreadyExistsException {
		// SUCCESS
		InstructorData c = new InstructorData();
		c.id = "valid.fresh.id";
		c.name = "John Doe";
		c.email = "john.doe@instructor.com";
		accountsDb.createInstructor(c);
		
		// FAIL : duplicate
		try {
			accountsDb.createInstructor(c);
			fail();
		} catch (EntityAlreadyExistsException e) {
			assertContains(AccountsDb.ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS, e.getMessage());
		}
		
		// FAIL : invalid params
		c.id = "invalid id with spaces";
		try {
			accountsDb.createInstructor(c);
			fail();
		} catch (AssertionError a) {
			assertEquals(a.getMessage(), InstructorData.ERROR_FIELD_ID);
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
		
		// Null params check:
		try {
			accountsDb.createInstructor(null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetInstructor() {
		InstructorData c = createNewInstructor();
		
		// Get existent
		InstructorData retrieved = accountsDb.getInstructor(c.id);
		assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = accountsDb.getInstructor("non.existent");
		assertNull(retrieved);
		
		// Null params check:
		try {
			accountsDb.getInstructor(null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testEditInstructor() {
		// Not implemented
	}
	
	@Test
	public void testDeleteInstructor() {
		InstructorData c = createNewInstructor();
		
		// Delete
		accountsDb.deleteInstructor(c.id);
		
		InstructorData deleted = accountsDb.getInstructor(c.id);
		assertNull(deleted);
		
		// delete again - should fail silently
		accountsDb.deleteInstructor(c.id);
		
		// Null params check:
		try {
			accountsDb.deleteInstructor(null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(AccountsDb.class);
		helper.tearDown();
	}
	
	private StudentData createNewStudent() {
		StudentData s = new StudentData();
		s.name = "valid student";
		s.course = "valid-course";
		s.email = "valid@email.com";
		
		try {
			accountsDb.createStudent(s);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		return s;
	}
	
	private InstructorData createNewInstructor() {
		InstructorData c = new InstructorData();
		c.id = "valid.id";
		c.name = "John Doe";
		c.email = "john.doe@instructor.com";
		
		try {
			accountsDb.createInstructor(c);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		return c;
	}
}

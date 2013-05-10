package teammates.test.cases;

import static teammates.common.FieldValidator.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.AssertJUnit;
import static org.testng.AssertJUnit.*;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.Common;
import static teammates.common.Common.EOL;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
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
	private void ____ACCOUNT_________________________________________() {
	}
	@Test
	public void testCreateAccount() {
		// SUCCESS
		AccountAttributes a = new AccountAttributes();
		a.googleId = "test.account";
		a.name = "Test account Name";
		a.isInstructor = false;
		a.email = "fresh-account@email.com";
		a.institute = "National University of Singapore";
		accountsDb.createAccount(a);
			
		// SUCCESS : duplicate
		accountsDb.createAccount(a);
		
		// Test for latest entry persistence
		AccountAttributes accountDataTest = accountsDb.getAccount(a.googleId);
		AssertJUnit.assertFalse(accountDataTest.isInstructor);
		// Change a field
		accountDataTest.isInstructor = true;
		accountsDb.updateAccount(accountDataTest);
		// Re-retrieve
		accountDataTest = accountsDb.getAccount(a.googleId);
		AssertJUnit.assertTrue(accountDataTest.isInstructor);
		
		// FAIL : invalid parameters
		// Should we not allow empty fields?
		/*
		a.email = "invalid email";
		try {
			accountsDb.createAccount(a);
			fail();
		} catch (AssertionError a) {
			assertEquals(a.getMessage(), AccountData.ERROR_FIELD_EMAIL);
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
		*/
		
		// Null parameters check:
		try {
			accountsDb.createAccount(null);
			Assert.fail();
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, ae.getMessage());
		}
	}
	
	@Test
	public void testGetAccount() {
		AccountAttributes a = createNewAccount();
		
		// Get existent
		AccountAttributes retrieved = accountsDb.getAccount(a.googleId);
		AssertJUnit.assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = accountsDb.getAccount("non.existent");
		AssertJUnit.assertNull(retrieved);
		
		// Null params check:
		try {
			accountsDb.getAccount(null);
			Assert.fail();
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, ae.getMessage());
		}
	}
	
	@Test
	public void testEditAccount() {
		AccountAttributes a = createNewAccount();
		
		// Edit existent
		a.name = "Edited name";
		accountsDb.updateAccount(a);
		
		// Edit non-existent
		try {
			a.googleId = "non.existent";
			accountsDb.updateAccount(a);
			Assert.fail();
		} catch (AssertionError ae) {
			assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT_ACCOUNT, ae.getMessage());
		}
		
		// Null parameters check:
		// Only check first 2 parameters (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
		try {
			accountsDb.updateAccount(null);
			Assert.fail();
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, ae.getMessage());
		}
	}
	
	@Test
	public void testDeleteAccount() {
		AccountAttributes a = createNewAccount();
		
		// Delete
		accountsDb.deleteAccount(a.googleId);
		
		AccountAttributes deleted = accountsDb.getAccount(a.googleId);
		AssertJUnit.assertNull(deleted);
		
		// delete again - should fail silently
		accountsDb.deleteAccount(a.googleId);
		
		// Null parameters check:
		try {
			accountsDb.deleteAccount(null);
			Assert.fail();
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, ae.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private void ____STUDENT_________________________________________() {
	}
	@Test
	public void testCreateStudent() throws EntityAlreadyExistsException {
		// SUCCESS
		StudentAttributes s = new StudentAttributes();
		s.name = "valid student";
		s.course = "valid-course";
		s.email = "valid-fresh@email.com";
		s.team = "";
		s.comments="";
		s.id="";
		accountsDb.createStudent(s);
			
		// FAIL : duplicate
		try {
			accountsDb.createStudent(s);
			Assert.fail();
		} catch (EntityAlreadyExistsException e) {
			assertContains(AccountsDb.ERROR_CREATE_STUDENT_ALREADY_EXISTS, e.getMessage());
		}
		
		// FAIL : invalid params
		s.course = "invalid id space";
		try {
			accountsDb.createStudent(s);
			Assert.fail();
		} catch (AssertionError a) {
			assertTrue(true); //expected
		} 
		
		// Null params check:
		try {
			accountsDb.createStudent(null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetStudent() {
		StudentAttributes s = createNewStudent();
		
		// Get existent
		StudentAttributes retrieved = accountsDb.getStudent(s.course, s.email);
		AssertJUnit.assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = accountsDb.getStudent("any-course-id", "non-existent@email.com");
		AssertJUnit.assertNull(retrieved);
		
		// Null params check:
		try {
			accountsDb.getStudent(null, "valid@email.com");
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			accountsDb.getStudent("any-course-id", null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testEditStudent() {
		StudentAttributes s = createNewStudent();
		
		// Edit existent
		accountsDb.updateStudent(s.course, s.email, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
		
		// Edit non-existent
		try {
			accountsDb.updateStudent("non-existent-course", "non@existent.email", "no-name", "non-existent-team", "non.existent.ID", "blah", "blah");
			Assert.fail();
		} catch (AssertionError a) {
			assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT_STUDENT, a.getMessage());
		}
		
		// Null params check:
		// Only check first 2 params (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
		try {
			accountsDb.updateStudent(null, s.email, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		try {
			accountsDb.updateStudent(s.course, null, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testDeleteStudent() {
		StudentAttributes s = createNewStudent();
		
		// Delete
		accountsDb.deleteStudent(s.course, s.email);
		
		StudentAttributes deleted = accountsDb.getStudent(s.course, s.email);
		AssertJUnit.assertNull(deleted);
		
		// delete again - should fail silently
		accountsDb.deleteStudent(s.course, s.email);
		
		// Null params check:
		try {
			accountsDb.deleteStudent(null, s.email);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			accountsDb.deleteStudent(s.course, null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@SuppressWarnings("unused")
	private void ____INSTRUCTOR_________________________________________() {
	}
	
	@Test
	public void testCreateInstructor() throws EntityAlreadyExistsException {
		// SUCCESS
		InstructorAttributes i = new InstructorAttributes();
		i.googleId = "valid.fresh.id";
		i.courseId = "valid.course.Id";
		i.name = "valid.name";
		i.email = "valid@email.com";
		accountsDb.createInstructor(i);
		
		// FAIL : duplicate
		try {
			accountsDb.createInstructor(i);
			Assert.fail();
		} catch (EntityAlreadyExistsException e) {
			assertContains(AccountsDb.ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS, e.getMessage());
		}
		
		// FAIL : invalid params
		i.googleId = "invalid id with spaces";
		try {
			accountsDb.createInstructor(i);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(
					a.getMessage(), 
					String.format(GOOGLE_ID_ERROR_MESSAGE, i.googleId, REASON_INCORRECT_FORMAT));
		} catch (EntityAlreadyExistsException e) {
			Assert.fail();
		}
		
		// Null params check:
		try {
			accountsDb.createInstructor(null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetInstructor() {
		InstructorAttributes i = createNewInstructor();
		
		// Get existent
		InstructorAttributes retrieved = accountsDb.getInstructor(i.googleId, i.courseId);
		AssertJUnit.assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = accountsDb.getInstructor("non.existent", "non.existent.course");
		AssertJUnit.assertNull(retrieved);
		
		// Null params check:
		try {
			accountsDb.getInstructor(null, null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testUpdateInstructor() {
		InstructorAttributes instructorToEdit = createNewInstructor();
		
		// SUCCESS
		// Test for old value
		AssertJUnit.assertEquals("valid.name", instructorToEdit.name);
		AssertJUnit.assertEquals("valid@email.com", instructorToEdit.email);
		
		// instructorToEdit is already inside, we can just edit and test
		instructorToEdit.name = "My New Name";
		instructorToEdit.email = "new@email.com";
		accountsDb.updateInstructor(instructorToEdit);
		
		// Re-retrieve
		instructorToEdit = accountsDb.getInstructor(instructorToEdit.googleId, instructorToEdit.courseId);
		AssertJUnit.assertEquals("My New Name", instructorToEdit.name);
		AssertJUnit.assertEquals("new@email.com", instructorToEdit.email);
		
		// FAIL : invalid parameters
		instructorToEdit.name = "";
		instructorToEdit.email = "aaa";
		try {
			accountsDb.updateInstructor(instructorToEdit);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(
					String.format(PERSON_NAME_ERROR_MESSAGE, instructorToEdit.name,	REASON_EMPTY) + EOL + 
					String.format(EMAIL_ERROR_MESSAGE, instructorToEdit.email,	REASON_INCORRECT_FORMAT), 
					a.getMessage());
		}
		
		// Null parameters check:
		try {
			accountsDb.updateInstructor(null);
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, ae.getMessage());
		}
	}
	
	@Test
	public void testDeleteInstructor() {
		InstructorAttributes i = createNewInstructor();
		
		// Delete
		accountsDb.deleteInstructor(i.googleId, i.courseId);
		
		InstructorAttributes deleted = accountsDb.getInstructor(i.googleId, i.courseId);
		AssertJUnit.assertNull(deleted);
		
		// delete again - should fail silently
		accountsDb.deleteInstructor(i.googleId, i.courseId);
		
		// Null params check:
		try {
			accountsDb.deleteInstructor(null, null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(AccountsDb.class);
		helper.tearDown();
	}
	
	private AccountAttributes createNewAccount() {
		AccountAttributes a = new AccountAttributes();
		a.googleId = "valid.googleId";
		a.name = "Valid Fresh Account";
		a.isInstructor = false;
		a.email = "valid@email.com";
		a.institute = "National University of Singapore";
		
		accountsDb.createAccount(a);
		return a;
	}
	
	private StudentAttributes createNewStudent() {
		StudentAttributes s = new StudentAttributes();
		s.name = "valid student";
		s.course = "valid-course";
		s.email = "valid@email.com";
		s.team = "";
		s.comments = "";
		s.id="";
		try {
			accountsDb.createStudent(s);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		return s;
	}
	
	private InstructorAttributes createNewInstructor() {
		InstructorAttributes c = new InstructorAttributes();
		c.googleId = "valid.id";
		c.courseId = "valid.course";
		c.name = "valid.name";
		c.email = "valid@email.com";
				
		try {
			accountsDb.createInstructor(c);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		return c;
	}
}

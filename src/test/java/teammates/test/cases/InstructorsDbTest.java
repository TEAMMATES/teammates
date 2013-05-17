package teammates.test.cases;

import static teammates.common.Common.EOL;
import static teammates.common.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.FieldValidator.GOOGLE_ID_ERROR_MESSAGE;
import static teammates.common.FieldValidator.PERSON_NAME_ERROR_MESSAGE;
import static teammates.common.FieldValidator.REASON_EMPTY;
import static teammates.common.FieldValidator.REASON_INCORRECT_FORMAT;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.datastore.Datastore;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class InstructorsDbTest extends BaseTestCase {

	private InstructorsDb instructorsDb = new InstructorsDb();
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
	
	@Test
	public void testCreateInstructor() throws EntityAlreadyExistsException {
		// SUCCESS
		InstructorAttributes i = new InstructorAttributes();
		i.googleId = "valid.fresh.id";
		i.courseId = "valid.course.Id";
		i.name = "valid.name";
		i.email = "valid@email.com";
		instructorsDb.createInstructor(i);
		
		// FAIL : duplicate
		try {
			instructorsDb.createInstructor(i);
			Assert.fail();
		} catch (EntityAlreadyExistsException e) {
			assertContains(AccountsDb.ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS, e.getMessage());
		}
		
		// FAIL : invalid params
		i.googleId = "invalid id with spaces";
		try {
			instructorsDb.createInstructor(i);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(
				"Invalid object received as a parameter :"+String.format(GOOGLE_ID_ERROR_MESSAGE, i.googleId, REASON_INCORRECT_FORMAT),
				a.getMessage()); 
		} catch (EntityAlreadyExistsException e) {
			Assert.fail();
		}
		
		// Null params check:
		try {
			instructorsDb.createInstructor(null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetInstructor() {
		InstructorAttributes i = createNewInstructor();
		
		// Get existent
		InstructorAttributes retrieved = instructorsDb.getInstructorForGoogleId(i.courseId, i.googleId);
		AssertJUnit.assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = instructorsDb.getInstructorForGoogleId("non.existent.course", "non.existent");
		AssertJUnit.assertNull(retrieved);
		
		// Null params check:
		try {
			instructorsDb.getInstructorForGoogleId(null, null);
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
		instructorsDb.updateInstructor(instructorToEdit);
		
		// Re-retrieve
		instructorToEdit = instructorsDb.getInstructorForGoogleId(instructorToEdit.courseId, instructorToEdit.googleId);
		AssertJUnit.assertEquals("My New Name", instructorToEdit.name);
		AssertJUnit.assertEquals("new@email.com", instructorToEdit.email);
		
		// FAIL : invalid parameters
		instructorToEdit.name = "";
		instructorToEdit.email = "aaa";
		try {
			instructorsDb.updateInstructor(instructorToEdit);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(
					"Invalid object received as a parameter :" 
						+ String.format(PERSON_NAME_ERROR_MESSAGE, instructorToEdit.name,	REASON_EMPTY) + EOL 
						+ String.format(EMAIL_ERROR_MESSAGE, instructorToEdit.email,	REASON_INCORRECT_FORMAT), 
					a.getMessage());
		}
		
		// Null parameters check:
		try {
			instructorsDb.updateInstructor(null);
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, ae.getMessage());
		}
	}
	
	@Test
	public void testDeleteInstructor() {
		InstructorAttributes i = createNewInstructor();
		
		// Delete
		instructorsDb.deleteInstructor(i.courseId, i.googleId);
		
		InstructorAttributes deleted = instructorsDb.getInstructorForGoogleId(i.courseId, i.googleId);
		AssertJUnit.assertNull(deleted);
		
		// delete again - should fail silently
		instructorsDb.deleteInstructor(i.courseId, i.googleId);
		
		// Null params check:
		try {
			instructorsDb.deleteInstructor(null, null);
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
	
	private InstructorAttributes createNewInstructor() {
		InstructorAttributes c = new InstructorAttributes();
		c.googleId = "valid.id";
		c.courseId = "valid.course";
		c.name = "valid.name";
		c.email = "valid@email.com";
				
		try {
			instructorsDb.createInstructor(c);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		return c;
	}
}

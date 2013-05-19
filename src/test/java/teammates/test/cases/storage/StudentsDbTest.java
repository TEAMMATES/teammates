package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertTrue;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.StudentsDb;
import teammates.storage.datastore.Datastore;
import teammates.test.cases.BaseTestCase;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class StudentsDbTest extends BaseTestCase {

	private StudentsDb studentsDb = new StudentsDb();
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
	public void testCreateStudent() throws EntityAlreadyExistsException, InvalidParametersException {
		// SUCCESS
		StudentAttributes s = new StudentAttributes();
		s.name = "valid student";
		s.course = "valid-course";
		s.email = "valid-fresh@email.com";
		s.team = "";
		s.comments="";
		s.id="";
		studentsDb.createStudent(s);
			
		// FAIL : duplicate
		try {
			studentsDb.createStudent(s);
			Assert.fail();
		} catch (EntityAlreadyExistsException e) {
			assertContains(AccountsDb.ERROR_CREATE_STUDENT_ALREADY_EXISTS, e.getMessage());
		}
		
		// FAIL : invalid params
		s.course = "invalid id space";
		try {
			studentsDb.createStudent(s);
			Assert.fail();
		} catch (InvalidParametersException e) {
			assertTrue(true); //expected
		} 
		
		// Null params check:
		try {
			studentsDb.createStudent(null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetStudent() throws InvalidParametersException {
		StudentAttributes s = createNewStudent();
		
		// Get existent
		StudentAttributes retrieved = studentsDb.getStudentForEmail(s.course, s.email);
		AssertJUnit.assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = studentsDb.getStudentForEmail("any-course-id", "non-existent@email.com");
		AssertJUnit.assertNull(retrieved);
		
		// Null params check:
		try {
			studentsDb.getStudentForEmail(null, "valid@email.com");
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			studentsDb.getStudentForEmail("any-course-id", null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testEditStudent() throws InvalidParametersException {
		StudentAttributes s = createNewStudent();
		
		// Edit existent
		studentsDb.updateStudent(s.course, s.email, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
		
		// Edit non-existent
		try {
			studentsDb.updateStudent("non-existent-course", "non@existent.email", "no-name", "non-existent-team", "non.existent.ID", "blah", "blah");
			Assert.fail();
		} catch (AssertionError a) {
			assertContains(AccountsDb.ERROR_UPDATE_NON_EXISTENT_STUDENT, a.getMessage());
		}
		
		// Null params check:
		// Only check first 2 params (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
		try {
			studentsDb.updateStudent(null, s.email, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		try {
			studentsDb.updateStudent(s.course, null, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testDeleteStudent() throws InvalidParametersException {
		StudentAttributes s = createNewStudent();
		
		// Delete
		studentsDb.deleteStudent(s.course, s.email);
		
		StudentAttributes deleted = studentsDb.getStudentForEmail(s.course, s.email);
		AssertJUnit.assertNull(deleted);
		
		// delete again - should fail silently
		studentsDb.deleteStudent(s.course, s.email);
		
		// Null params check:
		try {
			studentsDb.deleteStudent(null, s.email);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			studentsDb.deleteStudent(s.course, null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	private StudentAttributes createNewStudent() throws InvalidParametersException {
		StudentAttributes s = new StudentAttributes();
		s.name = "valid student";
		s.course = "valid-course";
		s.email = "valid@email.com";
		s.team = "";
		s.comments = "";
		s.id="";
		try {
			studentsDb.createStudent(s);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		return s;
	}
}

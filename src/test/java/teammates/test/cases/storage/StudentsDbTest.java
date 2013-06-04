package teammates.test.cases.storage;

import static org.testng.AssertJUnit.*;
import static teammates.common.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.FieldValidator.REASON_INCORRECT_FORMAT;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.StudentsDb;
import teammates.storage.datastore.Datastore;
import teammates.test.cases.BaseTestCase;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class StudentsDbTest extends BaseTestCase {
	
	//TODO: add missing test cases, refine existing ones. Follow the example
	//  of CoursesDbTest::testCreateCourse().

	private StudentsDb studentsDb = new StudentsDb();
	private static LocalServiceTestHelper helper;
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(StudentsDb.class);
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
		s.googleId="";
		studentsDb.createEntity(s);
			
		// FAIL : duplicate
		try {
			studentsDb.createEntity(s);
			Assert.fail();
		} catch (EntityAlreadyExistsException e) {
			assertContains(String.format(StudentsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, s.getEntityTypeAsString())
			+ s.getIdentificationString(), e.getMessage());
		}
		
		// FAIL : invalid params
		s.course = "invalid id space";
		try {
			studentsDb.createEntity(s);
			Assert.fail();
		} catch (InvalidParametersException e) {
			assertContains(
					String.format(COURSE_ID_ERROR_MESSAGE, s.course, REASON_INCORRECT_FORMAT),
					e.getMessage());
			
		} 
		
		// Null params check:
		try {
			studentsDb.createEntity(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetStudent() throws InvalidParametersException {
		StudentAttributes s = createNewStudent();
		
		______TS("typical success case");
		StudentAttributes retrieved = studentsDb.getStudentForEmail(s.course, s.email);
		assertNotNull(retrieved);
		
		______TS("non existant student case");
		retrieved = studentsDb.getStudentForEmail("any-course-id", "non-existent@email.com");
		assertNull(retrieved);
		
		______TS("null params case");
		try {
			studentsDb.getStudentForEmail(null, "valid@email.com");
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}		
		try {
			studentsDb.getStudentForEmail("any-course-id", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testUpdateStudent() throws InvalidParametersException, EntityDoesNotExistException {
		
		// Create a new student with valid attributes
		StudentAttributes s = createNewStudent();
		studentsDb.updateStudent(s.course, s.email, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
		
		______TS("non-existent case");
		try {
			studentsDb.updateStudent("non-existent-course", "non@existent.email", "no-name", "non-existent-team", "non.existent.ID", "blah", "blah");
			signalFailureToDetectException();
		} catch (EntityDoesNotExistException e) {
			assertEquals(StudentsDb.ERROR_UPDATE_NON_EXISTENT_STUDENT + "non-existent-course/non@existent.email", e.getMessage());
		}
		
		// Only check first 2 params (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
		______TS("null course case");
		try {
			studentsDb.updateStudent(null, s.email, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
			signalFailureToDetectException();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		______TS("null email case");
		try {
			studentsDb.updateStudent(s.course, null, "new-name", "new-team", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
			signalFailureToDetectException();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		______TS("duplicate email case");
		s = createNewStudent();
		// Create a second student with different email address
		StudentAttributes s2 = createNewStudent("valid2@email.com");
		try {
			studentsDb.updateStudent(s.course, s.email, "new-name", "new-team", s2.email, "new.google.id", "lorem ipsum dolor si amet");
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			assertEquals(StudentsDb.ERROR_UPDATE_EMAIL_ALREADY_USED + s2.name + "/" + 
					s2.email,e.getMessage());
		}

		______TS("typical success case");
		String originalEmail = s.email;
		s.name = "new-name-2";
		s.team = "new-team-2";
		s.email = "new-email-2";
		s.googleId = "new-id-2";
		s.comments = "this are new comments";
		studentsDb.updateStudent(s.course, originalEmail, s.name, s.team, s.email, s.googleId, s.comments);
		
		StudentAttributes updatedStudent = studentsDb.getStudentForEmail(s.course, s.email);
		assertTrue(updatedStudent.isEnrollInfoSameAs(s));
	}
	
	@Test
	public void testDeleteStudent() throws InvalidParametersException {
		StudentAttributes s = createNewStudent();
		
		// Delete
		studentsDb.deleteStudent(s.course, s.email);
		
		StudentAttributes deleted = studentsDb.getStudentForEmail(s.course, s.email);
		assertNull(deleted);
		
		// delete again - should fail silently
		studentsDb.deleteStudent(s.course, s.email);
		
		// Null params check:
		try {
			studentsDb.deleteStudent(null, s.email);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			studentsDb.deleteStudent(s.course, null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(StudentsDb.class);
		helper.tearDown();
	}
	
	private StudentAttributes createNewStudent() throws InvalidParametersException {
		StudentAttributes s = new StudentAttributes();
		s.name = "valid student";
		s.course = "valid-course";
		s.email = "valid@email.com";
		s.team = "";
		s.comments = "";
		s.googleId="";
		try {
			studentsDb.createEntity(s);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		return s;
	}
	
	private StudentAttributes createNewStudent(String email) throws InvalidParametersException {
		StudentAttributes s = new StudentAttributes();
		s.name = "valid student 2";
		s.course = "valid-course";
		s.email = email;
		s.team = "";
		s.comments = "";
		s.googleId="";
		try {
			studentsDb.createEntity(s);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		return s;
	}
}

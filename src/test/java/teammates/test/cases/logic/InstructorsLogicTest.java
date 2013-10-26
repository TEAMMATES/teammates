package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.InstructorsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class InstructorsLogicTest extends BaseComponentTestCase{

	private static InstructorsLogic instructorsLogic = new InstructorsLogic();
	private static InstructorsDb instructorsDb = new InstructorsDb();
	private static CoursesLogic coursesLogic = new CoursesLogic();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(InstructorsLogic.class);
	}
	
	@Test
	public void testCreateInstructor() throws Exception {
		
		______TS("success: create an instructor");
		
		InstructorAttributes instr = new InstructorAttributes(
				"ILogicT.newInstr", "test-course", "New Instructor", "instr@email.com");
		
		instructorsLogic.createInstructor(instr.googleId, instr.courseId, instr.name, instr.email);
		
		LogicTest.verifyPresentInDatastore(instr);
		
		______TS("failure: instructor already exists");
		
		try {
			instructorsLogic.createInstructor(instr.googleId, instr.courseId, instr.name, instr.email);
			signalFailureToDetectException();
		} catch (EntityAlreadyExistsException e) {
			AssertHelper.assertContains("Trying to create a Instructor that exists", e.getMessage());
		}
		
		______TS("failure: invalid parameter");
		
		instr.email = "invalidEmail.com";
		
		try {
			instructorsLogic.createInstructor(instr.googleId, instr.courseId, instr.name, instr.email);
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			AssertHelper.assertContains("\""+instr.email+"\" is not acceptable to TEAMMATES as an email",
								e.getMessage());
		}
		
	}
	
	@Test
	public void testGetInstructorForEmail() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("failure: instructor doesn't exist");

		assertNull(instructorsLogic.getInstructorForEmail("idOfTypicalCourse1", "non-exist@email.com"));

		______TS("success: get an instructor by using email");

		String courseId = "idOfTypicalCourse1";
		String email = "instructor1@course1.com";
		
		InstructorAttributes instr = instructorsLogic.getInstructorForEmail(courseId, email);
		
		assertEquals(courseId, instr.courseId);
		assertEquals(email, instr.email);
		assertEquals("idOfInstructor1OfCourse1", instr.googleId);
		assertEquals("Instructor1 Course1", instr.name);
	}
	
	@Test
	public void testGetInstructorForGoogleId() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("failure: instructor doesn't exist");

		assertNull(instructorsLogic.getInstructorForGoogleId("idOfTypicalCourse1", "non-exist-id"));

		______TS("success: typical case");

		String courseId = "idOfTypicalCourse1";
		String googleId = "idOfInstructor1OfCourse1";
		
		InstructorAttributes instr = instructorsLogic.getInstructorForGoogleId(courseId, googleId);
		
		assertEquals(courseId, instr.courseId);
		assertEquals(googleId, instr.googleId);
		assertEquals("instructor1@course1.com", instr.email);
		assertEquals("Instructor1 Course1", instr.name);
	} 

	@Test
	public void testGetInstructorsForCourse() throws Exception {
		restoreTypicalDataInDatastore();

		______TS("success: get all instructors for a course");

		String courseId = "idOfTypicalCourse1";
		
		List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(courseId);
		assertEquals(3, instructors.size());
		
		InstructorAttributes instructor1 = instructorsDb.getInstructorForGoogleId(courseId, "idOfInstructor1OfCourse1");
		InstructorAttributes instructor2 = instructorsDb.getInstructorForGoogleId(courseId, "idOfInstructor2OfCourse1");
		InstructorAttributes instructor3 = instructorsDb.getInstructorForGoogleId(courseId, "idOfInstructor3");
		
		verifySameInstructor(instructor1, instructors.get(0));
		verifySameInstructor(instructor2, instructors.get(1));
		verifySameInstructor(instructor3, instructors.get(2));
		
		______TS("failure: no instructors for a given course");
		
		courseId = "new-course";
		coursesLogic.createCourse(courseId, "New course");
		
		instructors = instructorsLogic.getInstructorsForCourse(courseId);
		assertEquals(0, instructors.size());
	}

	@Test
	public void testGetInstructorsForGoogleId() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("success: get all instructors for a google id");

		String googleId = "idOfInstructor3";
		
		List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForGoogleId(googleId);
		assertEquals(2, instructors.size());
		
		InstructorAttributes instructor1 = instructorsDb.getInstructorForGoogleId("idOfTypicalCourse1", "idOfInstructor3");
		InstructorAttributes instructor2 = instructorsDb.getInstructorForGoogleId("idOfTypicalCourse2", "idOfInstructor3");
		
		verifySameInstructor(instructor1, instructors.get(0));
		verifySameInstructor(instructor2, instructors.get(1));
		
		______TS("failure: non-exist google id");
		
		googleId = "non-exist-id";
		
		instructors = instructorsLogic.getInstructorsForGoogleId(googleId);
		assertEquals(0, instructors.size());
	}
	
	@Test
	public void testIsInstructorOfCourse() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("success: is an instructor of a given course");

		String instructorId = "idOfInstructor1OfCourse1";
		String courseId = "idOfTypicalCourse1";
		
		boolean result = instructorsLogic.isInstructorOfCourse(instructorId, courseId);
		
		assertEquals(true, result);
		
		______TS("failure: not an instructor of a given course");

		courseId = "idOfTypicalCourse2";
		
		result = instructorsLogic.isInstructorOfCourse(instructorId, courseId);
		
		assertEquals(false, result);
	}

	@Test
	public void testVerifyInstructorExists() throws Exception  {
		restoreTypicalDataInDatastore();
		
		______TS("success: instructor does exist");
		
		String instructorId = "idOfInstructor1OfCourse1";
		instructorsLogic.verifyInstructorExists(instructorId);
		
		______TS("failure: instructor doesn't exist");
		
		instructorId = "nonExistingInstructor";
		
		try {
			instructorsLogic.verifyInstructorExists(instructorId);
			signalFailureToDetectException();
		} catch (EntityDoesNotExistException e) {
			AssertHelper.assertContains("Instructor does not exist", e.getMessage());
		}
	}

	@Test
	public void testUpdateInstructor() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("typical case: update an instructor");
		
		String courseId = "idOfTypicalCourse1";
		String googleId = "idOfInstructor1OfCourse1";
		
		InstructorAttributes instructorToBeUpdated = instructorsLogic.getInstructorForGoogleId(courseId, googleId);
		instructorToBeUpdated.email = "new-email@course1.com";
		
		instructorsLogic.updateInstructor(courseId, googleId, instructorToBeUpdated.name, instructorToBeUpdated.email);
		
		InstructorAttributes instructorUpdated = instructorsLogic.getInstructorForGoogleId(courseId, googleId);
		verifySameInstructor(instructorToBeUpdated, instructorUpdated);
	}
	
	@Test
	public void testDeleteInstructor() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("typical case: delete an instructor for specific course");
		
		String courseId = "idOfTypicalCourse1";
		String googleId = "idOfInstructor1OfCourse1";
		
		InstructorAttributes instructorDeleted = instructorsLogic.getInstructorForGoogleId(courseId, googleId);
		
		instructorsLogic.deleteInstructor(courseId, googleId);
		
		LogicTest.verifyAbsentInDatastore(instructorDeleted);
	}

	@Test
	public void testDeleteInstructorsForGoogleId() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("typical case: delete all instructors for a given googleId");
		
		String googleId = "idOfInstructor3";
		
		instructorsLogic.deleteInstructorsForGoogleId(googleId);
		
		List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForGoogleId(googleId);
		
		assertEquals(true, instructorList.isEmpty());
	}

	@Test
	public void testDeleteInstructorsForCourse() throws Exception {
		restoreTypicalDataInDatastore();
		
		______TS("typical case: delete all instructors of a given course");
		
		String courseId = "idOfTypicalCourse1";
		
		instructorsLogic.deleteInstructorsForCourse(courseId);
		
		List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForCourse(courseId);
		
		assertEquals(true, instructorList.isEmpty());
	}

	
	@Test
	public void testParseInstructorLines() throws Exception {
		
		Method method = InstructorsLogic.class.getDeclaredMethod("parseInstructorLines",
				new Class[] { String.class, String.class });
		method.setAccessible(true);
		
		______TS("typical case");
		
		Object[] params = new Object[] { "private.course",
				"test1.googleId \t test1.name \t test1.email" + Const.EOL
			+	"test2.googleId | test2.name | test2.email"
		};
		
		@SuppressWarnings("unchecked")
		List<InstructorAttributes> result1 = 
			(List<InstructorAttributes>) method.invoke(instructorsLogic, params);
		assertEquals(result1.size(), 2);
		assertEquals(result1.get(0).googleId, "test1.googleId");	// only check first and last fields
		assertEquals(result1.get(1).email, "test2.email");
		
		______TS("blank space in first line");
		
		params = new Object[] { "private.course",
				Const.EOL
			+	"test1.googleId \t test1.name \t test1.email" + Const.EOL
			+	"test2.googleId | test2.name | test2.email"
		};
		
		@SuppressWarnings("unchecked")
		List<InstructorAttributes> result2 = 
			(List<InstructorAttributes>) method.invoke(instructorsLogic, params);
		assertEquals(result2.size(), 2);
		assertEquals(result2.get(0).googleId, "test1.googleId");	// only check first and last fields
		assertEquals(result2.get(1).email, "test2.email");
		
		______TS("blank space in between lines");
		
		params = new Object[] { "private.course",
				Const.EOL
			+	"test1.googleId \t test1.name \t test1.email" + Const.EOL
			+	Const.EOL
			+	"test2.googleId | test2.name | test2.email"
		};
		
		@SuppressWarnings("unchecked")
		List<InstructorAttributes> result3 = 
			(List<InstructorAttributes>) method.invoke(instructorsLogic, params);
		assertEquals(result3.size(), 2);
		assertEquals(result3.get(0).googleId, "test1.googleId");	// only check first and last fields
		assertEquals(result3.get(1).email, "test2.email");
		
		______TS("trailing blank lines");
		
		params = new Object[] { "private.course",
				Const.EOL
			+	"test1.googleId \t test1.name \t test1.email" + Const.EOL
			+	Const.EOL
			+	"test2.googleId | test2.name | test2.email"
			+	Const.EOL + Const.EOL
		};
		
		@SuppressWarnings("unchecked")
		List<InstructorAttributes> result4 = 
			(List<InstructorAttributes>) method.invoke(instructorsLogic, params);
		assertEquals(result4.size(), 2);
		assertEquals(result4.get(0).googleId, "test1.googleId");	// only check first and last fields
		assertEquals(result4.get(1).email, "test2.email");
		
		______TS("Instructor Lines information incorrect");
		
		// Too many
		try {
			params = new Object[] { "private.course",
				"test2.googleId | test2.name | test2.email | Something extra"
			};
			method.invoke(instructorsLogic,  params);
			Assert.fail();
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().toString().contains(
					InstructorAttributes.ERROR_INFORMATION_INCORRECT));
		}
		
		// Too few
		try {
			params = new Object[] { "private.course",
					"test2.googleId | "
				};
				method.invoke(instructorsLogic,  params);
			Assert.fail();
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().toString().contains(
					InstructorAttributes.ERROR_INFORMATION_INCORRECT));
		}
		
		______TS("lines is empty");
		
		try {
			params = new Object[] { "private.course",
					""
				};
				method.invoke(instructorsLogic,  params);
			Assert.fail();
		} catch (InvocationTargetException e) {
			assertTrue(e.getTargetException().toString().contains(
					InstructorsLogic.ERROR_NO_INSTRUCTOR_LINES));
		}
	}
	
	private void verifySameInstructor(InstructorAttributes instructor1, InstructorAttributes instructor2) {
		assertEquals(instructor1.googleId, instructor2.googleId);
		assertEquals(instructor1.courseId, instructor2.courseId);
		assertEquals(instructor1.name, instructor2.name);
		assertEquals(instructor1.email, instructor2.email);
	}
	
	@AfterClass()
	public static void classTearDown() throws Exception {
		turnLoggingDown(InstructorsLogic.class);
	}

}

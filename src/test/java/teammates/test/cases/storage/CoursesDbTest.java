package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.CoursesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.cases.logic.LogicTest;

public class CoursesDbTest extends BaseComponentTestCase {

	private CoursesDb coursesDb = new CoursesDb();
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
	}

	@Test
	public void testCreateCourse() throws EntityAlreadyExistsException, InvalidParametersException {
		
		/*Explanation:
		 * This is an inherited method from EntitiesDb and should be tested in
		 * EntitiesDbTest class. We test it here too because the method in
		 * the parent class actually calls an overridden method from the SUT.
		 */

		______TS("success: typical case");
		
		CourseAttributes c = new CourseAttributes();
		c.id = "CDbT.tCC.newCourse";
		c.name = "Basic Computing";
		coursesDb.createEntity(c);
		LogicTest.verifyPresentInDatastore(c);
		
		
	}
	
	@Test
	public void testGetCourse() throws InvalidParametersException {
		CourseAttributes c = createNewCourse();
		
		// Get existent
		CourseAttributes retrieved = coursesDb.getCourse(c.id);
		assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = coursesDb.getCourse("non-existent-course");
		assertNull(retrieved);
		
		// Null params check:
		try {
			coursesDb.getCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testUpdateCourse() throws Exception {
		
		______TS("null paramater");
		try {
			coursesDb.updateCourse(null);
			Assert.fail();
		} catch (AssertionError ae) {
			AssertJUnit.assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
		}
		
		______TS("fail: course with invalid parameters");
		
		CourseAttributes course = new CourseAttributes();
		course.id = "";
		course.name = "";
		course.isArchived = true;
		
		try {
			coursesDb.updateCourse(course);
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			ignoreExpectedException();
		}
		
		______TS("fail: non-exisitng course");
		
		course = new CourseAttributes();
		course.id = "CDbT.non-exist-course";
		course.name = "Non existing course";
		
		try {
			coursesDb.updateCourse(course);
			signalFailureToDetectException();
		} catch (EntityDoesNotExistException e) {
			assertEquals(CoursesDb.ERROR_UPDATE_NON_EXISTENT_COURSE, e.getMessage());
		}
		
		______TS("success: typical case");
		
		course = createNewCourse();
		course.isArchived = true;
		
		coursesDb.updateCourse(course);
		
		CourseAttributes courseRetrieved = coursesDb.getCourse(course.id);
		assertEquals(true, courseRetrieved.isArchived);
		
	}
	
	@Test
	public void testDeleteCourse() throws InvalidParametersException {
		CourseAttributes c = createNewCourse();
		
		// Delete
		coursesDb.deleteCourse(c.id);
		
		CourseAttributes deleted = coursesDb.getCourse(c.id);
		assertNull(deleted);
		
		// delete again - should fail silently
		coursesDb.deleteCourse(c.id);
		
		// Null params check:
		try {
			coursesDb.deleteCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}

	private CourseAttributes createNewCourse() throws InvalidParametersException {
		CourseAttributes c = new CourseAttributes();
		c.id = "Computing101";
		c.name = "Basic Computing";
		
		try {
			coursesDb.createEntity(c);
		} catch (EntityAlreadyExistsException e) {
			//It is ok if it already exists.
			ignoreExpectedException();
		}
		
		return c;
	}
}

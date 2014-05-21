package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.EntitiesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

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

        ______TS("Success: typical case");
        
        CourseAttributes c = new CourseAttributes();
        c.id = "CDbT.tCC.newCourse";
        c.name = "Basic Computing";
        coursesDb.createEntity(c);
        TestHelper.verifyPresentInDatastore(c);
        
        ______TS("Failure: create duplicate course");

        try {
            coursesDb.createEntity(c);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(String.format(EntitiesDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, "Course"), e.getMessage());
        }

        ______TS("Failure: create a course with invalid parameter");

        c.id = "Invalid id";
        try {
            coursesDb.createEntity(c);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("not acceptable to TEAMMATES as a Course ID because it is not in the correct format", 
                                        e.getMessage());
        }

        c.id = "CDbT.tCC.newCourse";
        c.name = StringHelper.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH + 1);
        try {
            coursesDb.createEntity(c);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("not acceptable to TEAMMATES as a course name because it is too long",
                                        e.getMessage());
        }

        ______TS("Failure: null parameter");

        try {
            coursesDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError e){
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
    }
    
    @Test
    public void testGetCourse() throws InvalidParametersException {
        CourseAttributes c = createNewCourse();
        
        ______TS("Success: get an existent course");

        CourseAttributes retrieved = coursesDb.getCourse(c.id);
        assertNotNull(retrieved);
        
        ______TS("Failure: get a non-existent course");

        retrieved = coursesDb.getCourse("non-existent-course");
        assertNull(retrieved);
        
        ______TS("Failure: get null parameters");
        
        try {
            coursesDb.getCourse(null);
            Assert.fail();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }
    
    @Test
    public void testUpdateCourse() throws Exception {
        
        ______TS("Failure: null paramater");
        
        try {
            coursesDb.updateCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
        ______TS("Failure: update course with invalid parameters");
        
        CourseAttributes course = new CourseAttributes();
        course.id = "";
        course.name = "";
        course.isArchived = true;
        
        try {
            coursesDb.updateCourse(course);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("not acceptable to TEAMMATES as a Course ID because it is empty",
                                        e.getMessage());
            AssertHelper.assertContains("not acceptable to TEAMMATES as a course name because it is empty",
                                        e.getMessage());
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
        assertEquals(course.isArchived, courseRetrieved.isArchived);
    }
    
    @Test
    public void testDeleteCourse() throws InvalidParametersException {
        CourseAttributes c = createNewCourse();
        
        ______TS("Success: delete an existing course");

        coursesDb.deleteCourse(c.id);
        
        CourseAttributes deleted = coursesDb.getCourse(c.id);
        assertNull(deleted);
        
        ______TS("Failure: delete a non-existent courses");

        // Should fail silently
        coursesDb.deleteCourse(c.id);

        ______TS("Failure: null parameter");

        try {
            coursesDb.deleteCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
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

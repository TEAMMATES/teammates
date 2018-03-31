package teammates.test.cases.storage;

import java.time.ZoneId;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.EntitiesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link CoursesDb}.
 */
public class CoursesDbTest extends BaseComponentTestCase {

    private CoursesDb coursesDb = new CoursesDb();

    @Test
    public void testCreateCourse() throws EntityAlreadyExistsException, InvalidParametersException {

        /*Explanation:
         * This is an inherited method from EntitiesDb and should be tested in
         * EntitiesDbTest class. We test it here too because the method in
         * the parent class actually calls an overridden method from the SUT.
         */

        ______TS("Success: typical case");

        CourseAttributes c = CourseAttributes
                .builder("CDbT.tCC.newCourse", "Basic Computing", ZoneId.of("UTC"))
                .build();
        coursesDb.createEntity(c);
        verifyPresentInDatastore(c);

        ______TS("Failure: create duplicate course");

        try {
            coursesDb.createEntity(c);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(String.format(EntitiesDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, "Course"),
                                        e.getMessage());
        }

        ______TS("Failure: create a course with invalid parameter");

        CourseAttributes invalidIdCourse = CourseAttributes
                .builder("Invalid id", "Basic Computing", ZoneId.of("UTC"))
                .build();
        try {
            coursesDb.createEntity(invalidIdCourse);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    "not acceptable to TEAMMATES as a/an course ID because it is not in the correct format",
                    e.getMessage());
        }

        String longCourseName = StringHelperExtension.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH + 1);
        CourseAttributes invalidNameCourse = CourseAttributes
                .builder("CDbT.tCC.newCourse", longCourseName, ZoneId.of("UTC"))
                .build();
        try {
            coursesDb.createEntity(invalidNameCourse);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("not acceptable to TEAMMATES as a/an course name because it is too long",
                                        e.getMessage());
        }

        ______TS("Failure: null parameter");

        try {
            coursesDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }

    }

    @Test
    public void testGetCourse() throws InvalidParametersException {
        CourseAttributes c = createNewCourse();

        ______TS("Success: get an existent course");

        CourseAttributes retrieved = coursesDb.getCourse(c.getId());
        assertNotNull(retrieved);

        ______TS("Failure: get a non-existent course");

        retrieved = coursesDb.getCourse("non-existent-course");
        assertNull(retrieved);

        ______TS("Failure: get null parameters");

        try {
            coursesDb.getCourse(null);
            signalFailureToDetectException();
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

        CourseAttributes invalidCourse = CourseAttributes
                .builder("", "", ZoneId.of("UTC"))
                .build();

        try {
            coursesDb.updateCourse(invalidCourse);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("The field 'course ID' is empty",
                                        e.getMessage());
            AssertHelper.assertContains("The field 'course name' is empty",
                                        e.getMessage());
        }

        ______TS("fail: non-exisitng course");

        CourseAttributes nonExistentCourse = CourseAttributes
                .builder("CDbT.non-exist-course", "Non existing course", ZoneId.of("UTC"))
                .build();

        try {
            coursesDb.updateCourse(nonExistentCourse);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(CoursesDb.ERROR_UPDATE_NON_EXISTENT_COURSE, e.getMessage());
        }

        ______TS("success: typical case");

        CourseAttributes c = createNewCourse();
        CourseAttributes updatedCourse = CourseAttributes
                .builder(c.getId(), c.getName() + " updated", ZoneId.of("UTC"))
                .build();

        coursesDb.updateCourse(updatedCourse);
        CourseAttributes retrieved = coursesDb.getCourse(c.getId());
        assertEquals(c.getName() + " updated", retrieved.getName());
    }

    @Test
    public void testDeleteCourse() throws InvalidParametersException {
        CourseAttributes c = createNewCourse();

        ______TS("Success: delete an existing course");

        coursesDb.deleteCourse(c.getId());

        CourseAttributes deleted = coursesDb.getCourse(c.getId());
        assertNull(deleted);

        ______TS("Failure: delete a non-existent courses");

        // Should fail silently
        coursesDb.deleteCourse(c.getId());

        ______TS("Failure: null parameter");

        try {
            coursesDb.deleteCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    private CourseAttributes createNewCourse() throws InvalidParametersException {

        CourseAttributes c = CourseAttributes
                .builder("Computing101", "Basic Computing", ZoneId.of("UTC"))
                .build();

        try {
            coursesDb.createEntity(c);
        } catch (EntityAlreadyExistsException e) {
            //It is ok if it already exists.
            ignoreExpectedException();
        }

        return c;
    }
}

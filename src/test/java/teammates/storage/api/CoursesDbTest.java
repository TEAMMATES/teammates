package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelperExtension;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * SUT: {@link CoursesDb}.
 */
public class CoursesDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    private final CoursesDb coursesDb = CoursesDb.inst();

    @Test
    public void testCreateCourse() throws Exception {

        /*Explanation:
         * This is an inherited method from EntitiesDb and should be tested in
         * EntitiesDbTest class. We test it here too because the method in
         * the parent class actually calls an overridden method from the SUT.
         */

        ______TS("Success: typical case");

        CourseAttributes c = CourseAttributes
                .builder("CDbT.tCC.newCourse")
                .withName("Basic Computing")
                .withTimezone("UTC")
                .withInstitute("Test institute")
                .build();
        coursesDb.createEntity(c);
        verifyPresentInDatabase(c);

        ______TS("Failure: create duplicate course");

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> coursesDb.createEntity(c));
        assertEquals(
                String.format(CoursesDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, c.toString()), eaee.getMessage());

        ______TS("Failure: create a course with invalid parameter");

        CourseAttributes invalidIdCourse = CourseAttributes
                .builder("Invalid id")
                .withName("Basic Computing")
                .withTimezone("UTC")
                .withInstitute("Test institute")
                .build();
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> coursesDb.createEntity(invalidIdCourse));
        AssertHelper.assertContains(
                "not acceptable to TEAMMATES as a/an course ID because it is not in the correct format",
                ipe.getMessage());

        String longCourseName = StringHelperExtension.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH + 1);
        CourseAttributes invalidNameCourse = CourseAttributes
                .builder("CDbT.tCC.newCourse")
                .withName(longCourseName)
                .withTimezone("UTC")
                .withInstitute("Test institute")
                .build();
        ipe = assertThrows(InvalidParametersException.class, () -> coursesDb.createEntity(invalidNameCourse));
        AssertHelper.assertContains("not acceptable to TEAMMATES as a/an course name because it is too long",
                ipe.getMessage());

        String longCourseInstitute = StringHelperExtension.generateStringOfLength(
                FieldValidator.INSTITUTE_NAME_MAX_LENGTH + 1);
        CourseAttributes invalidInstituteCourse = CourseAttributes
                .builder("CDbT.tCC.newCourse")
                .withName("Basic computing")
                .withTimezone("UTC")
                .withInstitute(longCourseInstitute)
                .build();
        ipe = assertThrows(InvalidParametersException.class, () -> coursesDb.createEntity(invalidInstituteCourse));
        AssertHelper.assertContains("not acceptable to TEAMMATES as a/an institute name because it is too long",
                ipe.getMessage());

        ______TS("Failure: null parameter");

        assertThrows(AssertionError.class, () -> coursesDb.createEntity(null));

    }

    @Test
    public void testGetCourse() throws Exception {
        CourseAttributes c = createNewCourse();

        ______TS("Success: get an existent course");

        CourseAttributes retrieved = coursesDb.getCourse(c.getId());
        assertNotNull(retrieved);

        ______TS("Failure: get a non-existent course");

        retrieved = coursesDb.getCourse("non-existent-course");
        assertNull(retrieved);

        ______TS("Failure: get null parameters");

        assertThrows(AssertionError.class, () -> coursesDb.getCourse(null));

    }

    @Test
    public void testGetCourses() throws Exception {
        CourseAttributes c = createNewCourse();
        List<String> courseIds = new ArrayList<>();

        ______TS("Success: get an existent course");

        courseIds.add(c.getId());
        List<CourseAttributes> retrieved = coursesDb.getCourses(courseIds);
        assertEquals(1, retrieved.size());

        ______TS("Failure: get a non-existent course");

        courseIds.remove(c.getId());
        courseIds.add("non-existent-course");
        retrieved = coursesDb.getCourses(courseIds);
        assertEquals(0, retrieved.size());

        ______TS("Failure: get null parameters");

        assertThrows(AssertionError.class, () -> coursesDb.getCourse(null));

    }

    @Test
    public void testUpdateCourse_noChangeToCourse_shouldNotIssueSaveRequest() throws Exception {
        CourseAttributes c = createNewCourse();

        CourseAttributes updatedCourse =
                coursesDb.updateCourse(
                        CourseAttributes.updateOptionsBuilder(c.getId())
                                .build());

        // please verify the log message manually to ensure that saving request is not issued
        assertEquals(JsonUtils.toJson(c), JsonUtils.toJson(updatedCourse));

        updatedCourse = coursesDb.updateCourse(
                CourseAttributes.updateOptionsBuilder(c.getId())
                        .withName(c.getName())
                        .withTimezone(c.getTimeZone())
                        .build());

        // please verify the log message manually to ensure that saving request is not issued
        assertEquals(JsonUtils.toJson(c), JsonUtils.toJson(updatedCourse));
    }

    @Test
    public void testUpdateCourse() throws Exception {

        ______TS("Failure: null parameter");

        assertThrows(AssertionError.class, () -> coursesDb.updateCourse(null));

        ______TS("fail: non-existing course");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> coursesDb.updateCourse(
                        CourseAttributes.updateOptionsBuilder("CDbT.non-exist-course")
                                .withName("Non existing course")
                                .build()
                ));
        assertEquals(CoursesDb.ERROR_UPDATE_NON_EXISTENT, ednee.getMessage());

        ______TS("success: typical case");

        CourseAttributes c = createNewCourse();

        CourseAttributes updatedCourse = coursesDb.updateCourse(
                CourseAttributes.updateOptionsBuilder(c.getId())
                        .withName(c.getName() + " updated")
                        .build()
        );
        CourseAttributes retrieved = coursesDb.getCourse(c.getId());
        assertEquals(c.getName() + " updated", retrieved.getName());
        assertEquals(c.getName() + " updated", updatedCourse.getName());

        ______TS("Failure: update course with invalid parameters");

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> coursesDb.updateCourse(
                        CourseAttributes.updateOptionsBuilder(c.getId())
                            .withName("")
                            .build()
                ));
        AssertHelper.assertContains("The field 'course name' is empty", ipe.getMessage());
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateCourse_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        CourseAttributes typicalCourse = createNewCourse();

        CourseAttributes updatedCourse = coursesDb.updateCourse(
                CourseAttributes.updateOptionsBuilder(typicalCourse.getId())
                        .withName(typicalCourse.getName() + " test")
                        .build());
        CourseAttributes actualCourse = coursesDb.getCourse(typicalCourse.getId());
        assertEquals(typicalCourse.getName() + " test", actualCourse.getName());
        assertEquals(typicalCourse.getName() + " test", updatedCourse.getName());

        assertNotEquals("Asia/Singapore", actualCourse.getTimeZone());
        updatedCourse = coursesDb.updateCourse(
                CourseAttributes.updateOptionsBuilder(typicalCourse.getId())
                        .withTimezone("Asia/Singapore")
                        .build());
        actualCourse = coursesDb.getCourse(typicalCourse.getId());
        assertEquals("Asia/Singapore", actualCourse.getTimeZone());
        assertEquals("Asia/Singapore", updatedCourse.getTimeZone());
    }

    @Test
    public void testDeleteCourse() throws Exception {
        CourseAttributes c = createNewCourse();
        assertNotNull(coursesDb.getCourse(c.getId()));

        ______TS("Failure: delete a non-existent courses");

        // Should fail silently
        coursesDb.deleteCourse("not_exist");
        assertNotNull(coursesDb.getCourse(c.getId()));

        ______TS("Success: delete an existing course");

        coursesDb.deleteCourse(c.getId());

        CourseAttributes deleted = coursesDb.getCourse(c.getId());
        assertNull(deleted);

        ______TS("Delete it again");

        coursesDb.deleteCourse(c.getId());
        assertNull(coursesDb.getCourse(c.getId()));

        ______TS("Failure: null parameter");

        assertThrows(AssertionError.class, () -> coursesDb.deleteCourse(null));

    }

    @Test
    public void testSoftDeleteCourse() throws Exception {
        CourseAttributes c = createNewCourse();

        ______TS("Success: soft delete an existing course");
        coursesDb.softDeleteCourse(c.getId());
        CourseAttributes deleted = coursesDb.getCourse(c.getId());

        assertTrue(deleted.isCourseDeleted());

        ______TS("Success: restore soft deleted course");
        coursesDb.restoreDeletedCourse(deleted.getId());
        CourseAttributes restored = coursesDb.getCourse(deleted.getId());
        assertFalse(restored.isCourseDeleted());

        ______TS("null parameter");

        assertThrows(AssertionError.class, () -> coursesDb.deleteCourse(null));

    }

    private CourseAttributes createNewCourse() throws Exception {

        CourseAttributes c = CourseAttributes
                .builder("Computing101")
                .withName("Basic Computing")
                .withTimezone("UTC")
                .withInstitute("Test institute")
                .build();

        return coursesDb.putEntity(c);
    }
}

package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.time.ZoneId;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.StringHelperExtension;
import teammates.storage.entity.Course;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link CourseAttributes}.
 */
public class CourseAttributesTest extends BaseTestCase {

    @Test
    public void testValueOf_withTypicalData_shouldGenerateAttributesCorrectly() {
        Instant typicalInstant = Instant.now();
        Course course = new Course("testId", "testName", "UTC", typicalInstant, typicalInstant);

        CourseAttributes courseAttributes = CourseAttributes.valueOf(course);

        assertEquals("testId", courseAttributes.getId());
        assertEquals("testName", courseAttributes.getName());
        assertEquals("UTC", courseAttributes.getTimeZone().getId());
        assertEquals(typicalInstant, courseAttributes.getCreatedAt());
        assertEquals(typicalInstant, courseAttributes.getDeletedAt());
    }

    @Test
    public void testValueOf_withInvalidTimezoneStr_shouldFallbackToDefaultTimezone() {
        Instant typicalInstant = Instant.now();
        Course course = new Course("testId", "testName", "invalid", typicalInstant, typicalInstant);

        CourseAttributes courseAttributes = CourseAttributes.valueOf(course);

        assertEquals("UTC", courseAttributes.getTimeZone().getId());
    }

    @Test
    public void testValueOf_withSomeFieldsPopulatedAsNull_shouldUseDefaultValues() {
        Course course = new Course("testId", "testName", "UTC", null, null);
        course.setCreatedAt(null);
        course.setDeletedAt(null);
        assertNull(course.getCreatedAt());
        assertNull(course.getDeletedAt());

        CourseAttributes courseAttributes = CourseAttributes.valueOf(course);

        assertEquals("testId", courseAttributes.getId());
        assertEquals("testName", courseAttributes.getName());
        assertEquals("UTC", courseAttributes.getTimeZone().getId());
        assertNotNull(courseAttributes.getCreatedAt());
        assertNull(courseAttributes.getDeletedAt());
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectAttributes() {
        String validName = "validName";
        String validId = "validId";
        ZoneId validTimeZone = ZoneId.of("UTC");

        CourseAttributes courseAttributes = CourseAttributes
                .builder(validId)
                .withName(validName)
                .withTimezone(validTimeZone)
                .build();

        assertNotNull(courseAttributes.getCreatedAt());
        assertNull(courseAttributes.getDeletedAt());
        assertEquals(validId, courseAttributes.getId());
        assertEquals(validName, courseAttributes.getName());
        assertEquals(validTimeZone, courseAttributes.getTimeZone());
    }

    @Test
    public void testBuilder_buildNothing_shouldUseDefaultValues() {
        CourseAttributes courseAttributes = CourseAttributes.builder("id").build();

        assertEquals("id", courseAttributes.getId());
        assertNull(courseAttributes.getName());
        assertEquals(Const.DEFAULT_TIME_ZONE, courseAttributes.getTimeZone());
        assertNotNull(courseAttributes.getCreatedAt());
        assertNull(courseAttributes.getDeletedAt());
    }

    @Test
    public void testBuilder_withNullArguments_shouldThrowException() {
        assertThrows(AssertionError.class, () -> {
            CourseAttributes
                    .builder(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            CourseAttributes
                    .builder("id")
                    .withName(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            CourseAttributes
                    .builder("id")
                    .withTimezone(null)
                    .build();
        });
    }

    @Test
    public void testValidate() throws Exception {

        CourseAttributes validCourse = generateValidCourseAttributesObject();

        assertTrue("valid value", validCourse.isValid());

        String veryLongId = StringHelperExtension.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH + 1);
        String emptyName = "";
        CourseAttributes invalidCourse = CourseAttributes
                .builder(veryLongId)
                .withName(emptyName)
                .withTimezone(ZoneId.of("UTC"))
                .build();

        assertFalse("invalid value", invalidCourse.isValid());
        String errorMessage =
                getPopulatedErrorMessage(
                    FieldValidator.COURSE_ID_ERROR_MESSAGE, invalidCourse.getId(),
                    FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                    FieldValidator.COURSE_ID_MAX_LENGTH) + System.lineSeparator()
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.COURSE_NAME_FIELD_NAME, FieldValidator.COURSE_NAME_MAX_LENGTH);
        assertEquals("invalid value", errorMessage, StringHelper.toString(invalidCourse.getInvalidityInfo()));
    }

    @Test
    public void testGetValidityInfo() {
        //already tested in testValidate() above
    }

    @Test
    public void testIsValid() {
        //already tested in testValidate() above
    }

    @Test
    public void testToString() {
        CourseAttributes c = generateValidCourseAttributesObject();
        assertEquals("[CourseAttributes] id: valid-id-$_abc name: valid-name timeZone: UTC", c.toString());
    }

    @Test
    public void testEquals() {
        CourseAttributes course = generateValidCourseAttributesObject();

        // When the two courses have same values
        CourseAttributes courseSimilar = generateValidCourseAttributesObject();

        assertTrue(course.equals(courseSimilar));

        // When the two courses are different
        CourseAttributes courseDifferent = CourseAttributes.builder("id")
                .withName("Another Name")
                .build();

        assertFalse(course.equals(courseDifferent));

        // When the other object is of different class
        assertFalse(course.equals(3));
    }

    @Test
    public void testHashCode() {
        CourseAttributes course = generateValidCourseAttributesObject();

        // When the two courses have same values, they should have the same hash code
        CourseAttributes courseSimilar = generateValidCourseAttributesObject();

        assertTrue(course.hashCode() == courseSimilar.hashCode());

        // When the two courses are different, they should have different hash code
        CourseAttributes courseDifferent = CourseAttributes.builder("id")
                .withName("Another Name")
                .build();

        assertFalse(course.hashCode() == courseDifferent.hashCode());
    }

    private static CourseAttributes generateValidCourseAttributesObject() {
        return CourseAttributes.builder("valid-id-$_abc")
                .withName("valid-name")
                .withTimezone(ZoneId.of("UTC"))
                .build();
    }

}

package teammates.test.cases.datatransfer;

import static teammates.common.util.Const.EOL;

import java.util.Date;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes.CourseAttributesBuilder;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Course;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link CourseAttributes}.
 */
public class CourseAttributesTest extends BaseTestCase {

    //TODO: add test for constructor

    private static final String VALID_COURSE_ID = "valid-id-$_abc";
    private static final String VALID_NAME = "valid-name";
    private static final String VALID_TIMEZONE = "UTC";
    private static final Date DEFAULT_DATE = CourseAttributes.DEFAULT_DATE;

    @Test
    public void testValidate() throws Exception {

        CourseAttributes validCourse = generateValidCourseAttributesObject();

        assertTrue("valid value", validCourse.isValid());

        String veryLongId = StringHelperExtension.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH + 1);
        String emptyName = "";
        String invalidTimeZone = "InvalidTimeZone";
        CourseAttributes invalidCourse = new CourseAttributesBuilder()
                .withCourseId(veryLongId)
                .withName(emptyName)
                .withTimeZone(invalidTimeZone)
                .build();

        assertFalse("invalid value", invalidCourse.isValid());
        String errorMessage =
                getPopulatedErrorMessage(
                    FieldValidator.COURSE_ID_ERROR_MESSAGE, invalidCourse.getId(),
                    FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                    FieldValidator.COURSE_ID_MAX_LENGTH) + EOL
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.COURSE_NAME_FIELD_NAME, FieldValidator.COURSE_NAME_MAX_LENGTH) + EOL
                + getPopulatedErrorMessage(
                      FieldValidator.COURSE_TIME_ZONE_ERROR_MESSAGE, invalidCourse.getTimeZone(),
                      FieldValidator.COURSE_TIME_ZONE_FIELD_NAME, FieldValidator.REASON_UNAVAILABLE_AS_CHOICE);
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
    public void testToEntity() {
        CourseAttributes attributes = generateValidCourseAttributesObject();

        Course expectedCourse = new Course(attributes.getId(), attributes.getName(),
                attributes.getTimeZone(), attributes.createdAt);

        Course actualCourse = CourseAttributes.valueOf(expectedCourse).toEntity();

        assertEquals(expectedCourse.getUniqueId(), actualCourse.getUniqueId());
        assertEquals(expectedCourse.getName(), actualCourse.getName());
        assertEquals(expectedCourse.getTimeZone(), actualCourse.getTimeZone());
        assertEquals(expectedCourse.getCreatedAt(), actualCourse.getCreatedAt());
    }

    @Test
    public void testBuilderWithNullArguments() {
        CourseAttributes courseAttributesWithNullValues = new CourseAttributesBuilder()
                .withCourseId(null) .withName(null) .withTimeZone(null)
                .withCreatedAt(null)
                .build();
        // No default values for required params
        assertNull(courseAttributesWithNullValues.getId());
        assertNull(courseAttributesWithNullValues.getName());
        assertNull(courseAttributesWithNullValues.getTimeZone());

        // Check default values for optional params
        assertEquals(DEFAULT_DATE, courseAttributesWithNullValues.createdAt);

    }

    @Test
    public void testBuilderWithRequiredValues() {
        CourseAttributes validCourseAttributes = generateValidCourseAttributesObject();
        assertEquals(VALID_COURSE_ID, validCourseAttributes.getId());
        assertEquals(VALID_NAME, validCourseAttributes.getName());
        assertEquals(VALID_TIMEZONE, validCourseAttributes.getTimeZone());
        assertEquals(DEFAULT_DATE, validCourseAttributes.createdAt);
    }

    private static CourseAttributes generateValidCourseAttributesObject() {
        return new CourseAttributesBuilder()
                .withCourseId(VALID_COURSE_ID) .withName(VALID_NAME) .withTimeZone(VALID_TIMEZONE)
                .build();
    }

}

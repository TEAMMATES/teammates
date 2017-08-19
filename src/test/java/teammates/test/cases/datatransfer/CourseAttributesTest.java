package teammates.test.cases.datatransfer;

import static teammates.common.util.Const.EOL;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.StringHelperExtension;

/**
 * SUT: {@link CourseAttributes}.
 */
public class CourseAttributesTest extends BaseTestCase {

    //TODO: add test for constructor

    @Test
    public void testValidate() throws Exception {

        CourseAttributes validCourse = generateValidCourseAttributesObject();

        assertTrue("valid value", validCourse.isValid());

        String veryLongId = StringHelperExtension.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH + 1);
        String emptyName = "";
        String invalidTimeZone = "InvalidTimeZone";
        CourseAttributes invalidCourse = new CourseAttributes(veryLongId, emptyName, invalidTimeZone);

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

    private static CourseAttributes generateValidCourseAttributesObject() {
        return new CourseAttributes("valid-id-$_abc", "valid-name", "UTC");
    }

}

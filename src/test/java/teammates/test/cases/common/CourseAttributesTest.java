package teammates.test.cases.common;

import static teammates.common.util.Const.EOL;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;

public class CourseAttributesTest extends BaseTestCase {

    //TODO: add test for constructor
    
    @BeforeClass
    public static void setupClass() {
        printTestClassHeader();
    }

    @Test
    public void testValidate() {
        
        CourseAttributes validCourse = generateValidCourseAttributesObject();
        
        assertTrue("valid value", validCourse.isValid());
        
        
        String veryLongId = StringHelper.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH + 1);
        String emptyName = "";
        CourseAttributes invalidCourse = new CourseAttributes(veryLongId, emptyName);
        
        assertFalse("invalid value", invalidCourse.isValid());
        String errorMessage =
                getPopulatedErrorMessage(
                    FieldValidator.COURSE_ID_ERROR_MESSAGE, invalidCourse.getId(),
                    FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                    FieldValidator.COURSE_ID_MAX_LENGTH) + EOL
                + getPopulatedErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, invalidCourse.getName(),
                      FieldValidator.COURSE_NAME_FIELD_NAME, FieldValidator.REASON_EMPTY,
                      FieldValidator.COURSE_NAME_MAX_LENGTH);
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
        assertEquals("[CourseAttributes] id: valid-id-$_abc name: valid-name isArchived: false", c.toString());
    }
    
    public static CourseAttributes generateValidCourseAttributesObject() {
        CourseAttributes c;
        c = new CourseAttributes("valid-id-$_abc", "valid-name");
        return c;
    }

    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }

}

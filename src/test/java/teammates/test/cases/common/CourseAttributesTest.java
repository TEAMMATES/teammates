package teammates.test.cases.common;

import static teammates.common.util.Const.EOL;
import static teammates.common.util.FieldValidator.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
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
        
        
        String veryLongId = StringHelper.generateStringOfLength(COURSE_ID_MAX_LENGTH + 1);
        String emptyName = "";
        CourseAttributes invalidCourse = new CourseAttributes(veryLongId, emptyName);
        
        assertFalse("invalid value", invalidCourse.isValid());
        String errorMessage = 
                String.format(COURSE_ID_ERROR_MESSAGE, invalidCourse.getId(), REASON_TOO_LONG) + EOL 
                + String.format(COURSE_NAME_ERROR_MESSAGE, invalidCourse.getName(), REASON_EMPTY);
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
        assertEquals("valid value", "[CourseAttributes] id: valid-id-$_abc name: valid-name isArchived: false", c.toString());
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

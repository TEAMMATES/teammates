package teammates.test.cases.common;

import static org.testng.AssertJUnit.*;
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
    public static void setupClass() throws Exception {
        printTestClassHeader();
    }

    @Test
    public void testValidate() {
        
        CourseAttributes c = generateValidCourseAttributesObject();
        
        assertEquals("valid value", true, c.isValid());
        
        
        String veryLongId = StringHelper.generateStringOfLength(COURSE_ID_MAX_LENGTH+1);
        String emptyName = "";
        c.id = veryLongId;
        c.name = emptyName;
        
        assertEquals("invalid value", false, c.isValid());
        String errorMessage = 
                String.format(COURSE_ID_ERROR_MESSAGE, c.id, REASON_TOO_LONG) + EOL + 
                String.format(COURSE_NAME_ERROR_MESSAGE, c.name, REASON_EMPTY);
        assertEquals("invalid value", errorMessage, StringHelper.toString(c.getInvalidityInfo()));
    }

    @Test
    public void testGetValidityInfo(){
        //already tested in testValidate() above
    }
    
    @Test
    public void testIsValid(){
        //already tested in testValidate() above
    }
    
    @Test
    public void testToString(){
        CourseAttributes c = generateValidCourseAttributesObject();
        assertEquals("valid value", "[CourseAttributes] id: valid-id-$_abc name: valid-name isArchived: false", c.toString());
    }
    
    public static CourseAttributes generateValidCourseAttributesObject() {
        CourseAttributes c;
        c = new CourseAttributes();
        c.id = "valid-id-$_abc";
        c.name = "valid-name";
        return c;
    }

    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }

}

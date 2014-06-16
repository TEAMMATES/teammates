package teammates.test.cases.common;

import static org.testng.AssertJUnit.*;
import static teammates.common.util.Const.EOL;
import static teammates.common.util.FieldValidator.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;

public class InstructorAttributesTest extends BaseTestCase {

    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
    }

    @Test
    public void testValidate() {
        
        InstructorAttributes i = new InstructorAttributes("valid.google.id", "valid-course-id", "valid name", "valid@email.com");
        
        assertEquals(true, i.isValid());
        
        i.googleId = "invalid@google@id";
        i.name = "";
        i.email = "invalid email";
        i.courseId = "";
        
        assertEquals("invalid value", false, i.isValid());
        String errorMessage = 
                String.format(GOOGLE_ID_ERROR_MESSAGE, i.googleId, REASON_INCORRECT_FORMAT) + EOL 
                + String.format(COURSE_ID_ERROR_MESSAGE, i.courseId, REASON_EMPTY) + EOL 
                + String.format(PERSON_NAME_ERROR_MESSAGE, i.name, REASON_EMPTY)+ EOL
                + String.format(EMAIL_ERROR_MESSAGE, i.email, REASON_INCORRECT_FORMAT);  
        assertEquals("invalid value", errorMessage, StringHelper.toString(i.getInvalidityInfo()));
    }
    
    @Test
    public void testConstructor() {
        InstructorAttributes instructor = new InstructorAttributes("valid.google.id", "valid-course-id", "valid name", "valid@email.com");
        String roleName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        
        assertEquals(roleName, instructor.role);
        assertEquals(displayedName, instructor.displayedName);
        assertEquals(privileges, instructor.privileges);
        
        InstructorAttributes instructor1 = new InstructorAttributes(instructor.googleId, instructor.courseId, instructor.name, instructor.email,
                instructor.role, instructor.displayedName, instructor.instructorPrivilegesAsText);
        
        assertEquals(privileges, instructor1.privileges);
    }
    
    @Test
    public void testIsRegistered() {
        InstructorAttributes instructor = new InstructorAttributes("valid.google.id", "valid-course-id", "valid name", "valid@email.com");       
        assertTrue(instructor.isRegistered());
        
        instructor.googleId = null;
        assertFalse(instructor.isRegistered());     
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }

}

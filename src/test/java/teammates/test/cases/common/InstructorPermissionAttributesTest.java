package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import teammates.common.util.FieldValidator;
import teammates.common.util.Utils;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.storage.entity.InstructorPermission;
import teammates.test.cases.BaseTestCase;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.Text;

public class InstructorPermissionAttributesTest extends BaseTestCase {
    private static Gson gson = Utils.getTeammatesGson();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
    }

    @Test
    public void testValidate() {
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.setDefaultPrivilegesForCoowner();
        String instrEmail = "instr@gmail.com";
        String courseId = "courseId";
        String role = "Co-owner";
        
        InstructorPermissionAttributes instructorAttr = new InstructorPermissionAttributes(instrEmail, courseId, role, privileges);
        assertEquals(0, instructorAttr.getInvalidityInfo().size());
        
        instructorAttr = new InstructorPermissionAttributes(null, courseId, role, privileges);
        try {
            instructorAttr.getInvalidityInfo();
            signalFailureToDetectException();
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
        instructorAttr = new InstructorPermissionAttributes(instrEmail, null, role, privileges);
        try {
            instructorAttr.getInvalidityInfo();
            signalFailureToDetectException();
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
        instructorAttr = new InstructorPermissionAttributes(instrEmail, courseId, null, privileges);
        try {
            instructorAttr.getInvalidityInfo();
            signalFailureToDetectException();
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
        
        String emptyValue = "";
        instructorAttr = new InstructorPermissionAttributes(emptyValue, courseId, role, privileges);
        assertEquals(1, instructorAttr.getInvalidityInfo().size());
        assertEquals(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, emptyValue, FieldValidator.REASON_EMPTY),
                instructorAttr.getInvalidityInfo().get(0));
        
        instructorAttr = new InstructorPermissionAttributes(instrEmail, emptyValue, role, privileges);
        assertEquals(1, instructorAttr.getInvalidityInfo().size());
        assertEquals(String.format(FieldValidator.COURSE_ID_ERROR_MESSAGE, emptyValue, FieldValidator.REASON_EMPTY),
                instructorAttr.getInvalidityInfo().get(0));
        
        instructorAttr = new InstructorPermissionAttributes(instrEmail, courseId, emptyValue, privileges);
        assertEquals(1, instructorAttr.getInvalidityInfo().size());
        assertEquals(String.format(FieldValidator.INSTRUCTOR_ROLE_ERROR_MESSAGE, emptyValue, FieldValidator.REASON_EMPTY),
                instructorAttr.getInvalidityInfo().get(0));
    }
    
    @Test
    public void testContructor() {
        InstructorPrivileges privileges = new InstructorPrivileges();
        privileges.setDefaultPrivilegesForCoowner();
        String instrEmail = "instr@gmail.com";
        String courseId = "courseId";
        String role = "Co-owner";        
        InstructorPermissionAttributes instructorPermissionAttr = new InstructorPermissionAttributes(instrEmail, courseId, role, privileges);
        
        String privilegesString = gson.toJson(privileges);
        assertEquals(privilegesString, instructorPermissionAttr.getInstructorPrivilegesAsString());
        
        instructorPermissionAttr = new InstructorPermissionAttributes(instrEmail, courseId, role, new Text(privilegesString));
        assertEquals(privileges, instructorPermissionAttr.privileges);
        
        InstructorPermission instrPermission = instructorPermissionAttr.toEntity();
        InstructorPermissionAttributes instrAttr = new InstructorPermissionAttributes(instrPermission);
        assertEquals(instructorPermissionAttr.toString(), instrAttr.toString());
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}

package teammates.test.cases.logic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.core.InstructorPermissionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.InstructorPermissionsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class InstructorPermissionsLogicTest extends BaseComponentTestCase {
    
    private static InstructorPermissionsLogic instructorPermissionsLogic = InstructorPermissionsLogic.inst();
    private static InstructorPermissionsDb instructorPermissionsDb = new InstructorPermissionsDb();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(InstructorsLogic.class);
    }
    
    @BeforeMethod
    private void caseSetUp()
            throws InvalidParametersException, EntityAlreadyExistsException {
        String courseId = "courseIdForIPL";
        String instrEmail = "instrPermLogic1@google.com";
        String role = "Co-owner";
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPermissionAttributes instrPermissionAttr = new InstructorPermissionAttributes(instrEmail, courseId, role, privileges);
        try {
            instructorPermissionsDb.createEntity(instrPermissionAttr);
        } catch (EntityAlreadyExistsException e) {
            ignoreExpectedException();
        }
        
        courseId = "courseIdForIPL";
        instrEmail = "instrPermLogic2@google.com";
        role = "Manager";
        privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        instrPermissionAttr = new InstructorPermissionAttributes(instrEmail, courseId, role, privileges);
        try {
            instructorPermissionsDb.createEntity(instrPermissionAttr);
        } catch (EntityAlreadyExistsException e) {
            ignoreExpectedException();
        }
        
        courseId = "courseIdForIPL";
        instrEmail = "instrPermLogic3@google.com";
        role = "Manager";
        privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        instrPermissionAttr = new InstructorPermissionAttributes(instrEmail, courseId, role, privileges);
        try {
            instructorPermissionsDb.createEntity(instrPermissionAttr);
        } catch (EntityAlreadyExistsException e) {
            ignoreExpectedException();
        }
        
        courseId = "courseId2ForIPL";
        instrEmail = "instrPermLogic1@google.com";
        role = "Manager";
        privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        instrPermissionAttr = new InstructorPermissionAttributes(instrEmail, courseId, role, privileges);
        try {
            instructorPermissionsDb.createEntity(instrPermissionAttr);
        } catch (EntityAlreadyExistsException e) {
            ignoreExpectedException();
        }
    }
    
    @Test
    public void testAddInstructorPermission() throws InvalidParametersException, EntityAlreadyExistsException {
        String courseId = "courseIdForAddIPL";
        String instrEmail = "instrPermLogic@google.com";
        String role = "Co-owner";
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        
        ______TS("typical case: create an instructorPermission");
        
        instructorPermissionsLogic.addInstructorPermission(courseId, instrEmail, role, privileges);
        
        ______TS("failure: duplicate instructorPermission");
        
        InstructorPermissionAttributes instrPermissionAttr = new InstructorPermissionAttributes(instrEmail, courseId, role, privileges);
        try {
            instructorPermissionsLogic.addInstructorPermission(instrPermissionAttr);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(instrEmail + ", " + courseId, e.getMessage());
        }
    }
    
    @Test
    public void testGetInstructorPermissionForEmail() throws InvalidParametersException, EntityAlreadyExistsException {
        String courseId = "courseIdForIPL";
        String instrEmail = "instrPermLogic1@google.com";
        String role = "Co-owner";
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        
        ______TS("typical case: get an instructorPermission");
        
        InstructorPermissionAttributes attrFromDb = instructorPermissionsLogic.getInstructorPermissionForEmail(courseId, instrEmail);
        assertNotNull(attrFromDb);
        assertEquals(courseId, attrFromDb.courseId);
        assertEquals(instrEmail, attrFromDb.instructorEmail);
        assertEquals(role, attrFromDb.role);
        assertEquals(privileges, attrFromDb.privileges);
        
        ______TS("typical case: get a non-existing instructorPermission");
        
        String nonExistingEmail = "nonExistingEmail@google.com";
        attrFromDb = instructorPermissionsLogic.getInstructorPermissionForEmail(courseId, nonExistingEmail);
        assertNull(attrFromDb);
    }
    
    @Test
    public void testGetInstructorPermissionsForEmail() throws InvalidParametersException, EntityAlreadyExistsException {
    }
    
    @AfterClass()
    public static void classTearDown() throws Exception {
        turnLoggingDown(InstructorsLogic.class);
        printTestClassFooter();
    }
}

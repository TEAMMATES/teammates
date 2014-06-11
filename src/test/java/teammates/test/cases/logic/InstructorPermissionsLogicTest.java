package teammates.test.cases.logic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
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
    public void testGetInstructorPermissionForEmail() {
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
    public void testGetInstructorPermissionsForEmail() {
        String instrEmail = "instrPermLogic1@google.com";
        
        ______TS("typical case: get multiple instructorPermissions");
        
        List<InstructorPermissionAttributes> attrList = instructorPermissionsLogic.getInstructorPermissionsForEmail(instrEmail);
        assertEquals(2, attrList.size());
        assertEquals(instrEmail, attrList.get(0).instructorEmail);
        assertEquals(instrEmail, attrList.get(1).instructorEmail);
        
        ______TS("typical case: get 0 instructorPermissions");
        
        String nonExistingEmail = "nonExistingEmail@google.com";
        attrList = instructorPermissionsLogic.getInstructorPermissionsForEmail(nonExistingEmail);
        assertEquals(0, attrList.size());
    }
    
    @Test
    public void testGetInstructorPermissionsForCourse() {
        String courseId = "courseIdForIPL";
        
        ______TS("typical case: get multiple instructorPermissions");
        
        List<InstructorPermissionAttributes> attrList = instructorPermissionsLogic.getInstructorPermissionsForCourse(courseId);
        assertEquals(3, attrList.size());
        assertEquals(courseId, attrList.get(0).courseId);
        assertEquals(courseId, attrList.get(1).courseId);
        assertEquals(courseId, attrList.get(2).courseId);
        
        ______TS("typical case: get 0 instructorPermissions");
        
        String nonExistingCourseId = "nonExistingCourseId";
        attrList = instructorPermissionsLogic.getInstructorPermissionsForCourse(nonExistingCourseId);
        assertEquals(0, attrList.size());
    }
    
    @Test
    public void testUpdateInstructorPermissionByEmail() throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        String instrEmail = "instrPermLogic1@google.com";
        String updatedEmail = "updatedIPL@google.com";
        String courseId = "courseIdForIPL";
        String role = "Helper";
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_HELPER);
        InstructorPermissionAttributes attr = new InstructorPermissionAttributes(updatedEmail, courseId, role, privileges);
        
        ______TS("typical case: successfully update instructorPermission");
        
        instructorPermissionsLogic.updateInstructorPermissionByEmail(attr, instrEmail);
        InstructorPermissionAttributes attrFromDb = instructorPermissionsLogic.getInstructorPermissionForEmail(courseId, updatedEmail);
        assertNotNull(attrFromDb);
        assertEquals(role, attrFromDb.role);
        assertEquals(privileges, attrFromDb.privileges);
        
        ______TS("failure: update to existent entity");
        
        String existentEmail = "instrPermLogic2@google.com";
        attr.instructorEmail = existentEmail;
        try {
            instructorPermissionsLogic.updateInstructorPermissionByEmail(attr, updatedEmail);
            signalFailureToDetectException();
        } catch(EntityAlreadyExistsException e) {
            AssertHelper.assertContains(courseId + "/" + existentEmail, e.getMessage());
        }
        
        ______TS("failure: update non existing entity");
        
        String nonExistingEmail = "nonExisting@google.com";
        try {
            instructorPermissionsLogic.updateInstructorPermissionByEmail(attr, nonExistingEmail);
            signalFailureToDetectException();
        } catch(EntityDoesNotExistException e) {
            AssertHelper.assertContains(courseId + "/" + nonExistingEmail, e.getMessage());
        }
        
        ______TS("failure: invalid parameters");
        
        String invalidEmail = "invalidEmail";
        attr.instructorEmail = invalidEmail;
        try {
            instructorPermissionsLogic.updateInstructorPermissionByEmail(attr, instrEmail);
            signalFailureToDetectException();
        } catch(InvalidParametersException e) {
            ignoreExpectedException();
        }       
    }
    
    @Test
    public void testDeleteInstructorPermission() {
        String instrEmail = "instrPermLogic1@google.com";
        String courseId = "courseIdForIPL";
        
        ______TS("typical case: successfully delete instructorPermission");
        
        instructorPermissionsLogic.deleteInstructorPermission(courseId, instrEmail);
        assertNull(instructorPermissionsLogic.getInstructorPermissionForEmail(courseId, instrEmail));
        String nonAffectedEmail = "instrPermLogic2@google.com";
        assertNotNull(instructorPermissionsLogic.getInstructorPermissionForEmail(courseId, nonAffectedEmail));
        
        ______TS("typical case: non-existing instructorPermission");
        
        instructorPermissionsLogic.deleteInstructorPermission(courseId, instrEmail);
        assertNull(instructorPermissionsLogic.getInstructorPermissionForEmail(courseId, instrEmail));
    }
    
    @Test
    public void testDeleteInstructorPermissionsForCourse() {
        String courseId = "courseIdForIPL";
        
        ______TS("typical case: successfully delete instructorPermissions");
        
        instructorPermissionsLogic.deleteInstructorPermissionsForCourse(courseId);
        assertEquals(0, instructorPermissionsLogic.getInstructorPermissionsForCourse(courseId).size());
        
        ______TS("typical case: non-existing instructorPermission");
        
        instructorPermissionsLogic.deleteInstructorPermissionsForCourse(courseId);
        assertEquals(0, instructorPermissionsLogic.getInstructorPermissionsForCourse(courseId).size());
    }
    
    @Test
    public void testIsAllowedForPrivilege() {
        String instrEmail = "instrPermLogic2@google.com";
        String nonExistingEmail = "nonExistingIPLPri@google.com";
        String courseId = "courseIdForIPL";
        String sectionId = "sectionId";
        String sessionId = "sessionId";
        
        ______TS("typical case: existing instructorPermission");
        
        assertFalse(instructorPermissionsLogic.isAllowedForPrivilege(courseId, instrEmail, 
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertTrue(instructorPermissionsLogic.isAllowedForPrivilege(courseId, instrEmail, sectionId,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertTrue(instructorPermissionsLogic.isAllowedForPrivilege(courseId, instrEmail, sectionId, sessionId,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
        
        ______TS("typical case: non-existing instructorPermission");
        
        assertTrue(instructorPermissionsLogic.isAllowedForPrivilege(courseId, nonExistingEmail, 
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertTrue(instructorPermissionsLogic.isAllowedForPrivilege(courseId, nonExistingEmail, sectionId,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTION));
        assertTrue(instructorPermissionsLogic.isAllowedForPrivilege(courseId, nonExistingEmail, sectionId, sessionId,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTION));
    }
    
    @AfterClass()
    public static void classTearDown() throws Exception {
        turnLoggingDown(InstructorsLogic.class);
        printTestClassFooter();
    }
}

package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.InstructorPermissionsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class InstructorPermissionsDbTest extends BaseComponentTestCase {
    
    private InstructorPermissionsDb instrPermissionsDb = new InstructorPermissionsDb();
    private String instrEmail = "instrPerm@gmail.com";
    private String courseId = "instrPermCourseId";
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(InstructorPermissionsDb.class);
    }
    
    @Test
    public void testCreateInstructorPermission() throws InvalidParametersException, EntityAlreadyExistsException {
        
        ______TS("Success: create an instructor");
        
        InstructorPermissionAttributes permission = getCoownerInstructorPermissionAttr();
        
        instrPermissionsDb.createEntity(permission);       
        TestHelper.verifyPresentInDatastore(permission);
        
        ______TS("Failure: create a duplicate entity");
        
        try {
            instrPermissionsDb.createEntity(permission);
            signalFailureToDetectException();
        } catch(EntityAlreadyExistsException e) {
            AssertHelper.assertContains(instrEmail + ", " + courseId, e.getMessage());
        }
        
        ______TS("Failure: create an entity with invalid parameters");
        
        String invalidRole = "invalidRole";
        permission.role = invalidRole;
        
        try {
            instrPermissionsDb.createEntity(permission);
            signalFailureToDetectException();
        } catch(InvalidParametersException e) {
            AssertHelper.assertContains("\"" + invalidRole + "\"", e.getMessage());
        }
        
        ______TS("Failure: null parameters");
        
        try {
            instrPermissionsDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
    }
    
    @Test
    public void testGetInstructorPermissionForEmail() throws InvalidParametersException, EntityAlreadyExistsException {
        // not going to use restoreTypicalDataInDatastore here
        //     separate dbTest from logic implementation
        createInstructorPermissions();
        
        ______TS("Success: get an instructorPermission");
        
        InstructorPermissionAttributes retrieved = instrPermissionsDb.getInstructorPermissionForEmail(courseId, instrEmail);
        assertNotNull(retrieved);
        
        ______TS("Failure: instructor does not exist");
        
        String nonexistInstrEmail = "nonExist@google.com";
        retrieved = instrPermissionsDb.getInstructorPermissionForEmail(courseId, nonexistInstrEmail);
        assertNull(retrieved);
        
        ______TS("Failure: null parameters");
        
        try {
            instrPermissionsDb.getInstructorPermissionForEmail(courseId, null);
            signalFailureToDetectException();
        } catch(AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
        try {
            instrPermissionsDb.getInstructorPermissionForEmail(null, instrEmail);
            signalFailureToDetectException();
        } catch(AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
    }
    
    @Test
    public void testGetInstructorPermissionsForEmail() throws InvalidParametersException {
        createInstructorPermissions();
        
        ______TS("Success: get instructorPermissions");
        
        String emailWithMultipleCourses = "instructorPerm1@coursePerm1.com";
        List<InstructorPermissionAttributes> instrPermissions = instrPermissionsDb.getInstructorPermissionsForEmail(emailWithMultipleCourses);
        assertEquals(3, instrPermissions.size());
        assertEquals(emailWithMultipleCourses, instrPermissions.get(0).instructorEmail);
        assertEquals(emailWithMultipleCourses, instrPermissions.get(1).instructorEmail);
        assertEquals(emailWithMultipleCourses, instrPermissions.get(2).instructorEmail);
        
        ______TS("Failure: instructorPermission does not exist");
        
        String nonExistInstrEmail = "nonExist@google.com";
        instrPermissions = instrPermissionsDb.getInstructorPermissionsForEmail(nonExistInstrEmail);
        assertEquals(0, instrPermissions.size());
        
        ______TS("Failure: null parameters");
        
        try {
            instrPermissionsDb.getInstructorPermissionsForEmail(null);
            signalFailureToDetectException();
        } catch(AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
    }
    
    @Test
    public void testGetInstructorPermissionsForCourse() throws InvalidParametersException {
        createInstructorPermissions();
        
        ______TS("Success: get instructorPermissions");
        
        String courseIdWithMultipleInstructorPermissions = "coursePerm1";
        List<InstructorPermissionAttributes> instrPermissions = instrPermissionsDb.getInstructorPermissionsForCourse(courseIdWithMultipleInstructorPermissions);
        assertEquals(3, instrPermissions.size());
        assertEquals(courseIdWithMultipleInstructorPermissions, instrPermissions.get(0).courseId);
        assertEquals(courseIdWithMultipleInstructorPermissions, instrPermissions.get(1).courseId);
        assertEquals(courseIdWithMultipleInstructorPermissions, instrPermissions.get(2).courseId);
        
        ______TS("Failure: instructorPermission does not exist");
        
        String nonExistCourseId = "nonExistCourseId";
        instrPermissions = instrPermissionsDb.getInstructorPermissionsForCourse(nonExistCourseId);
        assertEquals(0, instrPermissions.size());
        
        ______TS("Failure: null parameters");
        
        try {
            instrPermissionsDb.getInstructorPermissionsForCourse(null);
            signalFailureToDetectException();
        } catch(AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
    }
    
    @Test
    public void testUpdateInstructorPermissionByEmail() throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        instrEmail = "updateInstrPerm@google.com";
        courseId = "courseUpdateInstrPerm";
        InstructorPermissionAttributes permission = getCoownerInstructorPermissionAttr();       
        instrPermissionsDb.createEntity(permission);       
        TestHelper.verifyPresentInDatastore(permission);
        instrEmail = "updateInstrPerm1@google.com";
        courseId = "courseUpdateInstrPerm";
        permission = getCoownerInstructorPermissionAttr();       
        instrPermissionsDb.createEntity(permission);       
        TestHelper.verifyPresentInDatastore(permission);
        
        ______TS("Success: nothing updated");
        
        instrPermissionsDb.updateInstructorPermissionByEmail(permission, instrEmail);
        assertEquals(permission.instructorEmail, instrPermissionsDb.getInstructorPermissionForEmail(permission.courseId, 
                permission.instructorEmail).instructorEmail);
        assertEquals(permission.courseId, instrPermissionsDb.getInstructorPermissionForEmail(permission.courseId, 
                permission.instructorEmail).courseId);
        assertEquals(permission.role, instrPermissionsDb.getInstructorPermissionForEmail(permission.courseId, 
                permission.instructorEmail).role);
        assertEquals(permission.instructorPrivilegesAsText, instrPermissionsDb.getInstructorPermissionForEmail(permission.courseId, 
                permission.instructorEmail).instructorPrivilegesAsText);
        
        ______TS("Success: update instructorPermission");
        
        String updatedRole = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_HELPER;
        permission.role = updatedRole;
        instrPermissionsDb.updateInstructorPermissionByEmail(permission, instrEmail);
        assertEquals(updatedRole, instrPermissionsDb.getInstructorPermissionForEmail(permission.courseId, 
                permission.instructorEmail).role);
        
        ______TS("Success: update instructorPermission with email updated");
        
        String updatedInstrEmail = "updatedInstrPerm@google.com";
        permission.instructorEmail = updatedInstrEmail;
        instrPermissionsDb.updateInstructorPermissionByEmail(permission, instrEmail);
        assertEquals(updatedInstrEmail, instrPermissionsDb.getInstructorPermissionForEmail(permission.courseId, 
                permission.instructorEmail).instructorEmail);
        
        ______TS("Failure: update to existing instructorPermission");
        
        String updateToExisting = "updateInstrPerm@google.com";
        permission.instructorEmail = updateToExisting;      
        try {
            instrPermissionsDb.updateInstructorPermissionByEmail(permission, updatedInstrEmail);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(permission.courseId + "/" + permission.instructorEmail, e.getMessage());
        }
        
        ______TS("Failure: non-exist instructorPermission");
        
        String nonExistEmail = "nonExistEmail@google.com";
        try {
            instrPermissionsDb.updateInstructorPermissionByEmail(permission, nonExistEmail);
            signalFailureToDetectException();
        } catch(EntityDoesNotExistException e) {
            AssertHelper.assertContains(nonExistEmail, e.getMessage());
        }
        
        ______TS("Failure: invalid parameters");
        
        String invalidEmail = "invalidEmail";
        permission.instructorEmail = invalidEmail;
        try {
            instrPermissionsDb.updateInstructorPermissionByEmail(permission, instrEmail); 
            signalFailureToDetectException();
        } catch(InvalidParametersException e) {
            AssertHelper.assertContains("\"" + invalidEmail + "\"", e.getMessage());
        }
        
        ______TS("Failure: null parameters");
        
        try {
            instrPermissionsDb.updateInstructorPermissionByEmail(null, instrEmail);
            signalFailureToDetectException();
        } catch(AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
        try {
            instrPermissionsDb.updateInstructorPermissionByEmail(permission, null);
            signalFailureToDetectException();
        } catch(AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
    }
    
    @Test
    public void testDeleteInstructorPermission() throws InvalidParametersException {
        createInstructorPermissions();
        String instrEmailToDelete = "instructorPerm1@coursePerm1.com";
        String courseIdToDelete = "coursePerm1";
        InstructorPermissionAttributes permission = getCoownerInstructorPermissionAttr(); 
        
        ______TS("Success: delete entity");
        
        permission.instructorEmail = instrEmailToDelete;
        permission.courseId = courseIdToDelete;
        instrPermissionsDb.deleteInstructorPermission(courseIdToDelete, instrEmailToDelete);
        TestHelper.verifyAbsentInDatastore(permission);
        
        ______TS("Success: delete non-exist entity");
        
        String nonExistEmail = "nonExistEmail@google.com";
        instrPermissionsDb.deleteInstructorPermission(courseId, nonExistEmail);
        
        ______TS("Failure: null parameters");
        
        try {
            instrPermissionsDb.deleteInstructorPermission(null, instrEmail);
            signalFailureToDetectException();
        } catch(AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
        try {
            instrPermissionsDb.deleteInstructorPermission(courseId, null);
            signalFailureToDetectException();
        } catch(AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
    }
    
    @Test
    public void testDeleteInstructorPermissionsForCourse() throws InvalidParametersException {
        createInstructorPermissions();       
        String courseIdToDelete = "coursePerm1";
        InstructorPermissionAttributes permission = getCoownerInstructorPermissionAttr(); 
        
        ______TS("Success: typical case");
        
        instrPermissionsDb.deleteInstructorPermissionsForCourse(courseIdToDelete);
        String instrEmail1 = "instructorPerm1@coursePerm1.com";
        permission.instructorEmail = instrEmail1;
        permission.courseId = courseIdToDelete;
        TestHelper.verifyAbsentInDatastore(permission);
        String instrEmail2 = "instructorPerm2@coursePerm1.com";
        permission.instructorEmail = instrEmail2;
        TestHelper.verifyAbsentInDatastore(permission);
        String instrEmail3 = "instructorPerm3@coursePerm1.com";
        permission.instructorEmail = instrEmail3;
        TestHelper.verifyAbsentInDatastore(permission);
        
        ______TS("Failure: null parameters");
        
        try {
            instrPermissionsDb.deleteInstructorPermissionsForCourse(null);
            signalFailureToDetectException();
        } catch(AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
    }
    
    private void createInstructorPermissions() throws InvalidParametersException {
        instrEmail = "instructorPerm1@coursePerm1.com";
        courseId = "coursePerm1";
        InstructorPermissionAttributes permission = getCoownerInstructorPermissionAttr(); 
        try {
            instrPermissionsDb.createEntity(permission);       
            TestHelper.verifyPresentInDatastore(permission);
        } catch(EntityAlreadyExistsException e){
            ignoreExpectedException();
        }
        
        instrEmail = "instructorPerm2@coursePerm1.com";
        courseId = "coursePerm1";
        permission = getCoownerInstructorPermissionAttr();     
        try {
            instrPermissionsDb.createEntity(permission);       
            TestHelper.verifyPresentInDatastore(permission);
        } catch(EntityAlreadyExistsException e){
            ignoreExpectedException();
        }
        
        instrEmail = "instructorPerm3@coursePerm1.com";
        courseId = "coursePerm1";
        permission = getCoownerInstructorPermissionAttr();     
        try {
            instrPermissionsDb.createEntity(permission);       
            TestHelper.verifyPresentInDatastore(permission);
        } catch(EntityAlreadyExistsException e){
            ignoreExpectedException();
        }
        
        instrEmail = "instructorPerm1@coursePerm1.com";
        courseId = "coursePerm2";
        permission = getCoownerInstructorPermissionAttr();     
        try {
            instrPermissionsDb.createEntity(permission);       
            TestHelper.verifyPresentInDatastore(permission);
        } catch(EntityAlreadyExistsException e){
            ignoreExpectedException();
        }
        
        instrEmail = "instructorPerm1@coursePerm1.com";
        courseId = "coursePerm3";
        permission = getCoownerInstructorPermissionAttr();     
        try {
            instrPermissionsDb.createEntity(permission);       
            TestHelper.verifyPresentInDatastore(permission);
        } catch(EntityAlreadyExistsException e){
            ignoreExpectedException();
        }
    }
    
    private InstructorPermissionAttributes getCoownerInstructorPermissionAttr() {
        InstructorPrivileges instrPrivileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPermissionAttributes instrPermissionAttr = new InstructorPermissionAttributes(instrEmail, courseId, role, instrPrivileges);
        
        return instrPermissionAttr;
    }
}

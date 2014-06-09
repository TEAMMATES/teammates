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
            instrPermissionsDb.getInstructorPermissionForEmail(null, null);
        } catch(AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
    }
    
    @Test
    public void testGetInstructorPermissionsForEmail() throws InvalidParametersException {
        createInstructorPermissions();
        
        ______TS("Success: get an instructorPermission");
        
        String emailWithMultipleCourses = "instructorPerm1@coursePerm1.com";
        List<InstructorPermissionAttributes> instrPermissions = instrPermissionsDb.getInstructorPermissionsForEmail(emailWithMultipleCourses);
        assertEquals(3, instrPermissions.size());
        assertEquals(emailWithMultipleCourses, instrPermissions.get(0).instructorEmail);
        assertEquals(emailWithMultipleCourses, instrPermissions.get(1).instructorEmail);
        assertEquals(emailWithMultipleCourses, instrPermissions.get(2).instructorEmail);
        
        ______TS("Failure: instructorPermission does not exist");
        
        String nonexistInstrEmail = "nonExist@google.com";
        instrPermissions = instrPermissionsDb.getInstructorPermissionsForEmail(nonexistInstrEmail);
        assertEquals(0, instrPermissions.size());
        
        ______TS("Failure: null parameters");
        
        try {
            instrPermissionsDb.getInstructorPermissionsForEmail(null);
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

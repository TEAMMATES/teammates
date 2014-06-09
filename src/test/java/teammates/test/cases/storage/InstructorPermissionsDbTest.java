package teammates.test.cases.storage;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.InstructorPermissionsDb;
import teammates.storage.entity.InstructorPermission;
import teammates.test.cases.BaseComponentTestCase;

public class InstructorPermissionsDbTest extends BaseComponentTestCase {
    
    private InstructorPermissionsDb instrPermissionsDb = new InstructorPermissionsDb();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(InstructorPermissionsDb.class);
    }
    
    @Test
    public void testGetInstructorPermissionForEmail() throws InvalidParametersException, EntityAlreadyExistsException {
        InstructorPermissionAttributes permission = getCoownerInstructorPermissionAttr();
        
        instrPermissionsDb.createEntity(permission);
    }
    
    private InstructorPermissionAttributes getCoownerInstructorPermissionAttr() {
        InstructorPrivileges instrPrivileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String instrEmail = "instrPerm@gmail.com";
        String courseId = "instrPermCourseId";
        String role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPermissionAttributes instrPermissionAttr = new InstructorPermissionAttributes(instrEmail, courseId, role, instrPrivileges);
        
        return instrPermissionAttr;
    }
}

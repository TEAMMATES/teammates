package teammates.test.cases.storage;

import static teammates.common.util.FieldValidator.GOOGLE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

import java.security.InvalidParameterException;

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
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class InstructorPermissionsDbTest extends BaseComponentTestCase {
    
    private InstructorPermissionsDb instrPermissionsDb = new InstructorPermissionsDb();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(InstructorPermissionsDb.class);
    }
    
    @Test
    public void testGetInstructorPermissionForEmail() throws InvalidParametersException, EntityAlreadyExistsException {
        
        ______TS("Success: create an instructor");
        
        InstructorPermissionAttributes permission = getCoownerInstructorPermissionAttr();
        
        instrPermissionsDb.createEntity(permission);       
        TestHelper.verifyPresentInDatastore(permission);
        
        ______TS("Failure: create a duplicate entity");
        
        try {
            instrPermissionsDb.createEntity(permission);
            signalFailureToDetectException();
        } catch(InvalidParameterException e) {
            AssertHelper.assertContains(
                    String.format(GOOGLE_ID_ERROR_MESSAGE, "", REASON_INCORRECT_FORMAT),
                    e.getMessage());
        }
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

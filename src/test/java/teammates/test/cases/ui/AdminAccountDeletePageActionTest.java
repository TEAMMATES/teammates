package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.AccountsLogic;
import teammates.ui.controller.AdminAccountDeleteAction;
import teammates.ui.controller.RedirectResult;

public class AdminAccountDeletePageActionTest extends BaseActionTest {

    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        uri = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testExecuteAndPostProcess() {
        
        ______TS("success: delete entire account");
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.googleId,
                "account", "true"
        };
        
        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);
        
        AdminAccountDeleteAction deleteAction = getAction(submissionParams);
        RedirectResult result = (RedirectResult) deleteAction.executeAndPostProcess();
         
        assertNull(AccountsLogic.inst().getAccount(instructor1OfCourse1.googleId));
        assertEquals(Const.StatusMessages.INSTRUCTOR_ACCOUNT_DELETED, result.getStatusMessage());
        assertEquals("/admin/adminAccountManagementPage?error=false&user=admin.user",
                     result.getDestinationWithParams());
                
    }

    private AdminAccountDeleteAction getAction(String... params) {
        return (AdminAccountDeleteAction) gaeSimulation.getActionObject(uri, params);
    }
}

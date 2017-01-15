package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.controller.AdminAccountManagementPageAction;
import teammates.ui.controller.ShowPageResult;

public class AdminAccountManagementPageActionTest extends BaseActionTest {

    // private static final DataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        uri = Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE;
        // removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        
        ______TS("case: view admin acount management page");
        
        String[] submissionParams = new String[] {
        };

        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        AdminAccountManagementPageAction action = getAction(submissionParams);
        ShowPageResult result = (ShowPageResult) action.executeAndPostProcess();

        assertEquals("", result.getStatusMessage());
        assertEquals("/jsp/adminAccountManagement.jsp?error=false&user=admin.user",
                     result.getDestinationWithParams());
        assertFalse(result.isError);
                
    }

    private AdminAccountManagementPageAction getAction(String... params) {
        return (AdminAccountManagementPageAction) gaeSimulation.getActionObject(uri, params);
    }
    
}

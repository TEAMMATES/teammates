package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.AdminAccountDetailsPageAction;
import teammates.ui.controller.AdminAccountDetailsPageData;
import teammates.ui.controller.ShowPageResult;

public class AdminAccountDetailsPageActionTest extends BaseActionTest {

    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        uri = Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testExecuteAndPostProcess() {
        
        ______TS("case: view instructor account details");
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.googleId
        };

        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        AdminAccountDetailsPageAction action = getAction(submissionParams);
        ShowPageResult result = (ShowPageResult) action.executeAndPostProcess();

        assertEquals("", result.getStatusMessage());
        assertEquals("/jsp/adminAccountDetails.jsp?error=false&user=admin.user",
                     result.getDestinationWithParams());
        assertFalse(result.isError);

        AdminAccountDetailsPageData data = (AdminAccountDetailsPageData) result.data;
        assertEquals(instructor1OfCourse1.googleId, data.getAccountInformation().googleId);
                
    }

    private AdminAccountDetailsPageAction getAction(String... params) {
        return (AdminAccountDetailsPageAction) gaeSimulation.getActionObject(uri, params);
    }

}

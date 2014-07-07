package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.AdminAccountDetailsPageAction;
import teammates.ui.controller.AdminAccountDetailsPageData;
import teammates.ui.controller.ShowPageResult;

public class AdminAccountDetailsPageActionTest extends BaseActionTest {

    DataBundle dataBundle;
    
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        String[] submissionParams = new String[]{};
        verifyOnlyAdminsCanAccess(submissionParams);
    }

    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
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
        assertEquals(false, result.isError);

        AdminAccountDetailsPageData data = (AdminAccountDetailsPageData) result.data;
        assertEquals(instructor1OfCourse1.googleId, data.accountInformation.googleId);
                
    }
    

    private AdminAccountDetailsPageAction getAction(String... params) throws Exception {
        return (AdminAccountDetailsPageAction) (gaeSimulation.getActionObject(uri, params));
    }
    

}

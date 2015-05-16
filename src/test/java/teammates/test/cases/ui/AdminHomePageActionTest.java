package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.controller.Action;
import teammates.ui.controller.AdminHomePageData;
import teammates.ui.controller.ShowPageResult;

public class AdminHomePageActionTest extends BaseActionTest {

    // private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.ADMIN_HOME_PAGE;
        // removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        ______TS("Normal case: starting with a blank adminHome page");
        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);
        final Action a = getAction();
        
        final ShowPageResult result = (ShowPageResult) a.executeAndPostProcess();
        assertEquals( Const.ViewURIs.ADMIN_HOME, result.destination);
        final AdminHomePageData startingPageData = (AdminHomePageData) result.data;
        assertEquals("", startingPageData.instructorEmail);
        assertEquals("", startingPageData.instructorShortName);
        assertEquals("", startingPageData.instructorInstitution);
        assertEquals("", startingPageData.instructorName);
        assertEquals("", result.getStatusMessage());
        
    }
    
    private Action getAction(String... parameters) throws Exception {
        return (Action)gaeSimulation.getActionObject(uri, parameters);
    }


}

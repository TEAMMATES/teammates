package teammates.test.cases.action;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.controller.AdminHomePageAction;
import teammates.ui.controller.AdminHomePageData;
import teammates.ui.controller.ShowPageResult;

public class AdminHomePageActionTest extends BaseActionTest {

    // private final DataBundle dataBundle = getTypicalDataBundle();
    
    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_HOME_PAGE;
    }
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        // removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        
        ______TS("Normal case: starting with a blank adminHome page");
        final String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);
        final AdminHomePageAction a = getAction();
        
        final ShowPageResult result = (ShowPageResult) a.executeAndPostProcess();
        assertEquals(Const.ViewURIs.ADMIN_HOME, result.destination);
        final AdminHomePageData startingPageData = (AdminHomePageData) result.data;
        assertEquals("", startingPageData.instructorDetailsSingleLine);
        assertEquals("", startingPageData.instructorEmail);
        assertEquals("", startingPageData.instructorShortName);
        assertEquals("", startingPageData.instructorInstitution);
        assertEquals("", startingPageData.instructorName);
        assertEquals("", result.getStatusMessage());
        
    }
    
    @Override
    protected AdminHomePageAction getAction(String... parameters) {
        return (AdminHomePageAction) gaeSimulation.getActionObject(getActionUri(), parameters);
    }

}

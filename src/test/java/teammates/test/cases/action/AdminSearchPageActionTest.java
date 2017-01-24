package teammates.test.cases.action;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.controller.AdminSearchPageAction;

public class AdminSearchPageActionTest extends BaseActionTest {
    
    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_SEARCH_PAGE;
    }
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
    }
    
    @Override
    @Test
    public void testExecuteAndPostProcess() {
        // Test is done as a browser test,
        // because otherwise there are problems when rebuilding the document
    }
    
    @Override
    protected AdminSearchPageAction getAction(String... params) {
        return (AdminSearchPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }
    
}

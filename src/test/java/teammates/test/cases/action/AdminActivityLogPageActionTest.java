package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.controller.AdminActivityLogPageAction;

public class AdminActivityLogPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE;
    }
    
    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }
    
    @Override
    @Test
    public void testExecuteAndPostProcess() {
        
        //TODO: implement this
    }

    @Override
    protected AdminActivityLogPageAction getAction(String... params) {
        return (AdminActivityLogPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }
    
}

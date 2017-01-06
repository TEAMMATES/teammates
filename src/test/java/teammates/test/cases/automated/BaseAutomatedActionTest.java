package teammates.test.cases.automated;

import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import teammates.test.cases.BaseComponentTestCase;
import teammates.ui.automated.AutomatedAction;

/**
 * Base class for all automated actions tests.
 */
public abstract class BaseAutomatedActionTest extends BaseComponentTestCase {
    
    protected abstract String getActionUri();
    
    @BeforeClass
    public void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        loginAsAdmin();
    }
    
    @AfterClass
    public void classTearDown() {
        printTestClassFooter();
    }
    
    protected void loginAsAdmin() {
        gaeSimulation.loginAsAdmin("admin.user");
    }
    
    protected void verifyNoTasksAdded(AutomatedAction action) {
        Map<String, Integer> tasksAdded = action.getTaskQueuer().getTasksAdded();
        assertEquals(0, tasksAdded.keySet().size());
    }
    
    protected void verifySpecifiedTasksAdded(AutomatedAction action, String taskName, int taskCount) {
        Map<String, Integer> tasksAdded = action.getTaskQueuer().getTasksAdded();
        assertEquals(taskCount, tasksAdded.get(taskName).intValue());
    }
    
}

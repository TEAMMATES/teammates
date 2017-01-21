package teammates.test.cases.automated;

import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import teammates.common.util.EmailWrapper;
import teammates.test.cases.BaseComponentTestCase;
import teammates.ui.automated.AutomatedAction;

/**
 * Base class for all automated actions tests.
 */
public abstract class BaseAutomatedActionTest extends BaseComponentTestCase {
    
    protected abstract String getActionUri();
    
    protected abstract AutomatedAction getAction(String... submissionParams);
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
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
        Map<String, Integer> tasksAdded = action.getTaskQueuer().getNumberOfTasksAdded();
        assertEquals(0, tasksAdded.keySet().size());
    }
    
    protected void verifySpecifiedTasksAdded(AutomatedAction action, String taskName, int taskCount) {
        Map<String, Integer> tasksAdded = action.getTaskQueuer().getNumberOfTasksAdded();
        assertEquals(taskCount, tasksAdded.get(taskName).intValue());
    }
    
    protected void verifyNoEmailsSent(AutomatedAction action) {
        assertTrue(getEmailsSent(action).isEmpty());
    }
    
    protected List<EmailWrapper> getEmailsSent(AutomatedAction action) {
        return action.getEmailSender().getEmailsSent();
    }
    
    protected void verifyNumberOfEmailsSent(AutomatedAction action, int emailCount) {
        assertEquals(emailCount, action.getEmailSender().getEmailsSent().size());
    }
    
}

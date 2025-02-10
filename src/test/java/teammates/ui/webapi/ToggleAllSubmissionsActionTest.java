package teammates.ui.webapi.action;

import org.testng.annotations.Test;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

public class ToggleAllSubmissionsActionTest extends BaseActionTest<ToggleAllSubmissionsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.TOGGLE_ALL_SUBMISSIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    public void testExecute_typicalCase_shouldToggleAllSubmissions() {
        // Arrange
        String[] submissionIds = { "submission1", "submission2", "submission3" };
        loginAsStudent("studentEmail@example.com");

        // Act
        ToggleAllSubmissionsAction action = getAction(submissionIds);
        JsonResult result = getJsonResult(action);

        // Assert
        MessageOutput output = (MessageOutput) result.getOutput();
        assertEquals("All submissions toggled successfully", output.getMessage());
    }
}

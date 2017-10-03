package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.controller.AdminSearchPageAction;

/**
 * SUT: {@link AdminSearchPageAction}.
 */
public class AdminSearchPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_SEARCH_PAGE;
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

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyAdminsCanAccess(submissionParams);
    }

}

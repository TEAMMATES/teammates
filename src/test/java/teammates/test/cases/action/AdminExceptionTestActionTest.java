package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.controller.AdminExceptionTestAction;

/**
 * SUT: {@link AdminExceptionTestAction}.
 */
public class AdminExceptionTestActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_EXCEPTION_TEST;
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
    protected AdminExceptionTestAction getAction(String... params) {
        return (AdminExceptionTestAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

}

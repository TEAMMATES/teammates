package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.action.GetInstructorPrivilegeAction;

/**
 * SUT: {@link GetInstructorPrivilegeAction}.
 */
public class GetInstructorPrivilegeActionTest extends BaseActionTest<GetInstructorPrivilegeAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_PRIVILEGE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // TODO
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // TODO
    }

}

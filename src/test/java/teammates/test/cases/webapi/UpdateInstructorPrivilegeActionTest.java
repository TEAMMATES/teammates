package teammates.test.cases.webapi;

import teammates.common.util.Const;
import teammates.ui.webapi.action.UpdateInstructorPrivilegeAction;

/**
 * SUT: {@link UpdateInstructorPrivilegeAction}.
 */
public class UpdateInstructorPrivilegeActionTest extends BaseActionTest<UpdateInstructorPrivilegeAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_PRIVILEGE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    protected void testExecute() throws Exception {
        // TODO
    }

    @Override
    protected void testAccessControl() throws Exception {
        // TODO
    }
}

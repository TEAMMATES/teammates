package teammates.test.cases.newaction;

import org.apache.http.client.methods.HttpDelete;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.newcontroller.DeleteInstructorAction;

/**
 * SUT: {@link DeleteInstructorAction}.
 */
public class DeleteInstructorActionTest extends BaseActionTest<DeleteInstructorAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return HttpDelete.METHOD_NAME;
    }

    @Override
    @Test
    protected void testExecute() {
        // TODO
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}

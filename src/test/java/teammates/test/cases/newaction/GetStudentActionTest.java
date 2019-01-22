package teammates.test.cases.newaction;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.newcontroller.GetStudentAction;

/**
 * SUT: {@link GetStudentAction}.
 */
public class GetStudentActionTest extends BaseActionTest<GetStudentAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT;
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

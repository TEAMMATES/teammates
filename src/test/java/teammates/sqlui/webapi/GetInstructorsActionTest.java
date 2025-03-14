package teammates.sqlui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.GetInstructorsAction;

public class GetInstructorsActionTest extends BaseActionTest<GetInstructorsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTORS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void testExecute_invalidParams_throwsInvalidHttpParameterException() {

    }

}

package teammates.sqlui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.GetSessionResultsAction;

/**
 * SUT: {@link GetSessionResultsAction}.
 */
public class GetSessionResultsActionTest extends BaseActionTest<GetSessionResultsAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESULT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    void testExecute_

}

package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.ActionClasses;

/**
 * SUT: {@link GetActionClassesAction}.
 */
public class GetActionClassesActionTest extends BaseActionTest<GetActionClassesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACTION_CLASS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        ______TS("Retrieve the list of action class names");
        GetActionClassesAction action = getAction();
        action.execute();
        JsonResult result = getJsonResult(action);
        ActionClasses data = (ActionClasses) result.getOutput();
        List<String> expectedActionClasses = ActionFactory.ACTION_MAPPINGS.values().stream()
                .flatMap(map -> map.values().stream().map(Class::getSimpleName))
                .collect(Collectors.toList());

        assertEquals(expectedActionClasses, data.getActionClasses());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}

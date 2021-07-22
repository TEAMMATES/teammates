package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Set<String> actionClasses = new HashSet<>();
        for (Map<String, Class<? extends Action>> map : ActionFactory.ACTION_MAPPINGS.values()) {
            for (Class<? extends Action> action : map.values()) {
                actionClasses.add(action.getSimpleName());
            }
        }
        List<String> expectedActionClasses = new ArrayList<>(actionClasses);
        Collections.sort(expectedActionClasses);

        GetActionClassesAction action = getAction();
        action.execute();
        JsonResult result = getJsonResult(action);
        ActionClasses data = (ActionClasses) result.getOutput();
        List<String> actualActionClasses = data.getActionClasses();
        Collections.sort(actualActionClasses);

        assertEquals(expectedActionClasses, actualActionClasses);
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}

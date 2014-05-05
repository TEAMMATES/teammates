package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.ShowPageResult;

public class ActionResultTest extends BaseTestCase {
    
    @Test
    public void testAppendParameters(){
        Map<String, String[]> map = new HashMap<String, String[]>();
        ActionResult svr = new ShowPageResult("/page/instructorHome", null, map, null, null);
        assertEquals("/page/instructorHome", svr.getDestinationWithParams());
        svr.addResponseParam(Const.ParamsNames.STATUS_MESSAGE, "course deleted");
        assertEquals("/page/instructorHome?message=course+deleted", svr.getDestinationWithParams());
        svr.addResponseParam(Const.ParamsNames.USER_ID, "david");
        assertEquals("/page/instructorHome?message=course+deleted&user=david", svr.getDestinationWithParams());
    }

}

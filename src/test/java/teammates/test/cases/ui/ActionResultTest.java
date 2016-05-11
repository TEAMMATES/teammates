package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.ShowPageResult;

public class ActionResultTest extends BaseTestCase {
    
    @Test
    public void testAppendParameters(){
        ActionResult svr = new ShowPageResult("/page/instructorHome", null, null, null);
        assertEquals("/page/instructorHome", svr.getDestinationWithParams());
        svr.addResponseParam(Const.ParamsNames.USER_ID, "david");
        assertEquals("/page/instructorHome?user=david", svr.getDestinationWithParams());
    }

}

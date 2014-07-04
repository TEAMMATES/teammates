package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;

public class AdminAccountManagementPageActionTest extends BaseActionTest {

    // private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE;
        // restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        
        //TODO: implement this
    }
}

package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AdminSearchPageActionTest extends BaseActionTest {
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        // TODO : Test is done as a browser test,
        // cuz gaeSimulation gives problems when rebuilding the document
    }
    
}

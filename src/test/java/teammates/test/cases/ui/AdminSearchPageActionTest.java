package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AdminSearchPageActionTest extends BaseActionTest {
    
    @BeforeClass
    public static void classSetUp() {
        printTestClassHeader();
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        // Test is done as a browser test,
        // because otherwise there are problems when rebuilding the document
    }
    
}

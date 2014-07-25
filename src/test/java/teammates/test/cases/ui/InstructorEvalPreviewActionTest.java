package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;

public class InstructorEvalPreviewActionTest extends BaseActionTest {
    // private final DataBundle dataBundle = getTypicalDataBundle();
        
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		// removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_PREVIEW;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        //TODO: implement this
    }
}

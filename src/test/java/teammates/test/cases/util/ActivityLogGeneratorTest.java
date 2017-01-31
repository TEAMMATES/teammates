package teammates.test.cases.util;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.ActivityLogGenerator;
import teammates.test.cases.BaseComponentTestCase;

/**
 * SUT: {@link ActivityLogGenerator}
 */
public class ActivityLogGeneratorTest extends BaseComponentTestCase {
    
    ActivityLogGenerator logCenter = new ActivityLogGenerator();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
    }
    
    // TODO: may want to move all the things related to log in action test to here
    
    @Test
    public void testGenerateNormalActionLogMessage() {
        
    }
    
    @Test
    public void testGenerateServletActionFailureLogMessage() {
        
    }
    
    @Test
    public void testGenerateSystemErrorReportLogMessage() {
        
    }
    
    @AfterClass
    public void classTearDown() {
        printTestClassFooter();
    }
    
}

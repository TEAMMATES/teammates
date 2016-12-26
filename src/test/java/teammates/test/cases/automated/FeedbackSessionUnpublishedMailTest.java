package teammates.test.cases.automated;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.TimeHelper;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.util.Priority;

@Priority(-1)
public class FeedbackSessionUnpublishedMailTest extends BaseComponentTestCase {
    
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setup();
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }
    
    @Test(enabled = false)
    public void testFeedbackSessionUnpublishedMailAction() throws Exception {
        
        ______TS("Emails Test : set session 1 to unsent unpublished emails and unpublish");
        // unpublished session with emails unsent for unpublished session.
        FeedbackSessionAttributes session1 = dataBundle.feedbackSessions.get("session1InCourse1");
        session1.setResultsVisibleFromTime(TimeHelper.getDateOffsetToCurrentTime(+1));
        session1.setSentPublishedEmail(true);
        fsLogic.updateFeedbackSession(session1);
        
    }
    
}

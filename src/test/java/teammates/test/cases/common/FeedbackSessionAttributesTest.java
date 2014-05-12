package teammates.test.cases.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertEquals;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.util.FieldValidator;
import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;

public class FeedbackSessionAttributesTest extends BaseTestCase {
    private static String feedbackSessionName;
    private static String courseId;
    private static String creatorId;
    private static Text instructions;
    private static Date createdTime;
    private static Date startTime;
    private static Date endTime;
    private static Date sessionVisibleFromTime;
    private static Date resultsVisibleFromTime;
    private static double timeZone;
    private static int gracePeriod;
    private static FeedbackSessionType feedbackSessionType;
    private static boolean sentOpenEmail;
    private static boolean sentPublishedEmail;
    private static boolean isOpeningEmailEnabled;
    private static boolean isClosingEmailEnabled;
    private static boolean isPublishedEmailEnabled;
    
    private static FeedbackSessionAttributes fsa;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        
        feedbackSessionName = null;
        courseId = null;
        creatorId = null;
        instructions = null;
        createdTime = null;
        startTime = TimeHelper.combineDateTime("09/05/2016", "1000");
        endTime = TimeHelper.combineDateTime("09/05/2017", "1000");
        sessionVisibleFromTime = null;
        resultsVisibleFromTime = null;
        timeZone = 8;
        gracePeriod = 15;
        feedbackSessionType = FeedbackSessionType.STANDARD;
        sentOpenEmail = false;
        sentPublishedEmail = false;
        isOpeningEmailEnabled = false;
        isClosingEmailEnabled = false;
        isPublishedEmailEnabled = false;
        
        fsa = new FeedbackSessionAttributes(feedbackSessionName,
                courseId, creatorId, instructions,
                createdTime, startTime, endTime,
                sessionVisibleFromTime, resultsVisibleFromTime,
                timeZone, gracePeriod,
                feedbackSessionType, sentOpenEmail, sentPublishedEmail,
                isOpeningEmailEnabled, isClosingEmailEnabled, isPublishedEmailEnabled
                );
    }
    
    @Test
    public void testBasicGetters() {
        ______TS("get session stime, etime, name");
        
        assertEquals(fsa.getSessionEndTime(), endTime);
        assertEquals(fsa.getSessionStartTime(), startTime);
        assertEquals(fsa.getSessionName(), feedbackSessionName);
        
    }
    
    @Test
    public void testValidate() {
        
        ______TS("null parameter error messages");

        List<String> expectedErrorMessage = new ArrayList<String>();
        String[] fieldNames = new String[]{
                "feedback session name", 
                "course ID", 
                "instructions to students", 
                "time for the session to become visible", 
                "creator's email", 
                "session creation time"};
        for  (String fieldName : fieldNames) {
            expectedErrorMessage.add(String.format(FieldValidator.NON_NULL_FIELD_ERROR_MESSAGE, fieldName));
        }
        
        //expect all the error messages to be appended together.
        assertEquals(fsa.getInvalidityInfo(), expectedErrorMessage);
        
        ______TS("invalid parameters error messages");
        
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
}

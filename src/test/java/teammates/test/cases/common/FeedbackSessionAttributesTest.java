package teammates.test.cases.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.repackaged.org.joda.time.DateTime;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.util.Assumption;
import teammates.common.util.FieldValidator;
import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;

public class FeedbackSessionAttributesTest extends BaseTestCase {
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testValidate() {
        // TODO: follow test sequence similar to evalTest
        String feedbackSessionName = null, courseId = null, creatorId = null;
        Text instructions = null;
        Date createdTime = null;
        Date startTime = TimeHelper.combineDateTime("09/05/2016", "1000");
        Date endTime = TimeHelper.combineDateTime("09/05/2017", "1000");
        Date sessionVisibleFromTime = null;
        Date resultsVisibleFromTime = null;
        double timeZone = 8;
        int gracePeriod = 15;
        FeedbackSessionType feedbackSessionType = FeedbackSessionType.STANDARD;
        boolean sentOpenEmail = false, sentPublishedEmail = false;
        boolean isOpeningEmailEnabled = false, isClosingEmailEnabled = false, isPublishedEmailEnabled = false;
        
        ______TS("null parameter error messages");

        FeedbackSessionAttributes fsa = new FeedbackSessionAttributes(feedbackSessionName,
                courseId, creatorId, instructions,
                createdTime, startTime, endTime,
                sessionVisibleFromTime, resultsVisibleFromTime,
                timeZone, gracePeriod,
                feedbackSessionType, sentOpenEmail, sentPublishedEmail,
                isOpeningEmailEnabled, isClosingEmailEnabled, isPublishedEmailEnabled
                );
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
        Assumption.assertEquals(fsa.getInvalidityInfo(), expectedErrorMessage);
        
        ______TS("invalid parameters error messages");
        
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
}

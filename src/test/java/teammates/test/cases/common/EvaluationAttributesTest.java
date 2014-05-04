package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.common.util.Const.EOL;
import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.END_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.EVALUATION_NAME;
import static teammates.common.util.FieldValidator.EVALUATION_NAME_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_EMPTY;
import static teammates.common.util.FieldValidator.START_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.TIME_FRAME_ERROR_MESSAGE;

import java.util.Calendar;
import java.util.TimeZone;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Utils;
import teammates.storage.entity.Evaluation;
import teammates.test.cases.BaseTestCase;

public class EvaluationAttributesTest extends BaseTestCase {


    @Test
    public void testCalculateEvalStatus() throws InterruptedException {
        
        double timeZone;
        int gracePeriod;
        EvaluationAttributes evaluation = new EvaluationAttributes();
        int safetyMargin = 1000; // we use this to compensate for test execution time

        ______TS("in the awaiting period");

        evaluation.startTime = TimeHelper.getMsOffsetToCurrentTime(safetyMargin);
        evaluation.endTime = TimeHelper.getDateOffsetToCurrentTime(1);

        timeZone = 0.0;
        evaluation.timeZone = timeZone;

        gracePeriod = 0;
        ;
        evaluation.gracePeriod = gracePeriod;

        evaluation.published = false;
        AssertJUnit.assertEquals(EvalStatus.AWAITING, evaluation.getStatus());

        ______TS("in the middle of open period");

        evaluation.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        AssertJUnit.assertEquals(EvalStatus.OPEN, evaluation.getStatus());

        ______TS("just before grace period expires");

        gracePeriod = 5;
        int gracePeriodInMs = gracePeriod * 60 * 1000;
        evaluation.gracePeriod = gracePeriod;
        
        evaluation.endTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(
                gracePeriodInMs - safetyMargin, timeZone);
        
        timeZone = 0.0;
        evaluation.timeZone = timeZone;
        
        AssertJUnit.assertEquals(EvalStatus.OPEN, evaluation.getStatus());

        ______TS("just after the grace period expired");

        gracePeriod = 5;
        gracePeriodInMs = gracePeriod * 60 * 1000;
        evaluation.gracePeriod = gracePeriod;
        
        timeZone = 0.0;
        evaluation.timeZone = timeZone;
        
        evaluation.endTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(
                - gracePeriodInMs - safetyMargin, timeZone);
        
        AssertJUnit.assertEquals(EvalStatus.CLOSED, evaluation.getStatus());

        ______TS("already published");
        
        evaluation.published = true;
        AssertJUnit.assertEquals(EvalStatus.PUBLISHED, evaluation.getStatus());
        evaluation.published = false;

        ______TS("checking for user in different time zone");
        // do similar testing for +1.0 time zone
        
        timeZone = 1.0;
        evaluation.timeZone = timeZone;

        // in AWAITING period
        evaluation.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(
                safetyMargin, timeZone);
        evaluation.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        AssertJUnit.assertEquals(EvalStatus.AWAITING, evaluation.getStatus());

        // in OPEN period
        evaluation.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(
                -safetyMargin, timeZone);
        AssertJUnit.assertEquals(EvalStatus.OPEN, evaluation.getStatus());

        //TODO: just before grace period expired
        
        gracePeriod = 5;
        evaluation.gracePeriod = gracePeriod;

        timeZone = 1.0;
        evaluation.timeZone = timeZone;

        evaluation.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        evaluation.endTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(
                - gracePeriodInMs + safetyMargin, timeZone);

        AssertJUnit.assertEquals(EvalStatus.OPEN, evaluation.getStatus());
        
        // just after grace period
        gracePeriod = 5;
        evaluation.gracePeriod = gracePeriod;

        timeZone = 1.0;
        evaluation.timeZone = timeZone;

        evaluation.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        evaluation.endTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(
                - gracePeriodInMs - safetyMargin, timeZone);

        AssertJUnit.assertEquals(EvalStatus.CLOSED, evaluation.getStatus());

        // already PUBLISHED
        evaluation.published = true;
        AssertJUnit.assertEquals(EvalStatus.PUBLISHED, evaluation.getStatus());
    }

    @Test
    public void testValidate() {
        EvaluationAttributes e = new EvaluationAttributes();

        e.courseId = "";
        e.name = "";
        e.instructions = new Text("Instruction to students.");
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        e.activated = false;
        e.published = false;
        e.timeZone = 0.0;
        e.gracePeriod = 5;
        e.p2pEnabled = true;
        
        assertEquals("invalid values", false, e.isValid());
        String errorMessage = 
                String.format(COURSE_ID_ERROR_MESSAGE, e.courseId, REASON_EMPTY) + EOL 
                + String.format(EVALUATION_NAME_ERROR_MESSAGE, e.name, REASON_EMPTY);
        assertEquals("valid values", errorMessage, StringHelper.toString(e.getInvalidityInfo()));

        e.courseId = "valid-course";
        e.name = "valid name";
        e.instructions = new Text("valid instructions");
        assertTrue("valid, minimal properties", e.isValid());


        assertEquals("valid values", true, e.isValid());
        
        e.startTime = null;
        try {
            e.getInvalidityInfo();
            signalFailureToDetectException("null start time not detected");
        } catch (AssertionError e1) {
            ignoreExpectedException();
        }
        
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.endTime = null;
        try {
            e.getInvalidityInfo();
            signalFailureToDetectException("null end time not detected");
        } catch (AssertionError e1) {
            ignoreExpectedException();
        }

        
        // SUCCESS : end == start
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.startTime = e.endTime;
        assertTrue(e.isValid());
        
        // FAIL : end before start
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(2);
        assertFalse(e.isValid());
        errorMessage = String.format(TIME_FRAME_ERROR_MESSAGE, 
                END_TIME_FIELD_NAME, EVALUATION_NAME, START_TIME_FIELD_NAME);
        assertEquals(errorMessage, 
                StringHelper.toString(e.getInvalidityInfo()));

        // FAIL : published before endtime: invalid
        e.published = true;
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(0);
        e.endTime = TimeHelper.getMsOffsetToCurrentTime(5);
        assertFalse(e.isValid());
        assertEquals(FieldValidator.EVALUATION_END_TIME_ERROR_MESSAGE,
                StringHelper.toString(e.getInvalidityInfo()));

        // SUCCESS : just after endtime and published: valid
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        e.endTime = TimeHelper.getMsOffsetToCurrentTime(-5);
        e.published = true;
        assertTrue(e.isValid());

        // FAIL : activated before start time: invalid
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        e.published = false;
        e.activated = true;
        assertFalse(e.isValid());
        assertEquals(FieldValidator.EVALUATION_START_TIME_ERROR_MESSAGE,
            StringHelper.toString(e.getInvalidityInfo()));
    }
    
    @Test
    public void testGetInvalidStateInfo(){
        //already tested in testValidate() above
    }
    
    @Test
    public void testIsValid(){
        //already tested in testValidate() above
    }
    
    @Test
    public void testSetDerivedAttributes() throws Exception{

        ______TS("state change PUBLISHED --> * ");
        
        //We test for different timezones as the result depends on the timezone.
        verifyEvalUpdateFromPublishedToOpen(0);
        verifyEvalUpdateFromPublishedToOpen(5);
        verifyEvalUpdateFromPublishedToOpen(-5);
        
        // PUBLISHED --> AWAITING is similar to the above.
        // PUBLISHED --> CLOSED is not possible via the update method.
        
        ______TS("state change CLOSED --> * ");
        
        verifyEvalUpdateFromClosedToAwaiting(0);
        verifyEvalUpdateFromClosedToAwaiting(8);
        verifyEvalUpdateFromClosedToAwaiting(-12);
        
        // CLOSED --> OPEN doesn't need any changes to derived attributes.
        // ClOSED --> PUBLISHED is not possible via the update method.
        
        ______TS("state change OPEN --> * ");
        
        // OPEN --> AWAITING is similar to CLOSED-->AWAITING tested above.
        // OPEN --> CLOSED doesn't need any changes to derived attributes.
        // OPEN --> PUBLISHED is not possible via the update method.
        
        ______TS("state change AWAITING --> * ");
        
        verifyEvalUpdateFromAwaitingToClosed(0);
        verifyEvalUpdateFromAwaitingToClosed(10);
        verifyEvalUpdateFromAwaitingToClosed(-12);
        
        // AWAITING --> OPEN doesn't need any changes to derived attributes. 
        //     The reminder servlet will set the 'activated' attribute automatically.
        // AWAITING --> PUBLISHED is not possible via the update method.
    }
    
    @Test
    public void testIsReady() throws InvalidParametersException {

        // Create evaluation object to use as the test object
        Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar end = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        EvaluationAttributes e = new EvaluationAttributes(
                new Evaluation("course1", "evalution 1", new Text("instructions"),
                true, start.getTime(), end.getTime(), 0.0, 0));
        int oneSecInMilliSeconds = 1 * 1000;
        double timeZone = 0.0;
        
        ______TS("ready, just after start time");

        // start time set to 1 sec before current time
        e.startTime = TimeHelper.getMsOffsetToCurrentTime(-oneSecInMilliSeconds);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.activated = false;
        e.timeZone = 0.0;
        assertEquals(true, e.isReadyToActivate());
        
        // negative time zone, starting just before current time
        timeZone = -2.0;
        e.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(-oneSecInMilliSeconds,timeZone);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.activated = false;
        e.timeZone = timeZone;
        assertEquals(true, e.isReadyToActivate());
        
        
        // positive time zone, starting just before current time
        timeZone = 2.0;
        e.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(-oneSecInMilliSeconds, timeZone);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.activated = false;
        e.timeZone = timeZone;
        assertEquals(true, e.isReadyToActivate());
        
        ______TS("not ready, just before start time");
        //start time set to 1 sec after current time
        oneSecInMilliSeconds = 1 * 1000;
        e.startTime = TimeHelper.getMsOffsetToCurrentTime(+oneSecInMilliSeconds);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.activated = false;
        e.timeZone = 0.0;
        assertEquals(false, e.isReadyToActivate());

        // negative time zone, starting just after current time
        timeZone = -2.0;
        e.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(oneSecInMilliSeconds, timeZone);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.activated = false;
        e.timeZone = timeZone;
        assertEquals(false, e.isReadyToActivate());
        
        // positive time zone, starting just after current time
        timeZone = 2.0;
        e.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(oneSecInMilliSeconds, timeZone);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.activated = false;
        e.timeZone = timeZone;
        assertEquals(false, e.isReadyToActivate());
        
        ______TS("not ready, already activated");
        
        // start time set to 1 sec before current time
        e.startTime = TimeHelper.getMsOffsetToCurrentTime(-oneSecInMilliSeconds);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.activated = true;
        e.timeZone = 0.0;
        assertEquals(false, e.isReadyToActivate());

        // start time set to 1 sec after current time
        e.startTime = TimeHelper.getMsOffsetToCurrentTime(+oneSecInMilliSeconds);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.activated = true;
        e.timeZone = 0.0;
        assertEquals(false, e.isReadyToActivate());

    }
    
    @Test
    public void testSanitizeForSaving(){
        EvaluationAttributes e = generateValidEvaluationAttributesObject();
        EvaluationAttributes original = e.getCopy(); 
        
        //make it unsanitized
        e.courseId = "  "+e.courseId+ "   ";
        e.name = "\t "+ e.name+ "  \t";
        e.instructions = new Text("   " + e.instructions.getValue() + "\n\t  ");
        
        e.sanitizeForSaving();
        
        assertEquals(original.toString(), e.toString());
    }
    
    @Test
    public void testToString() {
        EvaluationAttributes e ;
        e = generateValidEvaluationAttributesObject();

        String inStringFormat = Utils.getTeammatesGson().toJson(e,
                EvaluationAttributes.class);
        assertEquals(inStringFormat, e.toString());

    }

    public static EvaluationAttributes generateValidEvaluationAttributesObject() {
        EvaluationAttributes e;
        e = new EvaluationAttributes();

        e.courseId = "valid-course";
        e.name = "valid name";
        e.instructions = new Text("1st line of instructions \n 2nd line of instructions");
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        e.activated = false;
        e.published = false;
        e.timeZone = 0.0;
        e.gracePeriod = 5;
        e.p2pEnabled = true;
        return e;
    }
    
private void verifyEvalUpdateFromPublishedToOpen(int timeZone) throws Exception{
        
        EvaluationAttributes eval = getTypicalDataBundle().evaluations.get("evaluation1InCourse1");
        int milliSecondsPerMinute = 60*1000;
        
        //first, make it PUBLISHED
        eval.timeZone = timeZone;
        eval.gracePeriod = 15;
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval.endTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(-milliSecondsPerMinute, timeZone);
        eval.activated = true;
        eval.published = true;
        assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
        
        //then, make it OPEN
        eval.endTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(-milliSecondsPerMinute*(eval.gracePeriod-1), timeZone);
        eval.setDerivedAttributes();
        
        //check if derived attributes are set correctly
        assertEquals( true, eval.activated);
        assertEquals( false, eval.published);
        assertEquals(EvalStatus.OPEN, eval.getStatus());
    }
    
    private void verifyEvalUpdateFromClosedToAwaiting(int timeZone) throws Exception{
        
        EvaluationAttributes eval = getTypicalDataBundle().evaluations.get("evaluation1InCourse1");
        int milliSecondsPerMinute = 60*1000;
        
        //first, make it CLOSED
        eval.timeZone = timeZone;
        eval.gracePeriod = 15;
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval.endTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(-milliSecondsPerMinute*(eval.gracePeriod+1), timeZone);
        eval.published = false;
        eval.activated = true;
        assertEquals(EvalStatus.CLOSED, eval.getStatus());
        
        //then, make it AWAITING
        eval.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(milliSecondsPerMinute,timeZone);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        eval.setDerivedAttributes();
        
        //check if derived attributes are set correctly
        assertEquals( false, eval.activated);
        assertEquals( false, eval.published);
        assertEquals(EvalStatus.AWAITING, eval.getStatus());
    }
    
    private void verifyEvalUpdateFromAwaitingToClosed(int timeZone) throws Exception{
        
        EvaluationAttributes eval = getTypicalDataBundle().evaluations.get("evaluation1InCourse1");
        int milliSecondsPerMinute = 60*1000;
        
        //first, make it AWAITING
        eval.timeZone = timeZone;
        eval.gracePeriod = 15;
        eval.startTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(milliSecondsPerMinute,timeZone);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(2);
        eval.published = false;
        eval.activated = false;
        assertEquals(EvalStatus.AWAITING, eval.getStatus());
        
        
        //then, make it CLOSED
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval.endTime = TimeHelper.getMsOffsetToCurrentTimeInUserTimeZone(-milliSecondsPerMinute*(eval.gracePeriod+1), timeZone);
        eval.setDerivedAttributes();
        
        //check if derived attributes are set correctly
        assertEquals( true, eval.activated);
        assertEquals( false, eval.published);
        assertEquals(EvalStatus.CLOSED, eval.getStatus());
    }
    
}

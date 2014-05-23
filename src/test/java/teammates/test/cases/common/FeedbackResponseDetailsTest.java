package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackAbstractResponseDetails;
import teammates.common.datatransfer.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

/**
 * Tests methods contained in Feedback*ResponseDetails classes. <br>
 * There is no need to test methods that output string/html/csv 
 * as they are implicitly tested in the UI tests. <br><br>
 * SUT: <br>
 * * {@link FeedbackAbstractResponseDetails} <br>
 * * {@link FeedbackTextResponseDetails} <br>
 * * {@link FeedbackMcqResponseDetails} <br>
 * * {@link FeedbackMsqResponseDetails} <br>
 * * {@link FeedbackNumericalScaleResponseDetails}
 */
public class FeedbackResponseDetailsTest extends BaseTestCase {
    @Test
    public void testCreateResponseDetails() throws Exception {
        Map<String, String[]> httpParams = new HashMap<String, String[]>();
        
        ______TS("TEXT Response");
        FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
        
        FeedbackAbstractResponseDetails responseDetails =
                FeedbackAbstractResponseDetails.createResponseDetails(
                        new String[] { "text answer" },
                        FeedbackQuestionType.TEXT,
                        textQuestionDetails);
        
        assertEquals(responseDetails.questionType, FeedbackQuestionType.TEXT);
        assertTrue(responseDetails instanceof FeedbackTextResponseDetails);
        assertEquals("text answer", responseDetails.getAnswerString());

        ______TS("MCQ Response: other disabled");
        FeedbackMcqQuestionDetails mcqQuestionDetails = new FeedbackMcqQuestionDetails();
        
        responseDetails = 
                FeedbackAbstractResponseDetails.createResponseDetails(
                        new String[] { "mcq option" },
                        FeedbackQuestionType.MCQ,
                        mcqQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.MCQ);
        assertTrue(responseDetails instanceof FeedbackMcqResponseDetails);
        assertEquals("mcq option", responseDetails.getAnswerString());
        
        ______TS("MSQ Response: other disabled");
        FeedbackMsqQuestionDetails msqQuestionDetails = new FeedbackMsqQuestionDetails();
        
        responseDetails = 
                FeedbackAbstractResponseDetails.createResponseDetails(
                        new String[] { "msq option 1", "msq option 2", "msq option 3" },
                        FeedbackQuestionType.MSQ,
                        msqQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.MSQ);
        assertTrue(responseDetails instanceof FeedbackMsqResponseDetails);
        assertEquals("msq option 1, msq option 2, msq option 3", responseDetails.getAnswerString());
        
        ______TS("NUMSCALE Response: typical case");
        FeedbackNumericalScaleQuestionDetails numericalScaleQuestionDetails = new FeedbackNumericalScaleQuestionDetails();
        numericalScaleQuestionDetails.maxScale = 5;
        numericalScaleQuestionDetails.minScale = -5;
        
        responseDetails = 
                FeedbackAbstractResponseDetails.createResponseDetails(
                        new String[] { "-3.5" },
                        FeedbackQuestionType.NUMSCALE,
                        numericalScaleQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.NUMSCALE);
        assertTrue(responseDetails instanceof FeedbackNumericalScaleResponseDetails);
        assertEquals("-3.5", responseDetails.getAnswerString());
        
        ______TS("NUMSCALE Response: more than max");
        
        responseDetails = 
                FeedbackAbstractResponseDetails.createResponseDetails(
                        new String[] { "9" },
                        FeedbackQuestionType.NUMSCALE,
                        numericalScaleQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.NUMSCALE);
        assertTrue(responseDetails instanceof FeedbackNumericalScaleResponseDetails);
        assertEquals("5", responseDetails.getAnswerString());
        
        ______TS("NUMSCALE Response: less than min");
        
        responseDetails = 
                FeedbackAbstractResponseDetails.createResponseDetails(
                        new String[] { "-10" },
                        FeedbackQuestionType.NUMSCALE,
                        numericalScaleQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.NUMSCALE);
        assertTrue(responseDetails instanceof FeedbackNumericalScaleResponseDetails);
        assertEquals("-5", responseDetails.getAnswerString());
        
        ______TS("NUMSCALE Response: wrong format");
        
        responseDetails = 
                FeedbackAbstractResponseDetails.createResponseDetails(
                        new String[] { "-0.5.3" },
                        FeedbackQuestionType.NUMSCALE,
                        numericalScaleQuestionDetails);

        assertNull(responseDetails);
        
        ______TS("NUMSCALE Response: fake http hidden value");
        
        httpParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN + "-1-0", new String[] {"-5"});
        httpParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX + "-1-0", new String[] {"9"});
        
        responseDetails = 
                FeedbackAbstractResponseDetails.createResponseDetails(
                        new String[] { "9" },
                        FeedbackQuestionType.NUMSCALE,
                        numericalScaleQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.NUMSCALE);
        assertTrue(responseDetails instanceof FeedbackNumericalScaleResponseDetails);
        assertEquals("5", responseDetails.getAnswerString());
    }
}

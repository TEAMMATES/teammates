package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackResponseDetails;
import teammates.common.datatransfer.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.FeedbackRubricResponseDetails;
import teammates.common.datatransfer.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.FeedbackTextResponseDetails;
import teammates.test.cases.BaseTestCase;

/**
 * Tests methods contained in Feedback*ResponseDetails classes. <br>
 * There is no need to test methods that output string/html/csv 
 * as they are implicitly tested in the UI tests. <br><br>
 * SUT: <br>
 * * {@link FeedbackResponseDetails} <br>
 * * {@link FeedbackTextResponseDetails} <br>
 * * {@link FeedbackMcqResponseDetails} <br>
 * * {@link FeedbackMsqResponseDetails} <br>
 * * {@link FeedbackNumericalScaleResponseDetails}
 */
public class FeedbackResponseDetailsTest extends BaseTestCase {
    @Test
    public void testCreateResponseDetails() throws Exception {
        
        ______TS("TEXT Response");
        FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
        
        FeedbackResponseDetails responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "text answer" },
                        FeedbackQuestionType.TEXT,
                        textQuestionDetails);
        
        assertEquals(responseDetails.questionType, FeedbackQuestionType.TEXT);
        assertTrue(responseDetails instanceof FeedbackTextResponseDetails);
        assertEquals("text answer", responseDetails.getAnswerString());

        ______TS("MCQ Response: other disabled");
        FeedbackMcqQuestionDetails mcqQuestionDetails = new FeedbackMcqQuestionDetails();
        
        responseDetails = 
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "mcq option" },
                        FeedbackQuestionType.MCQ,
                        mcqQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.MCQ);
        assertTrue(responseDetails instanceof FeedbackMcqResponseDetails);
        assertEquals("mcq option", responseDetails.getAnswerString());
        
        ______TS("MSQ Response: other disabled");
        FeedbackMsqQuestionDetails msqQuestionDetails = new FeedbackMsqQuestionDetails();
        
        responseDetails = 
                FeedbackResponseDetails.createResponseDetails(
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
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "-3.5" },
                        FeedbackQuestionType.NUMSCALE,
                        numericalScaleQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.NUMSCALE);
        assertTrue(responseDetails instanceof FeedbackNumericalScaleResponseDetails);
        assertEquals("-3.5", responseDetails.getAnswerString());
        
        ______TS("NUMSCALE Response: wrong format");
        
        responseDetails = 
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "-0.5.3" },
                        FeedbackQuestionType.NUMSCALE,
                        numericalScaleQuestionDetails);

        assertNull(responseDetails);

        ______TS("CONSTSUM Response: typical case");
        String questionText = "question text";
        int numOfConstSumOptions = 2;
        List<String> constSumOptions = new ArrayList<String>();
        constSumOptions.add("Option 1");
        constSumOptions.add("Option 2");
        boolean pointsPerOption = false;
        int points = 100;
        boolean forceUnevenDistribution = false;
        FeedbackConstantSumQuestionDetails constantSumQuestionDetails =
                new FeedbackConstantSumQuestionDetails(questionText, numOfConstSumOptions, 
                                                    constSumOptions, pointsPerOption, points, forceUnevenDistribution);
        
        responseDetails = 
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "20", "80" },
                        FeedbackQuestionType.CONSTSUM,
                        constantSumQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.CONSTSUM);
        assertTrue(responseDetails instanceof FeedbackConstantSumResponseDetails);
        assertEquals("20, 80", responseDetails.getAnswerString());
        
        ______TS("CONTRIB Response: typical case");
        questionText = "question text";
        FeedbackContributionQuestionDetails contribQuestionDetails =
                new FeedbackContributionQuestionDetails(questionText);
        
        responseDetails = 
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "100" },
                        FeedbackQuestionType.CONTRIB,
                        contribQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.CONTRIB);
        assertTrue(responseDetails instanceof FeedbackContributionResponseDetails);
        assertEquals("100", responseDetails.getAnswerString());
        
        ______TS("RUBRIC Response: invalid indexes in response");
        questionText = "question text";
        FeedbackRubricQuestionDetails rubricQuestionDetails =
                new FeedbackRubricQuestionDetails(questionText);
        
        responseDetails = 
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "0-0,1-0" },
                        FeedbackQuestionType.RUBRIC,
                        rubricQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.RUBRIC);
        assertTrue(responseDetails instanceof FeedbackRubricResponseDetails);
        assertEquals("[]", responseDetails.getAnswerString());
        
        ______TS("RUBRIC Response: typical case");
        rubricQuestionDetails.numOfRubricChoices++;
        rubricQuestionDetails.rubricChoices.add("choice1");
        rubricQuestionDetails.numOfRubricSubQuestions++;
        rubricQuestionDetails.rubricSubQuestions.add("sub-qn1");
        rubricQuestionDetails.numOfRubricSubQuestions++;
        rubricQuestionDetails.rubricSubQuestions.add("sub-qn2");
        responseDetails = 
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "0-0,1-0" },
                        FeedbackQuestionType.RUBRIC,
                        rubricQuestionDetails);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.RUBRIC);
        assertTrue(responseDetails instanceof FeedbackRubricResponseDetails);
        assertEquals("[0, 0]", responseDetails.getAnswerString());

    }
}

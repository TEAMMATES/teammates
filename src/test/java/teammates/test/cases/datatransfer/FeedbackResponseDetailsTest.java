package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackNumericalScaleResponseDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackResponseDetails},
 *      {@link FeedbackTextResponseDetails},
 *      {@link FeedbackMcqResponseDetails},
 *      {@link FeedbackMsqResponseDetails},
 *      {@link FeedbackNumericalScaleResponseDetails}.
 */
public class FeedbackResponseDetailsTest extends BaseTestCase {
    @Test
    public void testCreateResponseDetails() {

        ______TS("TEXT Response");
        FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
        HashMap<String, String[]> requestParameters = new HashMap<>();
        requestParameters.put("questiontype-1", new String[] { "TEXT" });
        requestParameters.put("responsetext-1-0", new String[] { "text answer" });

        FeedbackResponseDetails responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "text answer" },
                        FeedbackQuestionType.TEXT,
                        textQuestionDetails, requestParameters, 1, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.TEXT);
        assertTrue(responseDetails instanceof FeedbackTextResponseDetails);
        assertEquals("text answer", responseDetails.getAnswerString());
        requestParameters.clear();

        ______TS("MCQ Response: other disabled");
        FeedbackMcqQuestionDetails mcqQuestionDetails = new FeedbackMcqQuestionDetails();
        requestParameters.put("questiontype-2", new String[] { "MCQ" });
        requestParameters.put("responsetext-2-0", new String[] { "mcq option" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "mcq option" },
                        FeedbackQuestionType.MCQ,
                        mcqQuestionDetails, requestParameters, 2, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.MCQ);
        assertTrue(responseDetails instanceof FeedbackMcqResponseDetails);
        assertEquals("mcq option", responseDetails.getAnswerString());
        requestParameters.clear();

        ______TS("MCQ Response: other enabled, other option not selected");
        FeedbackMcqQuestionDetails mcqQuestionDetailsWithOtherOptionOtherNotSelected = new FeedbackMcqQuestionDetails();
        requestParameters.put("questiontype-3", new String[] { "MCQ" });
        requestParameters.put("responsetext-3-0", new String[] { "an answer" });
        requestParameters.put("mcqIsOtherOptionAnswer-3-0", new String[] { "0" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "an answer" },
                        FeedbackQuestionType.MCQ,
                        mcqQuestionDetailsWithOtherOptionOtherNotSelected, requestParameters, 3, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.MCQ);
        assertTrue(responseDetails instanceof FeedbackMcqResponseDetails);
        assertEquals("an answer", responseDetails.getAnswerString());
        assertFalse(((FeedbackMcqResponseDetails) responseDetails).isOtherOptionAnswer());
        requestParameters.clear();

        ______TS("MCQ Response: other enabled, other option selected");
        FeedbackMcqQuestionDetails mcqQuestionDetailsWithOtherOptionOtherSelected = new FeedbackMcqQuestionDetails();
        requestParameters.put("questiontype-4", new String[] { "MCQ" });
        requestParameters.put("responsetext-4-0", new String[] { "my answer" });
        requestParameters.put("mcqIsOtherOptionAnswer-4-0", new String[] { "1" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "my answer" },
                        FeedbackQuestionType.MCQ,
                        mcqQuestionDetailsWithOtherOptionOtherSelected, requestParameters, 4, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.MCQ);
        assertTrue(responseDetails instanceof FeedbackMcqResponseDetails);
        assertEquals("my answer", responseDetails.getAnswerString());
        assertTrue(((FeedbackMcqResponseDetails) responseDetails).isOtherOptionAnswer());
        requestParameters.clear();

        ______TS("MSQ Response: other disabled");
        FeedbackMsqQuestionDetails msqQuestionDetails = new FeedbackMsqQuestionDetails();
        requestParameters.put("questiontype-5", new String[] { "MSQ" });
        requestParameters.put("responsetext-5-0", new String[] { "msq option 1", "msq option 2", "msq option 3" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "msq option 1", "msq option 2", "msq option 3" },
                        FeedbackQuestionType.MSQ,
                        msqQuestionDetails, requestParameters, 5, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.MSQ);
        assertTrue(responseDetails instanceof FeedbackMsqResponseDetails);
        assertEquals("msq option 1, msq option 2, msq option 3", responseDetails.getAnswerString());
        requestParameters.clear();

        ______TS("MSQ Response: other disabled, other option not selected");
        msqQuestionDetails = new FeedbackMsqQuestionDetails();
        requestParameters.put("questiontype-6", new String[] { "MSQ" });
        requestParameters.put("responsetext-6-0", new String[] { "msq option 1", "msq option 2", "msq option 3" });
        requestParameters.put("msqIsOtherOptionAnswer-6-0", new String[] { "0" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "msq option 1", "msq option 2", "msq option 3" },
                        FeedbackQuestionType.MSQ,
                        msqQuestionDetails, requestParameters, 6, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.MSQ);
        assertTrue(responseDetails instanceof FeedbackMsqResponseDetails);
        assertEquals("msq option 1, msq option 2, msq option 3", responseDetails.getAnswerString());
        assertFalse(((FeedbackMsqResponseDetails) responseDetails).isOtherOptionAnswer());
        requestParameters.clear();

        ______TS("MSQ Response: other disabled, other option selected");
        msqQuestionDetails = new FeedbackMsqQuestionDetails();
        requestParameters.put("questiontype-7", new String[] { "MSQ" });
        requestParameters.put("responsetext-7-0", new String[] {
                "msq option 1", "msq option 2", "msq option 3", "other answer"
        });
        requestParameters.put("msqIsOtherOptionAnswer-7-0", new String[] { "1" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "msq option 1", "msq option 2", "msq option 3", "other answer" },
                        FeedbackQuestionType.MSQ,
                        msqQuestionDetails, requestParameters, 7, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.MSQ);
        assertTrue(responseDetails instanceof FeedbackMsqResponseDetails);
        assertEquals("msq option 1, msq option 2, msq option 3, other answer", responseDetails.getAnswerString());
        assertTrue(((FeedbackMsqResponseDetails) responseDetails).isOtherOptionAnswer());
        assertEquals("other answer", ((FeedbackMsqResponseDetails) responseDetails).getOtherFieldContent());
        requestParameters.clear();

        ______TS("NUMSCALE Response: typical case");
        FeedbackNumericalScaleQuestionDetails numericalScaleQuestionDetails = new FeedbackNumericalScaleQuestionDetails();
        numericalScaleQuestionDetails.setMaxScale(5);
        numericalScaleQuestionDetails.setMinScale(-5);
        requestParameters.put("questiontype-6", new String[] { "NUMSCALE" });
        requestParameters.put("responsetext-6-0", new String[] { "-3.5" });
        requestParameters.put("numscalemin-6-0", new String[] { "-5" });
        requestParameters.put("numscalemax-6-0", new String[] { "5" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "-3.5" },
                        FeedbackQuestionType.NUMSCALE,
                        numericalScaleQuestionDetails, requestParameters, 6, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.NUMSCALE);
        assertTrue(responseDetails instanceof FeedbackNumericalScaleResponseDetails);
        assertEquals("-3.5", responseDetails.getAnswerString());
        requestParameters.clear();

        ______TS("NUMSCALE Response: wrong format");
        requestParameters.put("questiontype-6", new String[] { "NUMSCALE" });
        requestParameters.put("responsetext-6-0", new String[] { "-0.5.3" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "-0.5.3" },
                        FeedbackQuestionType.NUMSCALE,
                        numericalScaleQuestionDetails, requestParameters, 6, 0);

        assertNull(responseDetails);
        requestParameters.clear();

        ______TS("CONSTSUM Response: typical case");
        String questionText = "question text";
        List<String> constSumOptions = new ArrayList<>();

        constSumOptions.add("Option 1");
        constSumOptions.add("Option 2");

        boolean isPointsPerOption = false;
        boolean shouldForceUevenDistribution = false;
        int points = 100;
        String distributePointsOption = "None";
        FeedbackConstantSumQuestionDetails constantSumQuestionDetails =
                new FeedbackConstantSumQuestionDetails(questionText, constSumOptions,
                                                       isPointsPerOption, points,
                                                        shouldForceUevenDistribution, distributePointsOption);

        requestParameters.put("questiontype-7", new String[] { "CONSTSUM" });
        requestParameters.put("responsetext-7-0", new String[] { "20", "80" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "20", "80" },
                        FeedbackQuestionType.CONSTSUM,
                        constantSumQuestionDetails, requestParameters, 7, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.CONSTSUM);
        assertTrue(responseDetails instanceof FeedbackConstantSumResponseDetails);
        assertEquals("20, 80", responseDetails.getAnswerString());
        requestParameters.clear();

        ______TS("CONTRIB Response: typical case");
        questionText = "question text";
        FeedbackContributionQuestionDetails contribQuestionDetails =
                new FeedbackContributionQuestionDetails(questionText);

        requestParameters.put("questiontype-8", new String[] { "CONTRIB" });
        requestParameters.put("responsetext-8-0", new String[] { "100" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "100" },
                        FeedbackQuestionType.CONTRIB,
                        contribQuestionDetails, requestParameters, 8, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.CONTRIB);
        assertTrue(responseDetails instanceof FeedbackContributionResponseDetails);
        assertEquals("100", responseDetails.getAnswerString());
        requestParameters.clear();

        ______TS("RUBRIC Response: invalid indexes in response");
        questionText = "question text";
        FeedbackRubricQuestionDetails rubricQuestionDetails =
                new FeedbackRubricQuestionDetails(questionText);

        requestParameters.put("questiontype-9", new String[] { "RUBRIC" });
        requestParameters.put("responsetext-9-0", new String[] { "0-0,1-0" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "0-0,1-0" },
                        FeedbackQuestionType.RUBRIC,
                        rubricQuestionDetails, requestParameters, 9, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.RUBRIC);
        assertTrue(responseDetails instanceof FeedbackRubricResponseDetails);
        assertEquals("[]", responseDetails.getAnswerString());
        requestParameters.clear();

        ______TS("RUBRIC Response: typical case");
        rubricQuestionDetails.setNumOfRubricChoices(rubricQuestionDetails.getNumOfRubricChoices() + 1);
        rubricQuestionDetails.getRubricChoices().add("choice1");
        rubricQuestionDetails.setNumOfRubricSubQuestions(rubricQuestionDetails.getNumOfRubricSubQuestions() + 1);
        rubricQuestionDetails.getRubricSubQuestions().add("sub-qn1");
        rubricQuestionDetails.setNumOfRubricSubQuestions(rubricQuestionDetails.getNumOfRubricSubQuestions() + 1);
        rubricQuestionDetails.getRubricSubQuestions().add("sub-qn2");

        requestParameters.put("questiontype-9", new String[] { "RUBRIC" });
        requestParameters.put("responsetext-9-0", new String[] { "0-0,1-0" });

        responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "0-0,1-0" },
                        FeedbackQuestionType.RUBRIC,
                        rubricQuestionDetails, requestParameters, 9, 0);

        assertEquals(responseDetails.questionType, FeedbackQuestionType.RUBRIC);
        assertTrue(responseDetails instanceof FeedbackRubricResponseDetails);
        assertEquals("[0, 0]", responseDetails.getAnswerString());
        requestParameters.clear();

    }
}

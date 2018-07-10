package teammates.test.cases.datatransfer;

import java.util.HashMap;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackRubricQuestionDetails}.
 */
public class FeedbackRubricQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();

        assertEquals(FeedbackQuestionType.RUBRIC, rubricDetails.getQuestionType());
        assertTrue(rubricDetails instanceof FeedbackRubricQuestionDetails);
        assertFalse(rubricDetails.hasAssignedWeights());
        assertEquals(0, rubricDetails.getNumOfRubricChoices());
        assertEquals(0, rubricDetails.getNumOfRubricSubQuestions());
    }

    @Test
    public void testGetQuestionWithExistingResponseSubmissionFormHtml_responsePresent_htmlTagsPresent() {

        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "rubricQuestion" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", new String[] { "subQn1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", new String[] { "subQn2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", new String[] { "choice" });

        requestParams.put("responsetext-1-0", new String[] { "0-0,1-0" });

        FeedbackQuestionDetails rubricQuestionDetails =
                FeedbackRubricQuestionDetails.createQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC);

        FeedbackResponseDetails rubricResponseDetails = FeedbackResponseDetails.createResponseDetails(
                        new String[] { "0-0,1-0" },
                        FeedbackQuestionType.RUBRIC,
                        rubricQuestionDetails, requestParams, 1, 0);

        String result = rubricQuestionDetails.getQuestionWithExistingResponseSubmissionFormHtml(true, 1,
                0, "test.course", 0, rubricResponseDetails,
                StudentAttributes.builder("test.course", "test", "test@gmail.com").build());

        assertTrue(result.contains("id=\"rubricResponseTable-1-0\""));
        assertTrue(result.contains("<th class=\"rubricCol-1-0\">"));
        assertTrue(result.contains("<input class=\"overlay\" type=\"radio\"  id=\"rubricChoice-1-0-1-0\""
                + " name=\"rubricChoice-1-0-1\" value=\"1-0\" checked/>"));

    }

    @Test
    public void testGetQuestionWithoutExistingResponseSubmissionFormHtml_responsePresent_htmlTagsPresent() {

        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "rubricQuestion" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", new String[] { "subQn1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", new String[] { "subQn2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", new String[] { "choice" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "off" });

        FeedbackQuestionDetails rubricQuestionDetails =
                FeedbackRubricQuestionDetails.createQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC);

        String result = rubricQuestionDetails.getQuestionWithoutExistingResponseSubmissionFormHtml(false, 1,
                0, "test.course", 0,
                StudentAttributes.builder("test.course", "test", "test@gmail.com").build());

        assertTrue(result.contains("id=\"rubricResponseTable-1-0\""));
        assertFalse(result.contains("checked"));
    }

    @Test
    public void testGetQuestionAdditionalInfo_additionalInfoPresent_htmlTagsPresent() {
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "rubricQuestion" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", new String[] { "subQn1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", new String[] { "subQn2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", new String[] { "choice" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "off" });

        FeedbackQuestionDetails feedbackQuestionDetails =
                FeedbackQuestionDetails.createQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC);

        String result = feedbackQuestionDetails.getQuestionAdditionalInfoHtml(1, "");
        assertTrue(result.contains("[more]"));
        assertTrue(result.contains("Rubric question sub-questions:\n"
                + "<p>a) subQn1<br>b) subQn2<br></p>"));
    }
}

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
 * SUT: {@link FeedbackRubricQuestionDetails}
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

        requestParams.put("questiontype-1", new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[]{ "rubricQuestion" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "off" });
        requestParams.put("responsetext-1-0", new String[] { "0-0,1-1" });


        FeedbackQuestionDetails rubricQuestionDetails =
                FeedbackRubricQuestionDetails.createQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC);

        FeedbackResponseDetails rubricResponseDetails = FeedbackResponseDetails.createResponseDetails(
                        new String[] { "0-0,1-1" },
                        FeedbackQuestionType.RUBRIC,
                        rubricQuestionDetails, requestParams, 1, 0);

        String result = rubricQuestionDetails.getQuestionWithExistingResponseSubmissionFormHtml(true, 1,
                0, "test.course", 0, rubricResponseDetails,
                StudentAttributes.builder("test.course", "test", "test@gmail.com").build());

        assertTrue(result.contains("id=\"rubricResponseTable-1-0\""));
        assertTrue(result.contains("id=\"rubricResponse-1-0\""));
    }
}

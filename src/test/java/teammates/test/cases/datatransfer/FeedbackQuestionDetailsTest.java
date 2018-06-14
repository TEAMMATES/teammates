package teammates.test.cases.datatransfer;

import java.util.HashMap;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackQuestionDetails}
 *      {@link FeedbackMcqQuestionDetails}.
 */
public class FeedbackQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testCreateQuestionDetailsTest() {

        ______TS("MCQ Question");

        HashMap<String, String[]> requestParameters = new HashMap<>();
        requestParameters.put("questiontext", new String[] { "MCQ" });
        requestParameters.put("mcqGeneratedOptions", new String[] {FeedbackParticipantType.NONE.toString()});
        requestParameters.put("noofchoicecreated", new String[] { "1" });
        requestParameters.put("mcqOption-0", new String[] { "mcq option" });

        FeedbackQuestionDetails feedbackQuestionDetails =
                FeedbackQuestionDetails.createQuestionDetails(requestParameters, FeedbackQuestionType.MCQ);
        assertEquals(feedbackQuestionDetails.getQuestionType(), FeedbackQuestionType.MCQ);
        assertTrue(feedbackQuestionDetails instanceof FeedbackMcqQuestionDetails);
        assertEquals(feedbackQuestionDetails.getQuestionText(), "MCQ");

        requestParameters.put("questiontype-1", new String[] { "MCQ" });
        requestParameters.put("responsetext-1-0", new String[] { "mcq option" });

        FeedbackResponseDetails responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "mcq option" },
                        FeedbackQuestionType.MCQ,
                        feedbackQuestionDetails, requestParameters, 1, 0);

        String result = feedbackQuestionDetails.getQuestionWithExistingResponseSubmissionFormHtml(true, 1,
                0, "test.course", 1, responseDetails,
                StudentAttributes.builder("test.course", "test", "test@gmail.com").build());
        assertTrue(result.contains("checked")); // Checking if response is selected
        assertTrue(result.contains("mcq option")); // Check if option is showed in HTML

        result = feedbackQuestionDetails.getQuestionWithoutExistingResponseSubmissionFormHtml(true, 1,
                0, "test.course", 0,
                StudentAttributes.builder("test.course", "test", "test@gmail.com").build());
        assertFalse(result.contains("checked")); // Checking if response is not present now

        result = feedbackQuestionDetails.getQuestionAdditionalInfoHtml(1, "");
        assertTrue(result.contains("[more]"));
        assertTrue(result.contains("<li>mcq option</li>"));
        requestParameters.clear();

    }
}

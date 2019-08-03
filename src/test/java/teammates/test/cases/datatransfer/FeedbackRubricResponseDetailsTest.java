package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackRubricResponseDetails;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackRubricResponseDetails}.
 */
public class FeedbackRubricResponseDetailsTest extends BaseTestCase {

    private FeedbackQuestionAttributes sampleQuestion;

    @BeforeMethod
    public void beforeMethod() {
        FeedbackRubricQuestionDetails rubricQuestionDetails = new FeedbackRubricQuestionDetails();
        rubricQuestionDetails.setHasAssignedWeights(false);
        rubricQuestionDetails.setRubricWeightsForEachCell(new ArrayList<>());
        rubricQuestionDetails.setNumOfRubricChoices(2);
        rubricQuestionDetails.setNumOfRubricSubQuestions(2);
        rubricQuestionDetails.setRubricChoices(Arrays.asList("a", "b"));
        rubricQuestionDetails.setRubricSubQuestions(Arrays.asList("q1", "q2"));
        rubricQuestionDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("d1", "d2"), Arrays.asList("d3", "d4")));

        sampleQuestion = FeedbackQuestionAttributes.builder()
                .withCourseId("testCourse")
                .withFeedbackSessionName("testSession")
                .withQuestionDescription("testDescription")
                .withQuestionDetails(rubricQuestionDetails)
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withNumberOfEntitiesToGiveFeedbackTo(5)
                .withShowGiverNameTo(new ArrayList<>())
                .withShowResponsesTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .build();
    }

    @Test
    public void testValidateResponseDetails_validAnswer_shouldReturnEmptyErrorList() {
        FeedbackRubricResponseDetails responseDetails = new FeedbackRubricResponseDetails();

        responseDetails.setAnswer(Arrays.asList(1, Const.FeedbackQuestion.RUBRIC_ANSWER_NOT_CHOSEN));
        assertTrue(responseDetails.validateResponseDetails(sampleQuestion).isEmpty());

        responseDetails.setAnswer(Arrays.asList(Const.FeedbackQuestion.RUBRIC_ANSWER_NOT_CHOSEN, 0));
        assertTrue(responseDetails.validateResponseDetails(sampleQuestion).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, 0));
        assertTrue(responseDetails.validateResponseDetails(sampleQuestion).isEmpty());
    }

    @Test
    public void testValidateResponseDetails_invalidAnswer_shouldReturnNonEmptyErrorList() {
        FeedbackRubricResponseDetails responseDetails = new FeedbackRubricResponseDetails();

        responseDetails.setAnswer(Arrays.asList());
        assertFalse(responseDetails.validateResponseDetails(sampleQuestion).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0));
        assertFalse(responseDetails.validateResponseDetails(sampleQuestion).isEmpty());

        responseDetails.setAnswer(Arrays.asList(
                Const.FeedbackQuestion.RUBRIC_ANSWER_NOT_CHOSEN, Const.FeedbackQuestion.RUBRIC_ANSWER_NOT_CHOSEN));
        assertFalse(responseDetails.validateResponseDetails(sampleQuestion).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, -2));
        assertFalse(responseDetails.validateResponseDetails(sampleQuestion).isEmpty());

        responseDetails.setAnswer(Arrays.asList(2, 1));
        assertFalse(responseDetails.validateResponseDetails(sampleQuestion).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, 1, 0));
        assertFalse(responseDetails.validateResponseDetails(sampleQuestion).isEmpty());
    }
}

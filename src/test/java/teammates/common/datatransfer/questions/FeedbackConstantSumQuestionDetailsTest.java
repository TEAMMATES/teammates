package teammates.common.datatransfer.questions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.FeedbackConstantSumQuestionDetailsHelper;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackConstantSumOptionsQuestionDetails},
 *      {@link FeedbackConstantSumRecipientsQuestionDetails}.
 */
public class FeedbackConstantSumQuestionDetailsTest extends BaseTestCase {

    @Test
    public void constructor_optionsQuestion_shouldUseOptionsType() {
        FeedbackConstantSumOptionsQuestionDetails details = new FeedbackConstantSumOptionsQuestionDetails();

        assertEquals(FeedbackQuestionType.CONSTSUM_OPTIONS, details.getQuestionType());
    }

    @Test
    public void constructor_recipientsQuestion_shouldUseRecipientsType() {
        FeedbackConstantSumRecipientsQuestionDetails details = new FeedbackConstantSumRecipientsQuestionDetails();

        assertEquals(FeedbackQuestionType.CONSTSUM_RECIPIENTS, details.getQuestionType());
    }

    @Test
    public void validateResponsesDetails_optionsQuestionInvalidResponses_shouldReturnError() {
        FeedbackConstantSumOptionsQuestionDetails questionDetails = new FeedbackConstantSumOptionsQuestionDetails();
        questionDetails.setConstSumOptions(Arrays.asList("UI", "Backend"));
        questionDetails.setPoints(100);

        FeedbackConstantSumOptionsResponseDetails responseDetails = new FeedbackConstantSumOptionsResponseDetails();
        responseDetails.setAnswers(Arrays.asList(60));

        List<String> errors = questionDetails.validateResponsesDetails(List.of(responseDetails), 1);

        assertEquals(List.of(FeedbackConstantSumQuestionDetailsHelper.CONST_SUM_ANSWER_OPTIONS_NOT_MATCH), errors);
    }

    @Test
    public void validateResponsesDetails_recipientsQuestionInvalidResponses_shouldReturnError() {
        FeedbackConstantSumRecipientsQuestionDetails questionDetails = new FeedbackConstantSumRecipientsQuestionDetails();
        questionDetails.setPoints(100);

        FeedbackConstantSumRecipientsResponseDetails responseDetails = new FeedbackConstantSumRecipientsResponseDetails();
        responseDetails.setAnswers(Arrays.asList(60, 40));

        List<String> errors = questionDetails.validateResponsesDetails(List.of(responseDetails), 2);

        assertEquals(List.of(FeedbackConstantSumQuestionDetailsHelper.CONST_SUM_ANSWER_RECIPIENT_NOT_MATCH), errors);
    }

    @Test
    public void shouldChangesRequireResponseDeletion_optionsChange_shouldReturnTrue() {
        FeedbackConstantSumOptionsQuestionDetails original = new FeedbackConstantSumOptionsQuestionDetails();
        original.setConstSumOptions(Arrays.asList("A", "B"));
        FeedbackConstantSumOptionsQuestionDetails updated = new FeedbackConstantSumOptionsQuestionDetails();
        updated.setConstSumOptions(Arrays.asList("A", "C"));

        assertTrue(original.shouldChangesRequireResponseDeletion(updated));
    }

    @Test
    public void shouldChangesRequireResponseDeletion_recipientsSameSettings_shouldReturnFalse() {
        FeedbackConstantSumRecipientsQuestionDetails original = new FeedbackConstantSumRecipientsQuestionDetails();
        FeedbackConstantSumRecipientsQuestionDetails updated = new FeedbackConstantSumRecipientsQuestionDetails();

        assertFalse(original.shouldChangesRequireResponseDeletion(updated));
    }
}

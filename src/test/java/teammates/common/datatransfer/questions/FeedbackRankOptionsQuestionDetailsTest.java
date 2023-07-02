package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackRankOptionsQuestionDetails}.
 */
public class FeedbackRankOptionsQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackRankOptionsQuestionDetails rankDetails = new FeedbackRankOptionsQuestionDetails();

        assertEquals(FeedbackQuestionType.RANK_OPTIONS, rankDetails.getQuestionType());
        assertEquals(rankDetails.getMinOptionsToBeRanked(), Const.POINTS_NO_VALUE);
        assertEquals(rankDetails.getMaxOptionsToBeRanked(), Const.POINTS_NO_VALUE);
        assertFalse(rankDetails.isAreDuplicatesAllowed());
    }

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        assertTrue(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testValidateQuestionDetails_emptyOption_errorReturned() {
        FeedbackRankOptionsQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        List<String> errorResponse = new ArrayList<>();

        feedbackQuestionDetails.setOptions(Arrays.asList("", "  "));
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_EMPTY_OPTIONS_ENTERED);
        assertEquals(errorResponse, feedbackQuestionDetails.validateQuestionDetails());
    }

    @Test
    public void testValidateQuestionDetails_invalidMaxMinOptions_errorReturned() {
        FeedbackRankOptionsQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        List<String> errorResponse = new ArrayList<>();

        feedbackQuestionDetails.setMaxOptionsToBeRanked(0);
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_INVALID_MAX_OPTIONS_ENABLED);
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_NOT_ENOUGH_OPTIONS
                + FeedbackRankOptionsQuestionDetails.MIN_NUM_OF_OPTIONS + ".");
        assertEquals(errorResponse, feedbackQuestionDetails.validateQuestionDetails());
        errorResponse.clear();

        feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackQuestionDetails.setMinOptionsToBeRanked(0);
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_INVALID_MIN_OPTIONS_ENABLED);
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_NOT_ENOUGH_OPTIONS
                + FeedbackRankOptionsQuestionDetails.MIN_NUM_OF_OPTIONS + ".");
        assertEquals(errorResponse, feedbackQuestionDetails.validateQuestionDetails());
        errorResponse.clear();

        feedbackQuestionDetails.setMinOptionsToBeRanked(1);
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_MIN_OPTIONS_ENABLED_MORE_THAN_CHOICES);
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_NOT_ENOUGH_OPTIONS
                + FeedbackRankOptionsQuestionDetails.MIN_NUM_OF_OPTIONS + ".");
        assertEquals(errorResponse, feedbackQuestionDetails.validateQuestionDetails());
        errorResponse.clear();

        feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackQuestionDetails.setMaxOptionsToBeRanked(4);
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_MAX_OPTIONS_ENABLED_MORE_THAN_CHOICES);
        feedbackQuestionDetails.setOptions(Arrays.asList("1", "2", "3"));
        assertEquals(errorResponse, feedbackQuestionDetails.validateQuestionDetails());
        errorResponse.clear();

        feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackQuestionDetails.setMinOptionsToBeRanked(5);
        feedbackQuestionDetails.setMaxOptionsToBeRanked(3);
        feedbackQuestionDetails.setOptions(Arrays.asList("1", "2", "3", "4", "5"));
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_INVALID_MIN_OPTIONS_ENABLED);
        assertEquals(errorResponse, feedbackQuestionDetails.validateQuestionDetails());
        errorResponse.clear();

        feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackQuestionDetails.setMaxOptionsToBeRanked(0);
        feedbackQuestionDetails.setMinOptionsToBeRanked(0);
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_INVALID_MAX_OPTIONS_ENABLED);
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_INVALID_MIN_OPTIONS_ENABLED);
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_NOT_ENOUGH_OPTIONS
                + FeedbackRankOptionsQuestionDetails.MIN_NUM_OF_OPTIONS + ".");
        assertEquals(errorResponse, feedbackQuestionDetails.validateQuestionDetails());
    }

    @Test
    public void testValidateQuestionDetails_optionSizeInvalid_errorReturned() {
        FeedbackRankOptionsQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        List<String> errorResponse = new ArrayList<>();

        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_NOT_ENOUGH_OPTIONS
                + FeedbackRankOptionsQuestionDetails.MIN_NUM_OF_OPTIONS + ".");
        assertEquals(errorResponse, feedbackQuestionDetails.validateQuestionDetails());
        feedbackQuestionDetails.setOptions(Arrays.asList("1"));
        assertEquals(errorResponse, feedbackQuestionDetails.validateQuestionDetails());
        errorResponse.clear();
    }

    @Test
    public void testValidateQuestionDetails_optionSizeValid_noError() {
        FeedbackRankOptionsQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();

        feedbackQuestionDetails.setOptions(Arrays.asList("1", "2"));
        assertTrue(feedbackQuestionDetails.validateQuestionDetails().isEmpty());
    }

    @Test
    public void testValidateResponseDetails_duplicateRankOptions_errorReturned() {
        FeedbackRankOptionsQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackQuestionDetails.setOptions(Arrays.asList("1", "2", "3"));
        List<String> errorResponse = new ArrayList<>();

        FeedbackRankOptionsResponseDetails feedbackResponseDetails = new FeedbackRankOptionsResponseDetails();
        feedbackResponseDetails.setAnswers(Arrays.asList(1, 1));
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_DUPLICATE_RANK_RESPONSE);
        assertEquals(errorResponse,
                feedbackQuestionDetails.validateResponsesDetails(Arrays.asList(feedbackResponseDetails), 1));
    }

    @Test
    public void testValidateResponseDetails_optionExceedBound_errorReturned() {
        FeedbackRankOptionsQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackQuestionDetails.setMinOptionsToBeRanked(3);
        feedbackQuestionDetails.setMaxOptionsToBeRanked(5);
        feedbackQuestionDetails.setOptions(Arrays.asList("1", "2", "3", "4", "5", "6"));
        List<String> errorResponse = new ArrayList<>();

        FeedbackRankOptionsResponseDetails feedbackResponseDetails = new FeedbackRankOptionsResponseDetails();
        feedbackResponseDetails.setAnswers(Arrays.asList(1, 2));
        errorResponse.add("You must rank at least " + feedbackQuestionDetails.minOptionsToBeRanked + " options.");
        assertEquals(errorResponse,
                feedbackQuestionDetails.validateResponsesDetails(Arrays.asList(feedbackResponseDetails), 1));
        errorResponse.clear();

        feedbackResponseDetails.setAnswers(Arrays.asList(1, 2, 3, 4, 5, 6));
        errorResponse.add("You can rank at most " + feedbackQuestionDetails.maxOptionsToBeRanked + " options.");
        assertEquals(errorResponse,
                feedbackQuestionDetails.validateResponsesDetails(Arrays.asList(feedbackResponseDetails), 1));
    }

    @Test
    public void testValidateResponseDetails_invalidRankOption_errorReturned() {
        FeedbackRankOptionsQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackQuestionDetails.setOptions(Arrays.asList("1", "2", "3"));
        List<String> errorResponse = new ArrayList<>();

        FeedbackRankOptionsResponseDetails feedbackResponseDetails = new FeedbackRankOptionsResponseDetails();
        feedbackResponseDetails.setAnswers(Arrays.asList(0));
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_INVALID_RANK_RESPONSE);
        assertEquals(errorResponse,
                feedbackQuestionDetails.validateResponsesDetails(Arrays.asList(feedbackResponseDetails), 1));

        feedbackResponseDetails.setAnswers(Arrays.asList(5));
        assertEquals(errorResponse,
                feedbackQuestionDetails.validateResponsesDetails(Arrays.asList(feedbackResponseDetails), 1));
    }

    @Test
    public void testValidateResponseDetails_validRankOption_noError() {
        FeedbackRankOptionsQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackQuestionDetails.setOptions(Arrays.asList("1", "2", "3", "4", "5"));

        FeedbackRankOptionsResponseDetails feedbackResponseDetails1 = new FeedbackRankOptionsResponseDetails();
        feedbackResponseDetails1.setAnswers(Arrays.asList(2, 3));
        FeedbackRankOptionsResponseDetails feedbackResponseDetails2 = new FeedbackRankOptionsResponseDetails();
        feedbackResponseDetails2.setAnswers(Arrays.asList(3, 5));
        assertTrue(feedbackQuestionDetails.validateResponsesDetails(
                Arrays.asList(
                        feedbackResponseDetails1,
                        feedbackResponseDetails2),
                1
                ).isEmpty());
    }

    @Test
    public void testValidateResponseDetails_duplicateOptionInDefault_errorReturned() {
        FeedbackRankOptionsQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackQuestionDetails.setOptions(Arrays.asList("1", "2", "3", "4", "5"));
        List<String> errorResponse = new ArrayList<>();

        FeedbackRankOptionsResponseDetails feedbackResponseDetails1 = new FeedbackRankOptionsResponseDetails();
        feedbackResponseDetails1.setAnswers(Arrays.asList(2, 2));
        FeedbackRankOptionsResponseDetails feedbackResponseDetails2 = new FeedbackRankOptionsResponseDetails();
        feedbackResponseDetails2.setAnswers(Arrays.asList(2, 3));
        errorResponse.add(FeedbackRankOptionsQuestionDetails.ERROR_DUPLICATE_RANK_RESPONSE);
        assertEquals(errorResponse,
                feedbackQuestionDetails.validateResponsesDetails(
                Arrays.asList(
                        feedbackResponseDetails1,
                        feedbackResponseDetails2),
                        1
                ));
    }

    @Test
    public void testValidateResponseDetails_duplicateOption_noError() {
        FeedbackRankOptionsQuestionDetails feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackQuestionDetails.setAreDuplicatesAllowed(true);
        feedbackQuestionDetails.setOptions(Arrays.asList("1", "2", "3", "4", "5"));

        FeedbackRankOptionsResponseDetails feedbackResponseDetails1 = new FeedbackRankOptionsResponseDetails();
        feedbackResponseDetails1.setAnswers(Arrays.asList(2, 2));
        FeedbackRankOptionsResponseDetails feedbackResponseDetails2 = new FeedbackRankOptionsResponseDetails();
        feedbackResponseDetails2.setAnswers(Arrays.asList(2, 3));
        assertTrue(feedbackQuestionDetails.validateResponsesDetails(
                Arrays.asList(
                        feedbackResponseDetails1,
                        feedbackResponseDetails2),
                1
                ).isEmpty());
    }
}

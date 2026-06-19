package teammates.common.datatransfer.questions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.visibility.FeedbackVisibilityType;
import teammates.common.util.Const;
import teammates.storage.entity.questions.FeedbackContributionQuestion;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackContributionQuestionDetails}.
 */
public class FeedbackContributionQuestionDetailsTest extends BaseTestCase {
    static final List<Integer> VALID_ZERO_SUM_CONTRIBUTION_RESPONSE_ANSWERS =
            new ArrayList<>(Arrays.asList(100, 55, 150, 95, 170, 30, 100));
    static final List<Integer> VALID_NON_ZERO_SUM_CONTRIBUTION_RESPONSE_ANSWERS =
            new ArrayList<>(Arrays.asList(0, 10, 15, 50, 100, 150, 190, 195, 200));
    static final List<Integer> INVALID_CONTRIBUTION_RESPONSE_ANSWERS =
            new ArrayList<>(Arrays.asList(-983, -1, 1, 4, 19, 51, 101, 199, 201, 1000));

    @Test
    public void testIsIndividualResponsesShownToStudents_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackContributionQuestionDetails();
        assertFalse(feedbackQuestionDetails.isIndividualResponsesShownToStudents());
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameIsZeroSum_shouldReturnFalse() {
        FeedbackContributionQuestionDetails feedbackContributionQuestionDetails =
                new FeedbackContributionQuestionDetails();
        feedbackContributionQuestionDetails.setZeroSum(true);
        FeedbackContributionQuestionDetails newDetails = new FeedbackContributionQuestionDetails();
        newDetails.setZeroSum(true);
        assertFalse(feedbackContributionQuestionDetails.shouldChangesRequireResponseDeletion(newDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentZeroSum_shouldReturnTrue() {
        FeedbackContributionQuestionDetails feedbackContributionQuestionDetails =
                new FeedbackContributionQuestionDetails();
        feedbackContributionQuestionDetails.setZeroSum(true);
        FeedbackContributionQuestionDetails newDetails = new FeedbackContributionQuestionDetails();
        newDetails.setZeroSum(false);
        assertTrue(feedbackContributionQuestionDetails.shouldChangesRequireResponseDeletion(newDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameIsNotSureAllowed_shouldReturnFalse() {
        FeedbackContributionQuestionDetails feedbackContributionQuestionDetails =
                new FeedbackContributionQuestionDetails();
        feedbackContributionQuestionDetails.setNotSureAllowed(false);
        FeedbackContributionQuestionDetails newDetails = new FeedbackContributionQuestionDetails();
        newDetails.setNotSureAllowed(false);
        assertFalse(feedbackContributionQuestionDetails.shouldChangesRequireResponseDeletion(newDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentIsNotSureAllowed_shouldReturnTrue() {
        FeedbackContributionQuestionDetails feedbackContributionQuestionDetails =
                new FeedbackContributionQuestionDetails();
        feedbackContributionQuestionDetails.setNotSureAllowed(false);
        FeedbackContributionQuestionDetails newDetails = new FeedbackContributionQuestionDetails();
        newDetails.setNotSureAllowed(true);
        assertTrue(feedbackContributionQuestionDetails.shouldChangesRequireResponseDeletion(newDetails));
    }

    @Test
    public void testValidateQuestionDetails() {
        FeedbackContributionQuestionDetails feedbackContributionQuestionDetails = new FeedbackContributionQuestionDetails();

        ______TS("failure: isZeroSum true and isNotSureAllowed true");
        feedbackContributionQuestionDetails.setZeroSum(true);
        feedbackContributionQuestionDetails.setNotSureAllowed(true);
        assertEquals(List.of(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_OPTION),
                feedbackContributionQuestionDetails.validateQuestionDetails());

        ______TS("success: isZeroSum true and isNotSureAllowed false");
        feedbackContributionQuestionDetails.setZeroSum(true);
        feedbackContributionQuestionDetails.setNotSureAllowed(false);
        assertTrue(feedbackContributionQuestionDetails.validateQuestionDetails().isEmpty());

        ______TS("success: isZeroSum false and isNotSureAllowed true");
        feedbackContributionQuestionDetails.setZeroSum(false);
        feedbackContributionQuestionDetails.setNotSureAllowed(true);
        assertTrue(feedbackContributionQuestionDetails.validateQuestionDetails().isEmpty());

        ______TS("success: isZeroSum false and isNotSureAllowed false");
        feedbackContributionQuestionDetails.setZeroSum(false);
        feedbackContributionQuestionDetails.setNotSureAllowed(false);
        assertTrue(feedbackContributionQuestionDetails.validateQuestionDetails().isEmpty());
    }

    @Test
    public void testValidateResponsesDetails() {
        FeedbackContributionQuestionDetails feedbackContributionQuestionDetails = new FeedbackContributionQuestionDetails();
        List<FeedbackResponseDetails> responses = new ArrayList<>();
        List<String> expectedResponsesValidationResults = new ArrayList<>();

        ______TS("success: all answers of all responses are in range and are multiple of 5");
        for (int answer : VALID_ZERO_SUM_CONTRIBUTION_RESPONSE_ANSWERS) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            details.setAnswer(answer);
            responses.add(details);
        }
        assertTrue(feedbackContributionQuestionDetails.validateResponsesDetails(responses,
                VALID_ZERO_SUM_CONTRIBUTION_RESPONSE_ANSWERS.size()).isEmpty());

        ______TS("success: all answers of all responses are POINTS_NOT_SURE and notSure is allowed");
        responses.clear();
        feedbackContributionQuestionDetails.setZeroSum(false);
        feedbackContributionQuestionDetails.setNotSureAllowed(true);
        for (int i = 0; i < 10; i++) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            details.setAnswer(Const.POINTS_NOT_SURE);
            responses.add(details);
        }
        assertTrue(feedbackContributionQuestionDetails.validateResponsesDetails(responses, 10).isEmpty());

        ______TS("success: all answers of all responses are POINTS_NOT_SUBMITTED and zeroSum is false");
        responses.clear();
        feedbackContributionQuestionDetails.setZeroSum(false);
        for (int i = 0; i < 10; i++) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            responses.add(details);
        }
        assertTrue(feedbackContributionQuestionDetails.validateResponsesDetails(responses, 10).isEmpty());

        ______TS("success: mix of answers of all responses that are 1) in range and is multiple of 5 "
                + "2) POINTS_NOT_SURE and notSure is allowed 3) POINTS_NOT_SUBMITTED");
        responses.clear();
        feedbackContributionQuestionDetails.setZeroSum(false);
        feedbackContributionQuestionDetails.setNotSureAllowed(true);
        for (int answer : VALID_NON_ZERO_SUM_CONTRIBUTION_RESPONSE_ANSWERS) {
            FeedbackContributionResponseDetails details1 = new FeedbackContributionResponseDetails();
            details1.setAnswer(answer);
            responses.add(details1);
            FeedbackContributionResponseDetails details2 = new FeedbackContributionResponseDetails();
            details2.setAnswer(Const.POINTS_NOT_SURE);
            responses.add(details2);
            FeedbackContributionResponseDetails details3 = new FeedbackContributionResponseDetails();
            responses.add(details3);
        }
        assertTrue(feedbackContributionQuestionDetails.validateResponsesDetails(responses,
                VALID_NON_ZERO_SUM_CONTRIBUTION_RESPONSE_ANSWERS.size()).isEmpty());

        ______TS("success: all answers of all responses are POINTS_NOT_SUBMITTED regardless value of zeroSum");
        responses.clear();
        feedbackContributionQuestionDetails.setZeroSum(true);
        feedbackContributionQuestionDetails.setNotSureAllowed(false);
        for (int i = 0; i < 10; i++) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            responses.add(details);
        }
        assertEquals(expectedResponsesValidationResults,
                feedbackContributionQuestionDetails.validateResponsesDetails(responses, 10));

        responses.clear();
        feedbackContributionQuestionDetails.setZeroSum(false);
        for (int i = 0; i < 10; i++) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            responses.add(details);
        }
        assertEquals(expectedResponsesValidationResults,
                feedbackContributionQuestionDetails.validateResponsesDetails(responses, 10));

        ______TS("failure: all answers of all responses are either not in range or are not multiple of 5");
        responses.clear();
        feedbackContributionQuestionDetails.setZeroSum(false);
        for (int answer : INVALID_CONTRIBUTION_RESPONSE_ANSWERS) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            details.setAnswer(answer);
            responses.add(details);
            expectedResponsesValidationResults.add(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_OPTION);
        }
        assertEquals(expectedResponsesValidationResults,
                feedbackContributionQuestionDetails.validateResponsesDetails(responses,
                        INVALID_CONTRIBUTION_RESPONSE_ANSWERS.size()));

        ______TS("failure: all answers of all responses are POINTS_NOT_SURE and notSure is not allowed");
        responses.clear();
        feedbackContributionQuestionDetails.setZeroSum(false);
        feedbackContributionQuestionDetails.setNotSureAllowed(false);
        for (int i = 0; i < 10; i++) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            details.setAnswer(Const.POINTS_NOT_SURE);
            responses.add(details);
        }
        assertEquals(expectedResponsesValidationResults,
                feedbackContributionQuestionDetails.validateResponsesDetails(responses, 10));

        ______TS("failure: some answers of all responses are POINTS_NOT_SUBMITTED and zeroSum is true");
        responses.clear();
        expectedResponsesValidationResults.clear();
        feedbackContributionQuestionDetails.setZeroSum(true);
        feedbackContributionQuestionDetails.setNotSureAllowed(false);
        for (int i = 0; i < 5; i++) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            details.setAnswer(VALID_ZERO_SUM_CONTRIBUTION_RESPONSE_ANSWERS.get(i));
            responses.add(details);
        }
        for (int i = 0; i < 5; i++) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            responses.add(details);
            expectedResponsesValidationResults.add(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_OPTION);
        }
        // actualTotal not zero-sum
        expectedResponsesValidationResults.add(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_OPTION);
        assertEquals(expectedResponsesValidationResults,
                feedbackContributionQuestionDetails.validateResponsesDetails(responses, 10));

        ______TS("failure: mix of answers of all responses that are 1) either not in range or not multiple of"
                + "5, 2) POINTS_NOT_SURE and notSure is not allowed");
        responses.clear();
        expectedResponsesValidationResults.clear();
        feedbackContributionQuestionDetails.setZeroSum(false);
        feedbackContributionQuestionDetails.setNotSureAllowed(false);
        for (int answer : INVALID_CONTRIBUTION_RESPONSE_ANSWERS) {
            FeedbackContributionResponseDetails details1 = new FeedbackContributionResponseDetails();
            details1.setAnswer(answer);
            responses.add(details1);
            FeedbackContributionResponseDetails details2 = new FeedbackContributionResponseDetails();
            details2.setAnswer(Const.POINTS_NOT_SURE);
            responses.add(details2);
        }
        responses.forEach(s ->
                expectedResponsesValidationResults.add(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_OPTION));
        assertEquals(expectedResponsesValidationResults,
                feedbackContributionQuestionDetails.validateResponsesDetails(responses, 10));

        ______TS("failure: mix of valid and invalid responses");
        responses.clear();
        expectedResponsesValidationResults.clear();
        feedbackContributionQuestionDetails.setZeroSum(true);
        feedbackContributionQuestionDetails.setNotSureAllowed(false);
        for (int answer : VALID_ZERO_SUM_CONTRIBUTION_RESPONSE_ANSWERS) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            details.setAnswer(answer);
            responses.add(details);
        }
        for (int answer : VALID_NON_ZERO_SUM_CONTRIBUTION_RESPONSE_ANSWERS) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            details.setAnswer(answer);
            responses.add(details);
        }
        // actualTotal not zero-sum
        expectedResponsesValidationResults.add(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_OPTION);
        for (int answer : INVALID_CONTRIBUTION_RESPONSE_ANSWERS) {
            FeedbackContributionResponseDetails details = new FeedbackContributionResponseDetails();
            details.setAnswer(answer);
            responses.add(details);
            expectedResponsesValidationResults.add(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_OPTION);
        }
        assertEquals(expectedResponsesValidationResults,
                feedbackContributionQuestionDetails.validateResponsesDetails(responses,
                        VALID_ZERO_SUM_CONTRIBUTION_RESPONSE_ANSWERS.size()
                                + VALID_NON_ZERO_SUM_CONTRIBUTION_RESPONSE_ANSWERS.size()
                                + INVALID_CONTRIBUTION_RESPONSE_ANSWERS.size()));

    }

    @Test
    public void testValidateGiverRecipientVisibility() {
        FeedbackContributionQuestionDetails details = new FeedbackContributionQuestionDetails();
        FeedbackContributionQuestion feedbackQuestion = new FeedbackContributionQuestion(
                1, "description",
                QuestionGiverType.STUDENTS,
                QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
                Const.MAX_POSSIBLE_RECIPIENTS,
                Arrays.asList(FeedbackVisibilityType.RECIPIENT,
                        FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS,
                        FeedbackVisibilityType.GIVER_TEAM_MEMBERS,
                        FeedbackVisibilityType.INSTRUCTORS),
                new ArrayList<>(),
                new ArrayList<>(),
                new FeedbackContributionQuestionDetails());

        ______TS("success: valid giver recipient visibility");
        assertEquals("", details.validateGiverRecipientVisibility(feedbackQuestion));

        ______TS("failure: giver type is not STUDENT");
        feedbackQuestion.setGiverType(QuestionGiverType.SESSION_CREATOR);
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_FEEDBACK_PATH,
                details.validateGiverRecipientVisibility(feedbackQuestion));
        assertEquals(QuestionGiverType.STUDENTS, feedbackQuestion.getGiverType());

        ______TS("failure: recipient type can only be OWN_TEAM_MEMBERS_INCLUDING_SELF");
        feedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);
        feedbackQuestion.setRecipientType(QuestionRecipientType.SELF);
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_FEEDBACK_PATH,
                details.validateGiverRecipientVisibility(feedbackQuestion));
        assertEquals(QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF, feedbackQuestion.getRecipientType());

        ______TS("failure: giver type is not STUDENT and recipient type is not OWN_TEAM_MEMBERS_INCLUDING_SELF");
        feedbackQuestion.setGiverType(QuestionGiverType.SESSION_CREATOR);
        feedbackQuestion.setRecipientType(QuestionRecipientType.SELF);
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_FEEDBACK_PATH,
                details.validateGiverRecipientVisibility(feedbackQuestion));
        assertEquals(QuestionGiverType.STUDENTS, feedbackQuestion.getGiverType());
        assertEquals(QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF, feedbackQuestion.getRecipientType());

        ______TS("failure: invalid restrictions on visibility options");
        feedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);
        feedbackQuestion.setRecipientType(QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
        feedbackQuestion.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS,
                details.validateGiverRecipientVisibility(feedbackQuestion));
        assertEquals(Arrays.asList(FeedbackVisibilityType.RECIPIENT,
                FeedbackVisibilityType.GIVER_TEAM_MEMBERS, FeedbackVisibilityType.INSTRUCTORS),
                feedbackQuestion.getShowResponsesTo());

        ______TS("failure: giver type is not STUDENT and invalid restrictions on visibility options");
        feedbackQuestion.setGiverType(QuestionGiverType.SESSION_CREATOR);
        feedbackQuestion.setRecipientType(QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
        feedbackQuestion.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS,
                details.validateGiverRecipientVisibility(feedbackQuestion));
        assertEquals(QuestionGiverType.STUDENTS, feedbackQuestion.getGiverType());
        assertEquals(Arrays.asList(FeedbackVisibilityType.RECIPIENT,
                FeedbackVisibilityType.GIVER_TEAM_MEMBERS, FeedbackVisibilityType.INSTRUCTORS),
                feedbackQuestion.getShowResponsesTo());

        ______TS("failure: recipient type is not OWN_TEAM_MEMBERS_INCLUDING_SELF and invalid restrictions on "
                + "visibility options");
        feedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);
        feedbackQuestion.setRecipientType(QuestionRecipientType.SELF);
        feedbackQuestion.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS,
                details.validateGiverRecipientVisibility(feedbackQuestion));
        assertEquals(QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF, feedbackQuestion.getRecipientType());
        assertEquals(Arrays.asList(FeedbackVisibilityType.RECIPIENT,
                FeedbackVisibilityType.GIVER_TEAM_MEMBERS, FeedbackVisibilityType.INSTRUCTORS),
                feedbackQuestion.getShowResponsesTo());

        ______TS("failure: giver type is not STUDENT and recipient type is not OWN_TEAM_MEMBERS_INCLUDING_SELF"
                + " and invalid restrictions on visibility options");
        feedbackQuestion.setGiverType(QuestionGiverType.SESSION_CREATOR);
        feedbackQuestion.setRecipientType(QuestionRecipientType.SELF);
        feedbackQuestion.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS,
                details.validateGiverRecipientVisibility(feedbackQuestion));
        assertEquals(QuestionGiverType.STUDENTS, feedbackQuestion.getGiverType());
        assertEquals(QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF, feedbackQuestion.getRecipientType());
        assertEquals(Arrays.asList(FeedbackVisibilityType.RECIPIENT,
                FeedbackVisibilityType.GIVER_TEAM_MEMBERS, FeedbackVisibilityType.INSTRUCTORS),
                feedbackQuestion.getShowResponsesTo());

    }
}

package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.util.Const;
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
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackContributionQuestionDetails();
        assertFalse(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsFeedbackParticipantCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackContributionQuestionDetails();
        assertFalse(feedbackQuestionDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
    }

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
    public void testGetQuestionResultStatisticsJson() {
        FeedbackContributionQuestionDetails feedbackContributionQuestionDetails = new FeedbackContributionQuestionDetails();

        DataBundle responseBundle = loadDataBundle("/FeedbackContributionQuestionTest.json");
        populateQuestionAndResponseIds(responseBundle);

        SessionResultsBundle bundle =
                new SessionResultsBundle(
                        responseBundle.feedbackQuestions, new HashMap<>(), new HashSet<>(),
                        new ArrayList<>(responseBundle.feedbackResponses.values()), new ArrayList<>(),
                        new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
                        new CourseRoster(new ArrayList<>(responseBundle.students.values()),
                                new ArrayList<>(responseBundle.instructors.values())));

        FeedbackQuestionAttributes fqa;

        ______TS("(student email specified): all students have response");
        fqa = responseBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        assertEquals("{\n"
                + "  \"results\": {\n"
                + "    \"student1InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": 10,\n"
                + "      \"perceived\": 17,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student2InCourse1@gmail.tmt\": 20,\n"
                + "        \"student3InCourse1@gmail.tmt\": 30\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        24,\n"
                + "        19\n"
                + "      ]\n"
                + "    }\n"
                + "  }\n"
                + "}", feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa,
                        "student1InCourse1@gmail.tmt", bundle));

        ______TS("(student email specified): mix of students with responses and students without responses");
        fqa = responseBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        assertEquals("{\n"
                + "  \"results\": {\n"
                + "    \"student5InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": 10,\n"
                + "      \"perceived\": 15,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student6InCourse1@gmail.tmt\": 20,\n"
                + "        \"student4InCourse1@gmail.tmt\": -999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        15,\n"
                + "        -9999\n"
                + "      ]\n"
                + "    }\n"
                + "  }\n"
                + "}", feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa,
                "student5InCourse1@gmail.tmt", bundle));

        ______TS("(student email specified): all students do not have responses");
        fqa = responseBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        assertEquals("{\n"
                + "  \"results\": {}\n"
                + "}", feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa,
                "student8InCourse1@gmail.tmt", bundle));

        ______TS("(student email not specified): qn1");
        fqa = responseBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        assertEquals("{\n"
                + "  \"results\": {\n"
                + "    \"student6InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student5InCourse1@gmail.tmt\": -9999,\n"
                + "        \"student4InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999,\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student7InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student8InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student8InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student7InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student2InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": 100,\n"
                + "      \"perceived\": 93,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student1InCourse1@gmail.tmt\": 80,\n"
                + "        \"student3InCourse1@gmail.tmt\": 120\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        107,\n"
                + "        80\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student5InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student6InCourse1@gmail.tmt\": -9999,\n"
                + "        \"student4InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999,\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student1InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": 50,\n"
                + "      \"perceived\": 87,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student2InCourse1@gmail.tmt\": 80,\n"
                + "        \"student3InCourse1@gmail.tmt\": 120\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        93,\n"
                + "        80\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student4InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student6InCourse1@gmail.tmt\": -9999,\n"
                + "        \"student5InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999,\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student3InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": 113,\n"
                + "      \"perceived\": 120,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student2InCourse1@gmail.tmt\": 107,\n"
                + "        \"student1InCourse1@gmail.tmt\": 93\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        120,\n"
                + "        120\n"
                + "      ]\n"
                + "    }\n"
                + "  }\n"
                + "}", feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa, null, bundle));

        ______TS("(student email not specified): qn2");
        fqa = responseBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        assertEquals("{\n"
                        + "  \"results\": {\n"
                        + "    \"student6InCourse1@gmail.tmt\": {\n"
                        + "      \"claimed\": 114,\n"
                        + "      \"perceived\": 100,\n"
                        + "      \"claimedOthers\": {\n"
                        + "        \"student5InCourse1@gmail.tmt\": 100,\n"
                        + "        \"student4InCourse1@gmail.tmt\": -9999\n"
                        + "      },\n"
                        + "      \"perceivedOthers\": [\n"
                        + "        100,\n"
                        + "        -9999\n"
                        + "      ]\n"
                        + "    },\n"
                        + "    \"student7InCourse1@gmail.tmt\": {\n"
                        + "      \"claimed\": -999,\n"
                        + "      \"perceived\": -9999,\n"
                        + "      \"claimedOthers\": {\n"
                        + "        \"student8InCourse1@gmail.tmt\": -9999\n"
                        + "      },\n"
                        + "      \"perceivedOthers\": [\n"
                        + "        -9999\n"
                        + "      ]\n"
                        + "    },\n"
                        + "    \"student8InCourse1@gmail.tmt\": {\n"
                        + "      \"claimed\": -999,\n"
                        + "      \"perceived\": -9999,\n"
                        + "      \"claimedOthers\": {\n"
                        + "        \"student7InCourse1@gmail.tmt\": -9999\n"
                        + "      },\n"
                        + "      \"perceivedOthers\": [\n"
                        + "        -9999\n"
                        + "      ]\n"
                        + "    },\n"
                        + "    \"student2InCourse1@gmail.tmt\": {\n"
                        + "      \"claimed\": -999,\n"
                        + "      \"perceived\": -9999,\n"
                        + "      \"claimedOthers\": {\n"
                        + "        \"student1InCourse1@gmail.tmt\": -9999,\n"
                        + "        \"student3InCourse1@gmail.tmt\": -9999\n"
                        + "      },\n"
                        + "      \"perceivedOthers\": [\n"
                        + "        -9999,\n"
                        + "        -9999\n"
                        + "      ]\n"
                        + "    },\n"
                        + "    \"student5InCourse1@gmail.tmt\": {\n"
                        + "      \"claimed\": 67,\n"
                        + "      \"perceived\": 100,\n"
                        + "      \"claimedOthers\": {\n"
                        + "        \"student6InCourse1@gmail.tmt\": 100,\n"
                        + "        \"student4InCourse1@gmail.tmt\": -9999\n"
                        + "      },\n"
                        + "      \"perceivedOthers\": [\n"
                        + "        100,\n"
                        + "        -9999\n"
                        + "      ]\n"
                        + "    },\n"
                        + "    \"student1InCourse1@gmail.tmt\": {\n"
                        + "      \"claimed\": -999,\n"
                        + "      \"perceived\": -9999,\n"
                        + "      \"claimedOthers\": {\n"
                        + "        \"student2InCourse1@gmail.tmt\": -9999,\n"
                        + "        \"student3InCourse1@gmail.tmt\": -9999\n"
                        + "      },\n"
                        + "      \"perceivedOthers\": [\n"
                        + "        -9999,\n"
                        + "        -9999\n"
                        + "      ]\n"
                        + "    },\n"
                        + "    \"student4InCourse1@gmail.tmt\": {\n"
                        + "      \"claimed\": -999,\n"
                        + "      \"perceived\": -9999,\n"
                        + "      \"claimedOthers\": {\n"
                        + "        \"student6InCourse1@gmail.tmt\": -9999,\n"
                        + "        \"student5InCourse1@gmail.tmt\": -9999\n"
                        + "      },\n"
                        + "      \"perceivedOthers\": [\n"
                        + "        -9999,\n"
                        + "        -9999\n"
                        + "      ]\n"
                        + "    },\n"
                        + "    \"student3InCourse1@gmail.tmt\": {\n"
                        + "      \"claimed\": -999,\n"
                        + "      \"perceived\": -9999,\n"
                        + "      \"claimedOthers\": {\n"
                        + "        \"student2InCourse1@gmail.tmt\": -9999,\n"
                        + "        \"student1InCourse1@gmail.tmt\": -9999\n"
                        + "      },\n"
                        + "      \"perceivedOthers\": [\n"
                        + "        -9999,\n"
                        + "        -9999\n"
                        + "      ]\n"
                        + "    }\n"
                        + "  }\n"
                        + "}", feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa, null, bundle));

        ______TS("(student email not specified): qn3");
        fqa = responseBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        assertEquals("{\n"
                + "  \"results\": {\n"
                + "    \"student6InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student5InCourse1@gmail.tmt\": -9999,\n"
                + "        \"student4InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999,\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student7InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student8InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student8InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student7InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student2InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student1InCourse1@gmail.tmt\": -9999,\n"
                + "        \"student3InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999,\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student5InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student6InCourse1@gmail.tmt\": -9999,\n"
                + "        \"student4InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999,\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student1InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student2InCourse1@gmail.tmt\": -9999,\n"
                + "        \"student3InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999,\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student4InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student6InCourse1@gmail.tmt\": -9999,\n"
                + "        \"student5InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999,\n"
                + "        -9999\n"
                + "      ]\n"
                + "    },\n"
                + "    \"student3InCourse1@gmail.tmt\": {\n"
                + "      \"claimed\": -999,\n"
                + "      \"perceived\": -9999,\n"
                + "      \"claimedOthers\": {\n"
                + "        \"student2InCourse1@gmail.tmt\": -9999,\n"
                + "        \"student1InCourse1@gmail.tmt\": -9999\n"
                + "      },\n"
                + "      \"perceivedOthers\": [\n"
                + "        -9999,\n"
                + "        -9999\n"
                + "      ]\n"
                + "    }\n"
                + "  }\n"
                + "}", feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa, null, bundle));

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
        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.builder()
                .withCourseId("course")
                .withFeedbackSessionName("session")
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF)
                .withQuestionNumber(1)
                .withNumberOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS)
                .withShowResponsesTo(Arrays.asList(FeedbackParticipantType.RECEIVER,
                        FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                        FeedbackParticipantType.OWN_TEAM_MEMBERS,
                        FeedbackParticipantType.INSTRUCTORS))
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .withQuestionDescription("description")
                .build();

        ______TS("success: valid giver recipient visibility");
        assertEquals("", details.validateGiverRecipientVisibility(feedbackQuestionAttributes));

        ______TS("failure: giver type is not STUDENT");
        feedbackQuestionAttributes.setGiverType(FeedbackParticipantType.SELF);
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_FEEDBACK_PATH,
                details.validateGiverRecipientVisibility(feedbackQuestionAttributes));
        assertEquals(FeedbackParticipantType.STUDENTS, feedbackQuestionAttributes.getGiverType());

        ______TS("failure: recipient type can only be OWN_TEAM_MEMBERS_INCLUDING_SELF");
        feedbackQuestionAttributes.setGiverType(FeedbackParticipantType.STUDENTS);
        feedbackQuestionAttributes.setRecipientType(FeedbackParticipantType.SELF);
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_FEEDBACK_PATH,
                details.validateGiverRecipientVisibility(feedbackQuestionAttributes));
        assertEquals(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, feedbackQuestionAttributes.getRecipientType());

        ______TS("failure: giver type is not STUDENT and recipient type is not OWN_TEAM_MEMBERS_INCLUDING_SELF");
        feedbackQuestionAttributes.setGiverType(FeedbackParticipantType.SELF);
        feedbackQuestionAttributes.setRecipientType(FeedbackParticipantType.SELF);
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_FEEDBACK_PATH,
                details.validateGiverRecipientVisibility(feedbackQuestionAttributes));
        assertEquals(FeedbackParticipantType.STUDENTS, feedbackQuestionAttributes.getGiverType());
        assertEquals(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, feedbackQuestionAttributes.getRecipientType());

        ______TS("failure: invalid restrictions on visibility options");
        feedbackQuestionAttributes.setGiverType(FeedbackParticipantType.STUDENTS);
        feedbackQuestionAttributes.setRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
        feedbackQuestionAttributes.setShowResponsesTo(Arrays.asList(FeedbackParticipantType.RECEIVER));
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS,
                details.validateGiverRecipientVisibility(feedbackQuestionAttributes));
        assertEquals(Arrays.asList(FeedbackParticipantType.RECEIVER, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.INSTRUCTORS),
                feedbackQuestionAttributes.getShowResponsesTo());

        ______TS("failure: giver type is not STUDENT and invalid restrictions on visibility options");
        feedbackQuestionAttributes.setGiverType(FeedbackParticipantType.SELF);
        feedbackQuestionAttributes.setRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
        feedbackQuestionAttributes.setShowResponsesTo(Arrays.asList(FeedbackParticipantType.RECEIVER));
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS,
                details.validateGiverRecipientVisibility(feedbackQuestionAttributes));
        assertEquals(FeedbackParticipantType.STUDENTS, feedbackQuestionAttributes.getGiverType());
        assertEquals(Arrays.asList(FeedbackParticipantType.RECEIVER, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.INSTRUCTORS),
                feedbackQuestionAttributes.getShowResponsesTo());

        ______TS("failure: recipient type is not OWN_TEAM_MEMBERS_INCLUDING_SELF and invalid restrictions on "
                + "visibility options");
        feedbackQuestionAttributes.setGiverType(FeedbackParticipantType.STUDENTS);
        feedbackQuestionAttributes.setRecipientType(FeedbackParticipantType.SELF);
        feedbackQuestionAttributes.setShowResponsesTo(Arrays.asList(FeedbackParticipantType.RECEIVER));
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS,
                details.validateGiverRecipientVisibility(feedbackQuestionAttributes));
        assertEquals(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, feedbackQuestionAttributes.getRecipientType());
        assertEquals(Arrays.asList(FeedbackParticipantType.RECEIVER, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.INSTRUCTORS),
                feedbackQuestionAttributes.getShowResponsesTo());

        ______TS("failure: giver type is not STUDENT and recipient type is not OWN_TEAM_MEMBERS_INCLUDING_SELF"
                + " and invalid restrictions on visibility options");
        feedbackQuestionAttributes.setGiverType(FeedbackParticipantType.SELF);
        feedbackQuestionAttributes.setRecipientType(FeedbackParticipantType.SELF);
        feedbackQuestionAttributes.setShowResponsesTo(Arrays.asList(FeedbackParticipantType.RECEIVER));
        assertEquals(FeedbackContributionQuestionDetails.CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS,
                details.validateGiverRecipientVisibility(feedbackQuestionAttributes));
        assertEquals(FeedbackParticipantType.STUDENTS, feedbackQuestionAttributes.getGiverType());
        assertEquals(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF, feedbackQuestionAttributes.getRecipientType());
        assertEquals(Arrays.asList(FeedbackParticipantType.RECEIVER, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS,
                FeedbackParticipantType.OWN_TEAM_MEMBERS, FeedbackParticipantType.INSTRUCTORS),
                feedbackQuestionAttributes.getShowResponsesTo());

    }

}

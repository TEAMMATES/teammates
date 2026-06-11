package teammates.common.datatransfer.questions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.visibility.FeedbackVisibilityType;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.Student;
import teammates.storage.entity.questions.FeedbackContributionQuestion;
import teammates.test.BaseTestCase;

import tools.jackson.core.type.TypeReference;

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

    @Test
    public void testGetQuestionResultStatisticsJson() {
        FeedbackContributionQuestionDetails feedbackContributionQuestionDetails = new FeedbackContributionQuestionDetails();

        DataBundle responseBundle = loadDataBundle("/FeedbackContributionQuestionTest.json");

        SessionResultsBundle resultsBundle = new SessionResultsBundle(
                new ArrayList<>(responseBundle.feedbackQuestions.values()),
                new HashSet<>(), new HashSet<>(),
                new ArrayList<>(responseBundle.feedbackResponses.values()),
                new ArrayList<>(),
                new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
                new CourseRoster(new ArrayList<>(responseBundle.students.values()),
                        new ArrayList<>(responseBundle.instructors.values())));

        FeedbackContributionQuestion fqa;
        Student student1 = responseBundle.students.get("student1InCourse1");
        Student student2 = responseBundle.students.get("student2InCourse1");
        Student student3 = responseBundle.students.get("student3InCourse1");
        Student student5 = responseBundle.students.get("student5InCourse1");
        Student student6 = responseBundle.students.get("student6InCourse1");
        Student student7 = responseBundle.students.get("student7InCourse1");
        Student student8 = responseBundle.students.get("student8InCourse1");

        ______TS("(student user ID specified): all students have response");
        fqa = (FeedbackContributionQuestion) responseBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        Map<String, Map<String, Object>> studentStats = getContributionResults(
                feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa, student1.getId(), resultsBundle));
        assertEquals(1, studentStats.size());
        Map<String, Object> student1Entry = studentStats.get(student1.getId().toString());
        assertEquals(10, intField(student1Entry, "claimed"));
        assertEquals(17, intField(student1Entry, "perceived"));
        assertEquals(Map.of(student2.getId().toString(), 20, student3.getId().toString(), 30),
                intMapField(student1Entry, "claimedOthers"));
        assertEquals(List.of(24, 19), intListField(student1Entry, "perceivedOthers"));

        ______TS("(student user ID specified): mix of students with responses and students without responses");
        fqa = (FeedbackContributionQuestion) responseBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        studentStats = getContributionResults(
                feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa, student5.getId(), resultsBundle));
        assertEquals(1, studentStats.size());
        Map<String, Object> student5Entry = studentStats.get(student5.getId().toString());
        assertEquals(10, intField(student5Entry, "claimed"));
        assertEquals(15, intField(student5Entry, "perceived"));
        assertEquals(Map.of(student6.getId().toString(), 20),
                intMapField(student5Entry, "claimedOthers"));
        assertEquals(List.of(15), intListField(student5Entry, "perceivedOthers"));

        ______TS("(student user ID specified): all students do not have responses");
        fqa = (FeedbackContributionQuestion) responseBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        studentStats = getContributionResults(
                feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa, student8.getId(), resultsBundle));
        assertTrue(studentStats.isEmpty());

        ______TS("(student user ID not specified): qn1");
        fqa = (FeedbackContributionQuestion) responseBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        Map<String, Map<String, Object>> instructorStats = getContributionResults(
                feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa, null, resultsBundle));
        assertEquals(8, instructorStats.size());
        Map<String, Object> student2Entry = instructorStats.get(student2.getId().toString());
        assertEquals(100, intField(student2Entry, "claimed"));
        assertEquals(93, intField(student2Entry, "perceived"));
        assertEquals(Map.of(student1.getId().toString(), 80, student3.getId().toString(), 120),
                intMapField(student2Entry, "claimedOthers"));
        assertEquals(List.of(107, 80), intListField(student2Entry, "perceivedOthers"));
        Map<String, Object> student8Entry = instructorStats.get(student8.getId().toString());
        assertEquals(-999, intField(student8Entry, "claimed"));
        assertEquals(-9999, intField(student8Entry, "perceived"));
        assertEquals(Map.of(student7.getId().toString(), -9999), intMapField(student8Entry, "claimedOthers"));

        ______TS("(student user ID not specified): qn2");
        fqa = (FeedbackContributionQuestion) responseBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        instructorStats = getContributionResults(
                feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa, null, resultsBundle));
        assertEquals(8, instructorStats.size());
        student5Entry = instructorStats.get(student5.getId().toString());
        assertEquals(67, intField(student5Entry, "claimed"));
        assertEquals(100, intField(student5Entry, "perceived"));
        assertEquals(Map.of(student6.getId().toString(), 100),
                intMapField(student5Entry, "claimedOthers"));
        Map<String, Object> student6Entry = instructorStats.get(student6.getId().toString());
        assertEquals(114, intField(student6Entry, "claimed"));
        assertEquals(100, intField(student6Entry, "perceived"));
        assertEquals(Map.of(student5.getId().toString(), 100),
                intMapField(student6Entry, "claimedOthers"));

        ______TS("(student user ID not specified): qn3");
        fqa = (FeedbackContributionQuestion) responseBundle.feedbackQuestions.get("qn3InSession1InCourse1");
        instructorStats = getContributionResults(
                feedbackContributionQuestionDetails.getQuestionResultStatisticsJson(fqa, null, resultsBundle));
        assertEquals(8, instructorStats.size());
        assertTrue(instructorStats.values().stream()
                .allMatch(entry -> intField(entry, "claimed") == -999 && intField(entry, "perceived") == -9999));

    }

    private Map<String, Map<String, Object>> getContributionResults(String json) {
        Map<String, Map<String, Map<String, Object>>> stats = JsonUtils.fromJson(
                json, new TypeReference<>() { });
        return stats.get("results");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> intMapField(Map<String, Object> entry, String field) {
        Map<String, Object> rawMap = (Map<String, Object>) entry.get(field);
        Map<String, Integer> parsedMap = new LinkedHashMap<>();
        rawMap.forEach((key, value) -> parsedMap.put(key, ((Number) value).intValue()));
        return parsedMap;
    }

    @SuppressWarnings("unchecked")
    private List<Integer> intListField(Map<String, Object> entry, String field) {
        return ((List<Object>) entry.get(field)).stream()
                .map(value -> ((Number) value).intValue())
                .toList();
    }

    private int intField(Map<String, Object> entry, String field) {
        return ((Number) entry.get(field)).intValue();
    }

}

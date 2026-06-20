package teammates.ui.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.FeedbackMissingResponse;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackContributionResponseDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatistics;
import teammates.common.util.Const;
import teammates.logic.statistics.FeedbackQuestionResultsStatisticsFactory;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;

/**
 * API output format for course-wide session results, including statistics.
 */
public class SessionResultsData implements ApiOutput {

    final List<QuestionOutput> questions = new ArrayList<>();

    SessionResultsData() {
        // use factory method instead
    }

    /**
     * Factory method to construct API output for course-wide results.
     */
    public static SessionResultsData init(SessionResultsBundle bundle) {
        SessionResultsData sessionResultsData = new SessionResultsData();

        Map<FeedbackQuestion, List<FeedbackResponse>> questionsWithResponses =
                bundle.getQuestionResponseMap();

        questionsWithResponses.forEach((question, responses) -> {
            FeedbackQuestionResultsStatistics questionStatistics =
                    FeedbackQuestionResultsStatisticsFactory.calculateCourseWide(question, responses, bundle);
            Map<UUID, Integer> normalizedContributionResponseValues =
                    FeedbackQuestionResultsStatisticsFactory.calculateNormalizedContributionResponseValues(
                            question, responses, bundle, null);
            QuestionOutput qnOutput = new QuestionOutput(question, questionStatistics);
            List<ResponseOutput> allResponses = buildResponses(responses, bundle,
                    normalizedContributionResponseValues);
            qnOutput.allResponses.addAll(allResponses);

            List<FeedbackMissingResponse> missingResponses = bundle.getQuestionMissingResponseMap().get(question);
            qnOutput.allResponses.addAll(buildMissingResponses(missingResponses, bundle));

            sessionResultsData.questions.add(qnOutput);
        });

        return sessionResultsData;
    }

    private static List<ResponseOutput> buildResponses(
            List<FeedbackResponse> responses,
            SessionResultsBundle bundle,
            Map<UUID, Integer> normalizedContributionValues) {
        List<ResponseOutput> output = new ArrayList<>();

        for (FeedbackResponse response : responses) {
            output.add(buildSingleResponse(
                    response,
                    bundle,
                    getResponseDetailsForResults(response, normalizedContributionValues)));
        }

        return output;
    }

    private static ResponseOutput buildSingleResponse(
            FeedbackResponse response,
            SessionResultsBundle bundle,
            FeedbackResponseDetails responseDetails) {
        ResponseGiver responseGiver = response.getGiver();
        String giverEmail = null;
        String userIdForModeration = null;
        if (bundle.isResponseGiverVisible(response.getId())) {
            if (responseGiver.isGiverUser()) {
                giverEmail = responseGiver.getGiverUser().getEmail();
                userIdForModeration = responseGiver.getGiverUser().getId().toString();
            } else {
                List<Student> teamMembers = bundle.getRoster().getTeamMembers(responseGiver.getTeamId());
                userIdForModeration = teamMembers.isEmpty() ? null : teamMembers.iterator().next().getId().toString();
                giverEmail = null;
            }
        }
        String giverName = getGiverNameOfResponse(response.getId(), responseGiver, bundle);
        String giverTeam = responseGiver.getTeamName();
        String giverSectionName = responseGiver.getSectionName();

        ResponseRecipient responseRecipient = response.getRecipient();
        String recipientEmail = null;
        String recipientName = getRecipientNameOfResponse(response.getId(), responseRecipient, bundle);
        String recipientTeam = responseRecipient.getTeamName();
        String recipientSectionName = responseRecipient.getSectionName();

        if (bundle.isResponseRecipientVisible(response.getId(), responseRecipient.getRecipientType())
                && responseRecipient.isRecipientUser()) {
            recipientEmail = responseRecipient.getRecipientUser().getEmail();
        }

        List<ResponseInstructorComment> responseInstructorComments =
                bundle.getResponseCommentsMap().getOrDefault(response, Collections.emptyList());
        List<ResponseInstructorCommentData> instructorComments =
                buildInstructorComments(responseInstructorComments, bundle);
        String participantComment = response.getGiverComment();

        return ResponseOutput.builder()
                .withIsMissingResponse(false)
                .withResponseId(response.getId().toString())
                .withGiver(giverName)
                .withGiverUserId(responseGiver.getGiverUserId())
                .withGiverTeamId(responseGiver.getTeamId())
                .withGiverTeam(giverTeam)
                .withGiverEmail(giverEmail)
                .withUserIdForModeration(userIdForModeration)
                .withGiverSectionName(giverSectionName)
                .withGiverSectionId(responseGiver.getSectionId())
                .withRecipient(recipientName)
                .withRecipientUserId(responseRecipient.getRecipientUserId())
                .withRecipientTeamId(responseRecipient.getTeamId())
                .withRecipientTeam(recipientTeam)
                .withRecipientEmail(recipientEmail)
                .withRecipientSectionName(recipientSectionName)
                .withRecipientSectionId(responseRecipient.getSectionId())
                .withResponseDetails(responseDetails)
                .withParticipantComment(participantComment)
                .withInstructorComments(instructorComments)
                .build();
    }

    private static List<ResponseOutput> buildMissingResponses(
            List<FeedbackMissingResponse> responses, SessionResultsBundle bundle) {
        List<ResponseOutput> output = new ArrayList<>();

        for (FeedbackMissingResponse response : responses) {
            output.add(buildSingleMissingResponse(response, bundle));
        }

        return output;
    }

    private static ResponseOutput buildSingleMissingResponse(
            FeedbackMissingResponse response, SessionResultsBundle bundle) {
        ResponseGiver responseGiver = response.giver();
        String giverEmail = null;
        String userIdForModeration = null;

        if (bundle.isResponseGiverVisible(response.id())) {
            if (responseGiver.isGiverUser()) {
                giverEmail = responseGiver.getGiverUser().getEmail();
                userIdForModeration = responseGiver.getGiverUser().getId().toString();
            } else {
                List<Student> teamMembers = bundle.getRoster().getTeamMembers(responseGiver.getTeamId());
                userIdForModeration = teamMembers.stream().findFirst()
                        .map(student -> student.getId().toString()).orElse(null);
                giverEmail = null;
            }
        }
        String giverName = getGiverNameOfResponse(response.id(), responseGiver, bundle);
        String giverTeam = responseGiver.getTeamName();
        String giverSectionName = responseGiver.getSectionName();

        ResponseRecipient responseRecipient = response.recipient();
        String recipientEmail = null;
        String recipientName = getRecipientNameOfResponse(response.id(), responseRecipient, bundle);
        String recipientTeam = responseRecipient.getTeamName();
        String recipientSectionName = responseRecipient.getSectionName();

        if (bundle.isResponseRecipientVisible(response.id(), responseRecipient.getRecipientType())
                && responseRecipient.isRecipientUser()) {
            recipientEmail = responseRecipient.getRecipientUser().getEmail();
        }

        FeedbackTextResponseDetails responseDetails = new FeedbackTextResponseDetails(Const.MISSING_RESPONSE_TEXT);
        return ResponseOutput.builder()
                .withIsMissingResponse(true)
                .withResponseId(response.id().toString())
                .withGiver(giverName)
                .withGiverUserId(responseGiver.getGiverUserId())
                .withGiverTeamId(responseGiver.getTeamId())
                .withGiverTeam(giverTeam)
                .withGiverEmail(giverEmail)
                .withUserIdForModeration(userIdForModeration)
                .withGiverSectionName(giverSectionName)
                .withGiverSectionId(responseGiver.getSectionId())
                .withRecipient(recipientName)
                .withRecipientUserId(responseRecipient.getRecipientUserId())
                .withRecipientTeamId(responseRecipient.getTeamId())
                .withRecipientTeam(recipientTeam)
                .withRecipientEmail(recipientEmail)
                .withRecipientSectionName(recipientSectionName)
                .withRecipientSectionId(responseRecipient.getSectionId())
                .withResponseDetails(responseDetails)
                .withParticipantComment(null)
                .withInstructorComments(new ArrayList<>())
                .build();
    }

    private static String getGiverNameOfResponse(UUID responseId,
            ResponseGiver responseGiver, SessionResultsBundle bundle) {
        if (bundle.isResponseGiverVisible(responseId)) {
            return responseGiver.getDisplayName();
        } else {
            return SessionResultsBundle.getAnonGiverName(responseGiver);
        }
    }

    private static String getRecipientNameOfResponse(UUID responseId,
            ResponseRecipient responseRecipient, SessionResultsBundle bundle) {
        if (bundle.isResponseRecipientVisible(responseId, responseRecipient.getRecipientType())) {
            return responseRecipient.getDisplayName();
        } else {
            return SessionResultsBundle.getAnonRecipientName(responseRecipient);
        }
    }

    private static List<ResponseInstructorCommentData> buildInstructorComments(
            List<ResponseInstructorComment> responseInstructorComments, SessionResultsBundle bundle) {
        List<ResponseInstructorCommentData> outputs = new ArrayList<>();

        for (ResponseInstructorComment comment : responseInstructorComments) {
            outputs.add(new ResponseInstructorCommentData(comment, bundle.isCommentGiverVisible(comment)));
        }

        return outputs;
    }

    public List<QuestionOutput> getQuestions() {
        return questions;
    }

    private static FeedbackResponseDetails getResponseDetailsForResults(
            FeedbackResponse response,
            Map<UUID, Integer> normalizedContributionValues) {
        if (response.getFeedbackQuestion().getQuestionType()
                != FeedbackQuestionType.CONTRIB) {
            return response.getFeedbackResponseDetailsCopy();
        }

        // Contribution responses are normalized for display
        Integer normalizedValue = normalizedContributionValues.get(response.getId());
        if (normalizedValue == null) {
            return response.getFeedbackResponseDetailsCopy();
        }

        FeedbackContributionResponseDetails responseDetails = new FeedbackContributionResponseDetails();
        responseDetails.setAnswer(normalizedValue);
        return responseDetails;
    }

    /**
     * API output format for questions in course-wide session results.
     */
    public static final class QuestionOutput {

        private final FeedbackQuestionData feedbackQuestion;
        @Nullable
        private final FeedbackQuestionResultsStatistics questionStatistics;
        private final List<ResponseOutput> allResponses = new ArrayList<>();

        private QuestionOutput(FeedbackQuestion feedbackQuestion,
                @Nullable FeedbackQuestionResultsStatistics questionStatistics) {
            this.feedbackQuestion = new FeedbackQuestionData(feedbackQuestion);
            this.questionStatistics = questionStatistics;
        }

        public FeedbackQuestionData getFeedbackQuestion() {
            return feedbackQuestion;
        }

        @Nullable
        public FeedbackQuestionResultsStatistics getQuestionStatistics() {
            return questionStatistics;
        }

        public List<ResponseOutput> getAllResponses() {
            return allResponses;
        }

    }
}

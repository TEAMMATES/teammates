package teammates.ui.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.statistics.FeedbackQuestionResultsStatistics;
import teammates.common.util.Const;
import teammates.logic.statistics.FeedbackQuestionResultsStatisticsFactory;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;

/**
 * API output format for user-specific session results, including statistics.
 */
public class UserSessionResultsData implements ApiOutput {

    private static final String REGEX_ANONYMOUS_PARTICIPANT_HASH = "[0-9]{1,10}";

    final List<UserQuestionOutput> questions = new ArrayList<>();

    UserSessionResultsData() {
        // use factory method instead
    }

    /**
     * Factory method to construct API output for a specific user.
     */
    public static UserSessionResultsData initForUser(SessionResultsBundle bundle, User user) {
        UserSessionResultsData sessionResultsData = new UserSessionResultsData();

        Map<FeedbackQuestion, List<FeedbackResponse>> questionsWithResponses =
                bundle.getQuestionResponseMap();

        questionsWithResponses.forEach((question, responses) -> {
            FeedbackQuestionDetails questionDetails = question.getQuestionDetailsCopy();
            boolean hasResponseButNotVisibleForPreview = bundle.getQuestionsNotVisibleForPreviewSet()
                    .contains(question);
            boolean hasCommentNotVisibleForPreview = bundle.getQuestionsWithCommentNotVisibleForPreviewSet()
                    .contains(question);

            FeedbackQuestionResultsStatistics questionStatistics = hasResponseButNotVisibleForPreview
                    ? null
                    : FeedbackQuestionResultsStatisticsFactory.calculateForRecipient(
                            question, responses, bundle, user);
            UserQuestionOutput qnOutput = new UserQuestionOutput(question,
                    questionStatistics,
                    hasResponseButNotVisibleForPreview,
                    hasCommentNotVisibleForPreview);
            Map<ResponseRecipient, List<ResponseOutput>> otherResponsesMap = new HashMap<>();

            qnOutput.getFeedbackQuestion().hideInformationForStudent();

            if (!hasResponseButNotVisibleForPreview && questionDetails.isIndividualResponsesShownToStudents()) {
                for (FeedbackResponse response : responses) {
                    boolean isUserGiver = Objects.equals(user, response.getGiver().getGiverUser());
                    boolean isUserRecipient = Objects.equals(user, response.getRecipient().getRecipientUser());

                    ResponseOutput responseOutput = buildSingleResponseForUser(
                            response,
                            bundle,
                            user,
                            response.getFeedbackResponseDetailsCopy());

                    if (isUserRecipient) {
                        qnOutput.responsesToSelf.add(responseOutput);
                    }

                    if (isUserGiver) {
                        qnOutput.responsesFromSelf.add(responseOutput);
                    }

                    if (!isUserRecipient && !isUserGiver) {
                        otherResponsesMap.computeIfAbsent(response.getRecipient(), k -> new ArrayList<>())
                                .add(responseOutput);
                    }

                    qnOutput.allResponses.add(responseOutput);
                }
            }
            qnOutput.otherResponses.addAll(otherResponsesMap.values());

            sessionResultsData.questions.add(qnOutput);
        });

        return sessionResultsData;
    }

    private static ResponseOutput buildSingleResponseForUser(
            FeedbackResponse response,
            SessionResultsBundle bundle,
            User user,
            FeedbackResponseDetails responseDetails) {
        Objects.requireNonNull(user);

        ResponseGiver giver = response.getGiver();
        boolean isUserGiver = Objects.equals(user, giver.getGiverUser());
        boolean isUserTeamGiver = false;
        if (user instanceof Student student) {
            isUserTeamGiver = Objects.equals(student.getTeam(), giver.getGiverTeam());
        }

        String giverName;
        String giverTeam = "";
        if (isUserTeamGiver) {
            giverName = String.format("Your Team (%s)", giver.getTeamName());
            giverTeam = giver.getTeamName();
        } else if (isUserGiver) {
            giverName = "You";
            giverTeam = giver.getTeamName();
        } else {
            giverName = removeAnonymousHash(
                    getGiverNameOfResponse(response.getId(), giver, bundle));
        }

        ResponseRecipient recipient = response.getRecipient();
        boolean isUserRecipient = Objects.equals(user, recipient.getRecipientUser());
        boolean isUserTeamRecipient = false;
        if (user instanceof Student student) {
            isUserTeamRecipient = Objects.equals(student.getTeam(), recipient.getRecipientTeam());
        }

        String recipientName;
        String recipientTeam = "";
        if (isUserRecipient) {
            recipientName = "You";
            recipientTeam = recipient.getTeamName();
        } else if (isUserTeamRecipient) {
            recipientName = String.format("Your Team (%s)", recipient.getDisplayName());
            recipientTeam = recipient.getTeamName();
        } else {
            recipientName = removeAnonymousHash(
                    getRecipientNameOfResponse(response.getId(), recipient, bundle));
            if (bundle.isResponseRecipientVisible(response.getId(), recipient.getRecipientType())) {
                recipientTeam = recipient.getTeamName();
            }
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
                .withGiverUserId(giver.getGiverUserId())
                .withGiverTeamId(giver.getTeamId())
                .withGiverTeam(giverTeam)
                .withGiverEmail(null)
                .withGiverSectionName(giver.getSectionName())
                .withGiverSectionId(giver.getSectionId())
                .withRecipient(recipientName)
                .withRecipientUserId(recipient.getRecipientUserId())
                .withRecipientTeamId(recipient.getTeamId())
                .withRecipientTeam(recipientTeam)
                .withRecipientEmail(null)
                .withRecipientSectionName(recipient.getSectionName())
                .withRecipientSectionId(recipient.getSectionId())
                .withResponseDetails(responseDetails)
                .withParticipantComment(participantComment)
                .withInstructorComments(instructorComments)
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

    private static String removeAnonymousHash(String identifier) {
        return identifier.replaceAll(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " (student|instructor|team) "
                + REGEX_ANONYMOUS_PARTICIPANT_HASH, Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " $1");
    }

    public List<UserQuestionOutput> getQuestions() {
        return questions;
    }

    /**
     * API output format for questions in user session results.
     */
    public static final class UserQuestionOutput {

        private final FeedbackQuestionData feedbackQuestion;
        @Nullable
        private final FeedbackQuestionResultsStatistics questionStatistics;
        private final boolean hasResponseButNotVisibleForPreview;
        private final boolean hasCommentNotVisibleForPreview;

        private final List<ResponseOutput> allResponses = new ArrayList<>();
        private final List<ResponseOutput> responsesToSelf = new ArrayList<>();
        private final List<ResponseOutput> responsesFromSelf = new ArrayList<>();
        private final List<List<ResponseOutput>> otherResponses = new ArrayList<>();

        private UserQuestionOutput(FeedbackQuestion feedbackQuestion,
                @Nullable FeedbackQuestionResultsStatistics questionStatistics,
                boolean hasResponseButNotVisibleForPreview, boolean hasCommentNotVisibleForPreview) {
            this.feedbackQuestion = new FeedbackQuestionData(feedbackQuestion);
            this.questionStatistics = questionStatistics;
            this.hasResponseButNotVisibleForPreview = hasResponseButNotVisibleForPreview;
            this.hasCommentNotVisibleForPreview = hasCommentNotVisibleForPreview;
        }

        public FeedbackQuestionData getFeedbackQuestion() {
            return feedbackQuestion;
        }

        @Nullable
        public FeedbackQuestionResultsStatistics getQuestionStatistics() {
            return questionStatistics;
        }

        public boolean getHasResponseButNotVisibleForPreview() {
            return hasResponseButNotVisibleForPreview;
        }

        public boolean getHasCommentNotVisibleForPreview() {
            return hasCommentNotVisibleForPreview;
        }

        public List<ResponseOutput> getAllResponses() {
            return allResponses;
        }

        public List<ResponseOutput> getResponsesFromSelf() {
            return responsesFromSelf;
        }

        public List<ResponseOutput> getResponsesToSelf() {
            return responsesToSelf;
        }

        public List<List<ResponseOutput>> getOtherResponses() {
            return otherResponses;
        }

    }
}

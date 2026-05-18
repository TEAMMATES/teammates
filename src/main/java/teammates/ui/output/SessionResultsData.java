package teammates.ui.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.FeedbackMissingResponse;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;

/**
 * API output format for session results, including statistics.
 */
public class SessionResultsData extends ApiOutput {

    private static final String REGEX_ANONYMOUS_PARTICIPANT_HASH = "[0-9]{1,10}";

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
            FeedbackQuestionDetails questionDetails = question.getQuestionDetailsCopy();
            QuestionOutput qnOutput = new QuestionOutput(question,
                    questionDetails.getQuestionResultStatisticsJson(question, null, bundle), false, false);
            // put normal responses
            List<ResponseOutput> allResponses = buildResponses(responses, bundle);
            qnOutput.allResponses.addAll(allResponses);

            // put missing responses
            List<FeedbackMissingResponse> missingResponses = bundle.getQuestionMissingResponseMap().get(question);
            qnOutput.allResponses.addAll(buildMissingResponses(missingResponses, bundle));

            sessionResultsData.questions.add(qnOutput);
        });

        return sessionResultsData;
    }

    /**
     * Factory method to construct API output for user.
     */
    public static SessionResultsData initForUser(SessionResultsBundle bundle, User user) {
        SessionResultsData sessionResultsData = new SessionResultsData();

        Map<FeedbackQuestion, List<FeedbackResponse>> questionsWithResponses =
                bundle.getQuestionResponseMap();

        questionsWithResponses.forEach((question, responses) -> {
            FeedbackQuestionDetails questionDetails = question.getQuestionDetailsCopy();
            // check if question has comments (on any responses) not visible for preview
            boolean hasCommentNotVisibleForPreview = bundle.getQuestionsWithCommentNotVisibleForPreviewSet()
                    .contains(question);
            QuestionOutput qnOutput = new QuestionOutput(question,
                    questionDetails.getQuestionResultStatisticsJson(question, user.getEmail(), bundle),
                    false, hasCommentNotVisibleForPreview);
            Map<ResponseRecipient, List<ResponseOutput>> otherResponsesMap = new HashMap<>();

            qnOutput.getFeedbackQuestion().hideInformationForStudent();

            if (questionDetails.isIndividualResponsesShownToStudents()) {
                for (FeedbackResponse response : responses) {
                    boolean isUserGiver = Objects.equals(user, response.getGiver().getGiverUser());
                    boolean isUserRecipient = Objects.equals(user, response.getRecipient().getRecipientUser());

                    ResponseOutput responseOutput = buildSingleResponseForUser(response, bundle, user);

                    if (isUserRecipient) {
                        qnOutput.responsesToSelf.add(responseOutput);
                    }

                    if (isUserGiver) {
                        qnOutput.responsesFromSelf.add(responseOutput);
                    }

                    if (!isUserRecipient && !isUserGiver) {
                        // we don't need care about the keys of the map here
                        // as only the values of the map will be used
                        otherResponsesMap.computeIfAbsent(response.getRecipient(), k -> new ArrayList<>())
                                .add(responseOutput);
                    }

                    qnOutput.allResponses.add(responseOutput);
                }
            }
            qnOutput.otherResponses.addAll(otherResponsesMap.values());

            sessionResultsData.questions.add(qnOutput);
        });

        Set<FeedbackQuestion> questionsWithResponsesNotVisibleForPreview =
                bundle.getQuestionsNotVisibleForPreviewSet();
        questionsWithResponsesNotVisibleForPreview.forEach(question -> {
            QuestionOutput qnOutput = new QuestionOutput(question, "", true, false);
            sessionResultsData.questions.add(qnOutput);
        });

        return sessionResultsData;
    }

    private static ResponseOutput buildSingleResponseForUser(
            FeedbackResponse response, SessionResultsBundle bundle, User user) {
        Objects.requireNonNull(user);

        // process giver
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
            // we don't want student to figure out who is who by using the hash
            giverName = removeAnonymousHash(getGiverNameOfResponse(response.getId(), giver, bundle));
        }

        // process recipient
        ResponseRecipient recipient = response.getRecipient();
        boolean isUserRecipient = Objects.equals(user, recipient.getRecipientUser());
        boolean isUserTeamRecipient = false;
        boolean isRecipientVisible = bundle.isResponseRecipientVisible(response.getId(), recipient.getRecipientType());
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
            // we don't want student to figure out who is who by using the hash
            recipientName = removeAnonymousHash(
                getRecipientNameOfResponse(response.getId(), recipient, bundle));
            if (isRecipientVisible) {
                recipientTeam = recipient.getTeamName();
            }
        }

        // process comments
        List<FeedbackResponseComment> feedbackResponseComments =
                bundle.getResponseCommentsMap().getOrDefault(response, Collections.emptyList());
        Queue<CommentOutput> comments = buildComments(feedbackResponseComments, bundle);

        return ResponseOutput.builder()
                .withResponseId(response.getId().toString())
                .withGiver(giverName)
                .withGiverTeam(giverTeam)
                .withGiverEmail(null)
                .withRelatedGiverEmail(null)
                .withGiverSectionName(giver.getSectionName())
                .withRecipient(recipientName)
                .withRecipientTeam(recipientTeam)
                .withRecipientEmail(null)
                .withRecipientSectionName(recipient.getSectionName())
                .withResponseDetails(response.getFeedbackResponseDetailsCopy())
                .withParticipantComment(comments.poll())
                .withInstructorComments(new ArrayList<>(comments))
                .build();
    }

    private static String removeAnonymousHash(String identifier) {
        return identifier.replaceAll(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " (student|instructor|team) "
                + REGEX_ANONYMOUS_PARTICIPANT_HASH, Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " $1");
    }

    private static List<ResponseOutput> buildResponses(
            List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        List<ResponseOutput> output = new ArrayList<>();

        for (FeedbackResponse response : responses) {
            output.add(buildSingleResponse(response, bundle));
        }

        return output;
    }

    private static ResponseOutput buildSingleResponse(
            FeedbackResponse response, SessionResultsBundle bundle) {
        // process giver
        ResponseGiver responseGiver = response.getGiver();
        String giverEmail = null;
        String relatedGiverEmail = null;
        if (bundle.isResponseGiverVisible(response.getId())) {
            if (responseGiver.isGiverUser()) {
                giverEmail = responseGiver.getGiverUser().getEmail();
                relatedGiverEmail = responseGiver.getIdentifier();
            } else {
                // team giver, relatedGiverEmail is any team member's email
                String teamName = responseGiver.getTeamName();
                List<Student> teamMembers =
                        bundle.getRoster().getTeamToMembers().getOrDefault(teamName, Collections.emptyList());
                relatedGiverEmail = teamMembers.isEmpty() ? null : teamMembers.iterator().next().getEmail();
                giverEmail = null;
            }
        }
        String giverName = getGiverNameOfResponse(response.getId(), responseGiver, bundle);
        String giverTeam = responseGiver.getTeamName();
        String giverSectionName = responseGiver.getSectionName();

        // process recipient
        ResponseRecipient responseRecipient = response.getRecipient();
        String recipientEmail = null;
        String recipientName = getRecipientNameOfResponse(response.getId(), responseRecipient, bundle);
        String recipientTeam = responseRecipient.getTeamName();
        String recipientSectionName = responseRecipient.getSectionName();

        if (bundle.isResponseRecipientVisible(response.getId(), responseRecipient.getRecipientType())
                && responseRecipient.isRecipientUser()) {
            recipientEmail = responseRecipient.getIdentifier();
        }

        // process comments
        List<FeedbackResponseComment> feedbackResponseComments =
                bundle.getResponseCommentsMap().getOrDefault(response, Collections.emptyList());
        Queue<CommentOutput> comments = buildComments(feedbackResponseComments, bundle);

        return ResponseOutput.builder()
                .withIsMissingResponse(false)
                .withResponseId(response.getId().toString())
                .withGiver(giverName)
                .withGiverTeam(giverTeam)
                .withGiverEmail(giverEmail)
                .withRelatedGiverEmail(relatedGiverEmail)
                .withGiverSectionName(giverSectionName)
                .withRecipient(recipientName)
                .withRecipientTeam(recipientTeam)
                .withRecipientEmail(recipientEmail)
                .withRecipientSectionName(recipientSectionName)
                .withResponseDetails(response.getFeedbackResponseDetailsCopy())
                .withParticipantComment(comments.poll())
                .withInstructorComments(new ArrayList<>(comments))
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
        // process giver
        ResponseGiver responseGiver = response.giver();
        String giverEmail = null;
        String relatedGiverEmail = null;

        if (bundle.isResponseGiverVisible(response.id())) {
            if (responseGiver.isGiverUser()) {
                giverEmail = responseGiver.getGiverUser().getEmail();
                relatedGiverEmail = responseGiver.getIdentifier();
            } else {
                // team giver, relatedGiverEmail is any team member's email
                String teamName = responseGiver.getTeamName();
                relatedGiverEmail = bundle.getRoster().getTeamToMembers().getOrDefault(teamName, Collections.emptyList())
                        .stream().findFirst().map(Student::getEmail).orElse(null);
                giverEmail = null;
            }
        }
        String giverName = getGiverNameOfResponse(response.id(), responseGiver, bundle);
        String giverTeam = responseGiver.getTeamName();
        String giverSectionName = responseGiver.getSectionName();

        // process recipient
        ResponseRecipient responseRecipient = response.recipient();
        String recipientEmail = null;
        String recipientName = getRecipientNameOfResponse(response.id(), responseRecipient, bundle);
        String recipientTeam = responseRecipient.getTeamName();
        String recipientSectionName = responseRecipient.getSectionName();

        if (bundle.isResponseRecipientVisible(response.id(), responseRecipient.getRecipientType())
                && responseRecipient.isRecipientUser()) {
            recipientEmail = responseRecipient.getIdentifier();
        }

        FeedbackTextResponseDetails responseDetails = new FeedbackTextResponseDetails(Const.MISSING_RESPONSE_TEXT);
        return ResponseOutput.builder()
                .withIsMissingResponse(true)
                .withResponseId(response.id().toString())
                .withGiver(giverName)
                .withGiverTeam(giverTeam)
                .withGiverEmail(giverEmail)
                .withRelatedGiverEmail(relatedGiverEmail)
                .withGiverSectionName(giverSectionName)
                .withRecipient(recipientName)
                .withRecipientTeam(recipientTeam)
                .withRecipientEmail(recipientEmail)
                .withRecipientSectionName(recipientSectionName)
                .withResponseDetails(responseDetails)
                .withParticipantComment(null)
                .withInstructorComments(new ArrayList<>())
                .build();
    }

    /**
     * Gets giver name of a response from the bundle.
     *
     * <p>Anonymized the name if necessary.
     */
    private static String getGiverNameOfResponse(UUID responseId,
            ResponseGiver responseGiver, SessionResultsBundle bundle) {
        if (bundle.isResponseGiverVisible(responseId)) {
            return responseGiver.getDisplayName();
        } else {
            return SessionResultsBundle.getAnonGiverName(responseGiver);
        }
    }

    /**
     * Gets recipient name of a response from the bundle.
     *
     * <p>Anonymized the name if necessary.
     */
    private static String getRecipientNameOfResponse(UUID responseId,
            ResponseRecipient responseRecipient, SessionResultsBundle bundle) {
        if (bundle.isResponseRecipientVisible(responseId, responseRecipient.getRecipientType())) {
            return responseRecipient.getDisplayName();
        } else {
            return SessionResultsBundle.getAnonRecipientName(responseRecipient);
        }
    }

    private static Queue<CommentOutput> buildComments(List<FeedbackResponseComment> feedbackResponseComments,
                                                      SessionResultsBundle bundle) {
        LinkedList<CommentOutput> outputs = new LinkedList<>();

        CommentOutput participantComment = null;
        for (FeedbackResponseComment comment : feedbackResponseComments) {
            if (comment.getIsCommentFromFeedbackParticipant()) {
                // participant comment will not need these fields
                participantComment = CommentOutput.builder(comment)
                        .withCommentGiver(null)
                        .withCommentGiverName(null)
                        .withLastEditorEmail(null)
                        .withLastEditorName(null)
                        .build();
            } else {
                String giverEmail = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
                String giverName = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
                String lastEditorEmail = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
                String lastEditorName = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
                if (bundle.isCommentGiverVisible(comment)) {
                    giverEmail = comment.getGiver().getIdentifier();
                    giverName = comment.getGiver().getDisplayName();
                    lastEditorEmail = comment.getLastEditedBy().getIdentifier();
                    lastEditorName = comment.getLastEditedBy().getDisplayName();
                }
                outputs.add(CommentOutput.builder(comment)
                        .withCommentGiver(giverEmail)
                        .withCommentGiverName(giverName)
                        .withLastEditorEmail(lastEditorEmail)
                        .withLastEditorName(lastEditorName)
                        .build());
            }
        }
        outputs.addFirst(participantComment);

        return outputs;
    }

    public List<QuestionOutput> getQuestions() {
        return questions;
    }

    /**
     * API output format for questions in session results.
     */
    public static final class QuestionOutput {

        private final FeedbackQuestionData feedbackQuestion;
        private final String questionStatistics;

        private final List<ResponseOutput> allResponses = new ArrayList<>();
        private final boolean hasResponseButNotVisibleForPreview;
        private final boolean hasCommentNotVisibleForPreview;

        // For student view only
        private final List<ResponseOutput> responsesToSelf = new ArrayList<>();
        private final List<ResponseOutput> responsesFromSelf = new ArrayList<>();
        private final List<List<ResponseOutput>> otherResponses = new ArrayList<>();

        private QuestionOutput(FeedbackQuestion feedbackQuestion, String questionStatistics,
                boolean hasResponseButNotVisibleForPreview, boolean hasCommentNotVisibleForPreview) {
            this.feedbackQuestion = new FeedbackQuestionData(feedbackQuestion);
            this.questionStatistics = questionStatistics;
            this.hasResponseButNotVisibleForPreview = hasResponseButNotVisibleForPreview;
            this.hasCommentNotVisibleForPreview = hasCommentNotVisibleForPreview;
        }

        public FeedbackQuestionData getFeedbackQuestion() {
            return feedbackQuestion;
        }

        public String getQuestionStatistics() {
            return questionStatistics;
        }

        public List<ResponseOutput> getAllResponses() {
            return allResponses;
        }

        public boolean getHasResponseButNotVisibleForPreview() {
            return hasResponseButNotVisibleForPreview;
        }

        public boolean getHasCommentNotVisibleForPreview() {
            return hasCommentNotVisibleForPreview;
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

    /**
     * API output format for question responses.
     */
    public static final class ResponseOutput {

        private boolean isMissingResponse;

        private String responseId;

        private String giver;
        /**
         * Depending on the question giver type, {@code giverIdentifier} may contain the giver's email, any team member's
         * email or null.
         */
        @Nullable
        private String relatedGiverEmail;
        private String giverTeam;
        @Nullable
        private String giverEmail;
        private String giverSection;
        private String recipient;
        private String recipientTeam;
        @Nullable
        private String recipientEmail;
        private String recipientSection;
        private FeedbackResponseDetails responseDetails;

        // comments
        @Nullable
        private CommentOutput participantComment;
        private List<CommentOutput> instructorComments;

        private ResponseOutput() {
            // use builder instead
        }

        /**
         * Returns a builder for {@link ResponseOutput}.
         */
        static Builder builder() {
            return new Builder();
        }

        public boolean isMissingResponse() {
            return isMissingResponse;
        }

        public String getResponseId() {
            return responseId;
        }

        public String getGiver() {
            return giver;
        }

        @Nullable
        public String getGiverEmail() {
            return giverEmail;
        }

        @Nullable
        public String getRelatedGiverEmail() {
            return relatedGiverEmail;
        }

        public String getGiverTeam() {
            return giverTeam;
        }

        public String getGiverSection() {
            return giverSection;
        }

        public String getRecipient() {
            return recipient;
        }

        public String getRecipientTeam() {
            return recipientTeam;
        }

        @Nullable
        public String getRecipientEmail() {
            return recipientEmail;
        }

        public String getRecipientSection() {
            return recipientSection;
        }

        public FeedbackResponseDetails getResponseDetails() {
            return responseDetails;
        }

        @Nullable
        public CommentOutput getParticipantComment() {
            return participantComment;
        }

        public List<CommentOutput> getInstructorComments() {
            return instructorComments;
        }

        /**
         * Builder class for {@link ResponseOutput}.
         */
        public static final class Builder {
            private final ResponseOutput responseOutput;

            private Builder() {
                responseOutput = new ResponseOutput();
            }

            Builder withIsMissingResponse(boolean isMissingResponse) {
                responseOutput.isMissingResponse = isMissingResponse;
                return this;
            }

            Builder withResponseId(String responseId) {
                responseOutput.responseId = responseId;
                return this;
            }

            Builder withGiver(String giverName) {
                responseOutput.giver = giverName;
                return this;
            }

            Builder withRelatedGiverEmail(@Nullable String relatedGiverEmail) {
                responseOutput.relatedGiverEmail = relatedGiverEmail;
                return this;
            }

            Builder withGiverTeam(String giverTeam) {
                responseOutput.giverTeam = giverTeam;
                return this;
            }

            Builder withGiverEmail(@Nullable String giverEmail) {
                responseOutput.giverEmail = giverEmail;
                return this;
            }

            Builder withGiverSectionName(String giverSection) {
                responseOutput.giverSection = giverSection;
                return this;
            }

            Builder withRecipient(String recipientName) {
                responseOutput.recipient = recipientName;
                return this;
            }

            Builder withRecipientTeam(String recipientTeam) {
                responseOutput.recipientTeam = recipientTeam;
                return this;
            }

            Builder withRecipientEmail(@Nullable String recipientEmail) {
                responseOutput.recipientEmail = recipientEmail;
                return this;
            }

            Builder withRecipientSectionName(String recipientSection) {
                responseOutput.recipientSection = recipientSection;
                return this;
            }

            Builder withResponseDetails(FeedbackResponseDetails responseDetails) {
                responseOutput.responseDetails = responseDetails;
                return this;
            }

            Builder withParticipantComment(@Nullable CommentOutput participantComment) {
                responseOutput.participantComment = participantComment;
                return this;
            }

            Builder withInstructorComments(List<CommentOutput> instructorComments) {
                responseOutput.instructorComments = instructorComments;
                return this;
            }

            ResponseOutput build() {
                return responseOutput;
            }
        }
    }

    /**
     * API output format for response comments.
     */
    public static final class CommentOutput extends FeedbackResponseCommentData {

        @Nullable
        private String commentGiverName;
        @Nullable
        private String lastEditorName;

        private CommentOutput(FeedbackResponseComment frc) {
            // use builder instead
            super(frc);
        }

        /**
         * Returns a builder for {@link CommentOutput}.
         */
        static Builder builder(FeedbackResponseComment frc) {
            return new Builder(frc);
        }

        @Nullable
        public String getCommentGiverName() {
            return commentGiverName;
        }

        @Nullable
        public String getLastEditorName() {
            return lastEditorName;
        }

        /**
         * Builder class for {@link CommentOutput}.
         */
        public static final class Builder {
            private final CommentOutput commentOutput;

            private Builder(FeedbackResponseComment frc) {
                commentOutput = new CommentOutput(frc);
            }

            Builder withCommentGiver(@Nullable String commentGiver) {
                commentOutput.commentGiver = commentGiver;
                return this;
            }

            Builder withCommentGiverName(@Nullable String commentGiverName) {
                commentOutput.commentGiverName = commentGiverName;
                return this;
            }

            Builder withLastEditorEmail(@Nullable String lastEditorEmail) {
                commentOutput.lastEditorEmail = lastEditorEmail;
                return this;
            }

            Builder withLastEditorName(@Nullable String lastEditorName) {
                commentOutput.lastEditorName = lastEditorName;
                return this;
            }

            CommentOutput build() {
                return commentOutput;
            }
        }
    }

}

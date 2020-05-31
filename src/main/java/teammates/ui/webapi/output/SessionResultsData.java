package teammates.ui.webapi.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Nullable;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.Const;

/**
 * API output format for session results, including statistics.
 */
public class SessionResultsData extends ApiOutput {

    private static final String REGEX_ANONYMOUS_PARTICIPANT_HASH = "[0-9]{1,10}";

    private final List<QuestionOutput> questions = new ArrayList<>();

    private SessionResultsData() {
        // use factory method instead
    }

    /**
     * Factory method to construct API output for instructor.
     */
    public static SessionResultsData initForInstructor(SessionResultsBundle bundle) {
        SessionResultsData sessionResultsData = new SessionResultsData();

        Map<String, List<FeedbackResponseAttributes>> questionsWithResponses =
                bundle.getQuestionResponseMap();

        questionsWithResponses.forEach((questionId, responses) -> {
            FeedbackQuestionAttributes question = bundle.getQuestionsMap().get(questionId);
            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
            QuestionOutput qnOutput = new QuestionOutput(question,
                    questionDetails.getQuestionResultStatisticsJson(question, null, bundle));
            List<ResponseOutput> allResponses = buildResponsesForInstructor(responses, bundle);
            qnOutput.allResponses.addAll(allResponses);

            sessionResultsData.questions.add(qnOutput);
        });

        return sessionResultsData;
    }

    /**
     * Factory method to construct API output for student.
     */
    public static SessionResultsData initForStudent(SessionResultsBundle bundle, StudentAttributes student) {
        SessionResultsData sessionResultsData = new SessionResultsData();

        Map<String, List<FeedbackResponseAttributes>> questionsWithResponses =
                bundle.getQuestionResponseMap();

        questionsWithResponses.forEach((questionId, responses) -> {
            FeedbackQuestionAttributes question = bundle.getQuestionsMap().get(questionId);
            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
            QuestionOutput qnOutput = new QuestionOutput(question,
                    questionDetails.getQuestionResultStatisticsJson(question, student.getEmail(), bundle));
            Map<String, List<ResponseOutput>> otherResponsesMap = new HashMap<>();

            if (questionDetails.isIndividualResponsesShownToStudents()) {
                for (FeedbackResponseAttributes response : responses) {
                    boolean isUserGiver = student.getEmail().equals(response.getGiver());
                    boolean isUserRecipient = student.getEmail().equals(response.getRecipient());
                    if (isUserGiver) {
                        qnOutput.responsesFromSelf.add(buildSingleResponseForStudent(response, bundle, student));
                    } else if (isUserRecipient) {
                        qnOutput.responsesToSelf.add(buildSingleResponseForStudent(response, bundle, student));
                    } else {
                        // we don't need care about the keys of the map here
                        // as only the values of the map will be used
                        otherResponsesMap.computeIfAbsent(response.getRecipient(), k -> new ArrayList<>())
                                .add(buildSingleResponseForStudent(response, bundle, student));
                    }
                }
            }
            qnOutput.otherResponses.addAll(otherResponsesMap.values());

            sessionResultsData.questions.add(qnOutput);
        });

        return sessionResultsData;
    }

    private static ResponseOutput buildSingleResponseForStudent(
            FeedbackResponseAttributes response, SessionResultsBundle bundle, StudentAttributes student) {
        FeedbackQuestionAttributes question = bundle.getQuestionsMap().get(response.getFeedbackQuestionId());

        // process giver
        boolean isUserGiver = student.getEmail().equals(response.getGiver());
        boolean isUserTeamGiver = question.giverType == FeedbackParticipantType.TEAMS
                && student.getTeam().equals(response.getGiver());
        String giverName = "";
        if (isUserTeamGiver) {
            giverName = String.format("Your Team (%s)", response.getGiver());
        } else if (isUserGiver) {
            giverName = "You";
        } else {
            // we don't want student to figure out who is who by using the hash
            giverName = removeAnonymousHash(getGiverNameOfResponse(response, bundle));
        }

        // process recipient
        boolean isUserRecipient = student.getEmail().equals(response.getRecipient());
        boolean isUserTeamRecipient = question.getRecipientType() == FeedbackParticipantType.TEAMS
                && student.getTeam().equals(response.getRecipient());
        String recipientName = "";
        if (isUserRecipient) {
            recipientName = "You";
        } else if (isUserTeamRecipient) {
            recipientName = String.format("Your Team (%s)", response.getRecipient());
        } else {
            // we don't want student to figure out who is who by using the hash
            recipientName = removeAnonymousHash(getRecipientNameOfResponse(response, bundle));
        }

        // process comments
        List<FeedbackResponseCommentAttributes> feedbackResponseComments =
                bundle.getResponseCommentsMap().getOrDefault(response.getId(), Collections.emptyList());
        Queue<CommentOutput> comments = buildComments(feedbackResponseComments, bundle);

        // Student does not need to know the teams for giver and/or recipient
        return ResponseOutput.builder()
                .withResponseId(response.getId())
                .withGiver(giverName)
                .withGiverTeam(null)
                .withGiverEmail(null)
                .withRelatedGiverEmail(null)
                .withGiverSection(response.getGiverSection())
                .withRecipient(recipientName)
                .withRecipientTeam(null)
                .withRecipientEmail(null)
                .withRecipientSection(response.getRecipientSection())
                .withResponseDetails(response.getResponseDetails())
                .withParticipantComment(comments.poll())
                .withInstructorComments(new ArrayList<>(comments))
                .build();
    }

    private static String removeAnonymousHash(String identifier) {
        return identifier.replaceAll(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " (student|instructor|team) "
                + REGEX_ANONYMOUS_PARTICIPANT_HASH, Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " $1");
    }

    private static List<ResponseOutput> buildResponsesForInstructor(
            List<FeedbackResponseAttributes> responses, SessionResultsBundle bundle) {
        List<ResponseOutput> output = new ArrayList<>();

        for (FeedbackResponseAttributes response : responses) {
            output.add(buildSingleResponseForInstructor(response, bundle));
        }

        return output;
    }

    private static ResponseOutput buildSingleResponseForInstructor(
            FeedbackResponseAttributes response, SessionResultsBundle bundle) {
        // process giver
        String giverEmail = null;
        String relatedGiverEmail = null;
        if (bundle.isResponseGiverVisible(response)) {
            giverEmail = response.getGiver();
            relatedGiverEmail = response.getGiver();

            if (bundle.getRoster().isTeamInCourse(giverEmail)) {
                // remove recipient email as it is a team name
                giverEmail = null;
                relatedGiverEmail =
                        bundle.getRoster().getTeamToMembersTable().get(giverEmail).iterator().next().getEmail();
            }
        }
        String giverName = getGiverNameOfResponse(response, bundle);
        String giverTeam = bundle.getRoster().getInfoForIdentifier(response.getGiver()).getTeamName();

        // process recipient
        String recipientEmail = null;
        String recipientName = getRecipientNameOfResponse(response, bundle);
        String recipientTeam =
                bundle.getRoster().getInfoForIdentifier(response.getRecipient()).getTeamName();
        if (bundle.isResponseRecipientVisible(response)) {
            recipientEmail = response.getRecipient();

            if (bundle.getRoster().isTeamInCourse(recipientEmail)) {
                // remove recipient email as it is a team name
                recipientEmail = null;
            } else if (Const.GENERAL_QUESTION.equals(recipientEmail)) {
                // general recipient does not have email
                recipientEmail = null;
            }
        }

        // process comments
        List<FeedbackResponseCommentAttributes> feedbackResponseComments =
                bundle.getResponseCommentsMap().getOrDefault(response.getId(), Collections.emptyList());
        Queue<CommentOutput> comments = buildComments(feedbackResponseComments, bundle);

        return ResponseOutput.builder()
                .withResponseId(response.getId())
                .withGiver(giverName)
                .withGiverTeam(giverTeam)
                .withGiverEmail(giverEmail)
                .withRelatedGiverEmail(relatedGiverEmail)
                .withGiverSection(response.getGiverSection())
                .withRecipient(recipientName)
                .withRecipientTeam(recipientTeam)
                .withRecipientEmail(recipientEmail)
                .withRecipientSection(response.getRecipientSection())
                .withResponseDetails(response.getResponseDetails())
                .withParticipantComment(comments.poll())
                .withInstructorComments(new ArrayList<>(comments))
                .build();
    }

    /**
     * Gets giver name of a response from the bundle.
     *
     * <p>Anonymized the name if necessary.
     */
    private static String getGiverNameOfResponse(FeedbackResponseAttributes response, SessionResultsBundle bundle) {
        FeedbackQuestionAttributes question = bundle.getQuestionsMap().get(response.getFeedbackQuestionId());
        FeedbackParticipantType participantType = question.giverType;

        CourseRoster.ParticipantInfo userInfo = bundle.getRoster().getInfoForIdentifier(response.getGiver());
        String name = userInfo.getName();
        if (question.getGiverType() == FeedbackParticipantType.TEAMS
                && bundle.getRoster().isStudentInCourse(response.getGiver())) {
            // user gives responses on behalf of the team (legacy implementation), the name should be the team name instead
            name = userInfo.getTeamName();
        }
        if (!bundle.isResponseGiverVisible(response)) {
            name = SessionResultsBundle.getAnonName(participantType, name);
        }

        return name;
    }

    /**
     * Gets recipient name of a response from the bundle.
     *
     * <p>Anonymized the name if necessary.
     */
    private static String getRecipientNameOfResponse(FeedbackResponseAttributes response, SessionResultsBundle bundle) {
        FeedbackQuestionAttributes question = bundle.getQuestionsMap().get(response.getFeedbackQuestionId());
        FeedbackParticipantType participantType = question.getRecipientType();
        if (participantType == FeedbackParticipantType.SELF) {
            // recipient type for self-feedback is the same as the giver type
            participantType = question.getGiverType();
        }

        String name = bundle.getRoster().getInfoForIdentifier(response.getRecipient()).getName();
        if (response.getRecipient().equals(Const.GENERAL_QUESTION)) {
            // for general question
            name = Const.USER_NOBODY_TEXT;
        }
        if (!bundle.isResponseRecipientVisible(response)) {
            name = SessionResultsBundle.getAnonName(participantType, name);
        }

        return name;
    }

    private static Queue<CommentOutput> buildComments(List<FeedbackResponseCommentAttributes> feedbackResponseComments,
                                                      SessionResultsBundle bundle) {
        LinkedList<CommentOutput> outputs = new LinkedList<>();

        CommentOutput participantComment = null;
        for (FeedbackResponseCommentAttributes comment : feedbackResponseComments) {
            if (comment.isCommentFromFeedbackParticipant()) {
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
                    giverEmail = comment.getCommentGiver();
                    giverName = bundle.getRoster().getInfoForIdentifier(comment.getCommentGiver()).getName();
                    lastEditorEmail = comment.getLastEditorEmail();
                    lastEditorName = bundle.getRoster().getInfoForIdentifier(comment.getLastEditorEmail()).getName();
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
    public static class QuestionOutput {

        private final FeedbackQuestionData feedbackQuestion;
        private final String questionStatistics;

        // For instructor view
        private final List<ResponseOutput> allResponses = new ArrayList<>();

        // For student view
        private final List<ResponseOutput> responsesToSelf = new ArrayList<>();
        private final List<ResponseOutput> responsesFromSelf = new ArrayList<>();
        private final List<List<ResponseOutput>> otherResponses = new ArrayList<>();

        QuestionOutput(FeedbackQuestionAttributes feedbackQuestionAttributes, String questionStatistics) {
            this.feedbackQuestion = new FeedbackQuestionData(feedbackQuestionAttributes);
            this.questionStatistics = questionStatistics;
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
    public static class ResponseOutput {

        // TODO: security risk: responseId can expose giver and recipient email
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
        public static Builder builder() {
            return new Builder();
        }

        public String getResponseId() {
            return responseId;
        }

        public String getGiver() {
            return giver;
        }

        public String getGiverEmail() {
            return giverEmail;
        }

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

            //CHECKSTYLE.OFF:MissingJavadocMethod
            public Builder withResponseId(String responseId) {
                responseOutput.responseId = responseId;
                return this;
            }

            public Builder withGiver(String giverName) {
                responseOutput.giver = giverName;
                return this;
            }

            public Builder withRelatedGiverEmail(@Nullable String relatedGiverEmail) {
                responseOutput.relatedGiverEmail = relatedGiverEmail;
                return this;
            }

            public Builder withGiverTeam(String giverTeam) {
                responseOutput.giverTeam = giverTeam;
                return this;
            }

            public Builder withGiverEmail(@Nullable String giverEmail) {
                responseOutput.giverEmail = giverEmail;
                return this;
            }

            public Builder withGiverSection(String giverSection) {
                responseOutput.giverSection = giverSection;
                return this;
            }

            public Builder withRecipient(String recipientName) {
                responseOutput.recipient = recipientName;
                return this;
            }

            public Builder withRecipientTeam(String recipientTeam) {
                responseOutput.recipientTeam = recipientTeam;
                return this;
            }

            public Builder withRecipientEmail(@Nullable String recipientEmail) {
                responseOutput.recipientEmail = recipientEmail;
                return this;
            }

            public Builder withRecipientSection(String recipientSection) {
                responseOutput.recipientSection = recipientSection;
                return this;
            }

            public Builder withResponseDetails(FeedbackResponseDetails responseDetails) {
                responseOutput.responseDetails = responseDetails;
                return this;
            }

            public Builder withParticipantComment(@Nullable CommentOutput participantComment) {
                responseOutput.participantComment = participantComment;
                return this;
            }

            public Builder withInstructorComments(List<CommentOutput> instructorComments) {
                responseOutput.instructorComments = instructorComments;
                return this;
            }

            public ResponseOutput build() {
                return responseOutput;
            }
            //CHECKSTYLE.ON:MissingJavadocMethod
        }
    }

    /**
     * API output format for response comments.
     */
    public static class CommentOutput extends FeedbackResponseCommentData {

        @Nullable
        private String commentGiverName;
        @Nullable
        private String lastEditorName;

        private CommentOutput(FeedbackResponseCommentAttributes frc) {
            // use builder instead
            super(frc);
        }

        /**
         * Returns a builder for {@link CommentOutput}.
         */
        public static Builder builder(FeedbackResponseCommentAttributes frc) {
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

            private Builder(FeedbackResponseCommentAttributes frc) {
                commentOutput = new CommentOutput(frc);
            }

            //CHECKSTYLE.OFF:MissingJavadocMethod
            public Builder withCommentGiver(@Nullable String commentGiver) {
                commentOutput.commentGiver = commentGiver;
                return this;
            }

            public Builder withCommentGiverName(@Nullable String commentGiverName) {
                commentOutput.commentGiverName = commentGiverName;
                return this;
            }

            public Builder withLastEditorEmail(@Nullable String lastEditorEmail) {
                commentOutput.lastEditorEmail = lastEditorEmail;
                return this;
            }

            public Builder withLastEditorName(@Nullable String lastEditorName) {
                commentOutput.lastEditorName = lastEditorName;
                return this;
            }

            public CommentOutput build() {
                return commentOutput;
            }
            //CHECKSTYLE.ON:MissingJavadocMethod
        }
    }

}

package teammates.ui.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackMissingResponse;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;

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
     * Factory method to construct API output for instructor.
     */
    public static SessionResultsData initForInstructor(SessionResultsBundle bundle) {
        SessionResultsData sessionResultsData = new SessionResultsData();

        Map<FeedbackQuestion, List<FeedbackResponse>> questionsWithResponses =
                bundle.getQuestionResponseMap();

        questionsWithResponses.forEach((question, responses) -> {
            FeedbackQuestionDetails questionDetails = question.getQuestionDetailsCopy();
            QuestionOutput qnOutput = new QuestionOutput(question,
                    questionDetails.getQuestionResultStatisticsJson(question, null, bundle), false, false);
            // put normal responses
            List<ResponseOutput> allResponses = buildResponsesForInstructor(responses, bundle);
            qnOutput.allResponses.addAll(allResponses);

            // put missing responses
            List<FeedbackMissingResponse> missingResponses = bundle.getQuestionMissingResponseMap().get(question);
            qnOutput.allResponses.addAll(buildMissingResponsesForInstructor(missingResponses, bundle));

            sessionResultsData.questions.add(qnOutput);
        });

        return sessionResultsData;
    }

    /**
     * Factory method to construct API output for student.
     */
    public static SessionResultsData initForStudent(SessionResultsBundle bundle, Student student) {
        SessionResultsData sessionResultsData = new SessionResultsData();

        Map<FeedbackQuestion, List<FeedbackResponse>> questionsWithResponses =
                bundle.getQuestionResponseMap();

        questionsWithResponses.forEach((question, responses) -> {
            FeedbackQuestionDetails questionDetails = question.getQuestionDetailsCopy();
            // check if question has comments (on any responses) not visible for preview
            boolean hasCommentNotVisibleForPreview = bundle.getQuestionsWithCommentNotVisibleForPreviewSet()
                    .contains(question);
            QuestionOutput qnOutput = new QuestionOutput(question,
                    questionDetails.getQuestionResultStatisticsJson(question, student.getEmail(), bundle),
                    false, hasCommentNotVisibleForPreview);
            Map<String, List<ResponseOutput>> otherResponsesMap = new HashMap<>();

            qnOutput.getFeedbackQuestion().hideInformationForStudent();

            if (questionDetails.isIndividualResponsesShownToStudents()) {
                for (FeedbackResponse response : responses) {
                    boolean isUserInstructor = Const.USER_TEAM_FOR_INSTRUCTOR.equals(student.getTeamName());

                    boolean isUserGiver = SanitizationHelper.areEmailsEqual(student.getEmail(), response.getGiver())
                            && (isUserInstructor && question.getGiverType() == QuestionGiverType.INSTRUCTORS
                            || !isUserInstructor && question.getGiverType() != QuestionGiverType.INSTRUCTORS);
                    boolean isUserRecipient = SanitizationHelper.areEmailsEqual(student.getEmail(), response.getRecipient())
                            && (isUserInstructor && question.getRecipientType() == QuestionRecipientType.INSTRUCTORS
                            || !isUserInstructor && question.getRecipientType() != QuestionRecipientType.INSTRUCTORS);
                    ResponseOutput responseOutput = buildSingleResponseForStudent(response, bundle, student);

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

    private static ResponseOutput buildSingleResponseForStudent(
            FeedbackResponse response, SessionResultsBundle bundle, Student student) {
        FeedbackQuestion question = response.getFeedbackQuestion();
        boolean isUserInstructor = Const.USER_TEAM_FOR_INSTRUCTOR.equals(student.getTeamName());

        // process giver
        boolean isUserGiver = SanitizationHelper.areEmailsEqual(student.getEmail(), response.getGiver())
                && (isUserInstructor && question.getGiverType() == QuestionGiverType.INSTRUCTORS
                || !isUserInstructor && question.getGiverType() != QuestionGiverType.INSTRUCTORS);
        boolean isUserTeamGiver = question.getGiverType() == QuestionGiverType.TEAMS
                && student.getTeamName().equals(response.getGiver());
        String giverName;
        String giverTeam = "";
        if (isUserTeamGiver) {
            giverName = String.format("Your Team (%s)", response.getGiver());
            giverTeam = response.getGiver();
        } else if (isUserGiver) {
            giverName = "You";
            giverTeam = student.getTeamName();
        } else {
            // we don't want student to figure out who is who by using the hash
            giverName = removeAnonymousHash(getGiverNameOfResponse(response.getId(), response.getGiver(), question, bundle));
        }

        // process recipient
        boolean isUserRecipient = SanitizationHelper.areEmailsEqual(student.getEmail(), response.getRecipient())
                && (isUserInstructor && question.getRecipientType() == QuestionRecipientType.INSTRUCTORS
                || !isUserInstructor && question.getRecipientType() != QuestionRecipientType.INSTRUCTORS);
        boolean isUserTeamRecipient = (question.getRecipientType() == QuestionRecipientType.TEAMS
                || question.getRecipientType() == QuestionRecipientType.TEAMS_IN_SAME_SECTION)
                && student.getTeamName().equals(response.getRecipient());
        String recipientName;
        String recipientTeam = "";
        if (isUserRecipient) {
            recipientName = "You";
            recipientTeam = student.getTeamName();
        } else if (isUserTeamRecipient) {
            recipientName = String.format("Your Team (%s)", response.getRecipient());
            recipientTeam = response.getRecipient();
        } else {
            // we don't want student to figure out who is who by using the hash
            recipientName = removeAnonymousHash(
                getRecipientNameOfResponse(response.getId(), response.getRecipient(), question, bundle));
            if (!recipientName.contains(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT)) {
                recipientTeam = bundle.getRoster().getInfoForIdentifier(response.getRecipient()).getTeamName();
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
                .withGiverSection(response.getGiverSection())
                .withRecipient(recipientName)
                .withRecipientTeam(recipientTeam)
                .withRecipientEmail(null)
                .withRecipientSection(response.getRecipientSection())
                .withResponseDetails(response.getFeedbackResponseDetailsCopy())
                .withParticipantComment(comments.poll())
                .withInstructorComments(new ArrayList<>(comments))
                .build();
    }

    private static String removeAnonymousHash(String identifier) {
        return identifier.replaceAll(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " (student|instructor|team) "
                + REGEX_ANONYMOUS_PARTICIPANT_HASH, Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " $1");
    }

    private static List<ResponseOutput> buildResponsesForInstructor(
            List<FeedbackResponse> responses, SessionResultsBundle bundle) {
        List<ResponseOutput> output = new ArrayList<>();

        for (FeedbackResponse response : responses) {
            output.add(buildSingleResponseForInstructor(response, bundle));
        }

        return output;
    }

    private static ResponseOutput buildSingleResponseForInstructor(
            FeedbackResponse response, SessionResultsBundle bundle) {
        FeedbackQuestion question = response.getFeedbackQuestion();
        // process giver
        String giverEmail = null;
        String relatedGiverEmail = null;
        if (bundle.isResponseGiverVisible(response.getId())) {
            giverEmail = response.getGiver();
            relatedGiverEmail = response.getGiver();

            if (bundle.getRoster().isTeamInCourse(giverEmail)) {
                // remove recipient email as it is a team name
                relatedGiverEmail =
                        bundle.getRoster().getTeamToMembersTable().get(giverEmail).iterator().next().getEmail();
                giverEmail = null;
            }
        }
        String giverName = getGiverNameOfResponse(response.getId(), response.getGiver(), question, bundle);
        String giverTeam = bundle.getRoster().getInfoForIdentifier(response.getGiver()).getTeamName();
        String giverSectionName = response.getGiverSectionName();
        if (question.getGiverType() == QuestionGiverType.INSTRUCTORS) {
            Instructor instructor = bundle.getRoster().getInstructorForEmail(response.getGiver());
            giverName = instructor.getName();
            giverTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
            giverSectionName = Const.DEFAULT_SECTION;
        }

        // process recipient
        String recipientEmail = null;
        String recipientName = getRecipientNameOfResponse(response.getId(), response.getRecipient(), question, bundle);
        String recipientTeam =
                bundle.getRoster().getInfoForIdentifier(response.getRecipient()).getTeamName();
        String recipientSectionName = response.getRecipientSectionName();
        if (question.getRecipientType() == QuestionRecipientType.INSTRUCTORS) {
            Instructor instructor = bundle.getRoster().getInstructorForEmail(response.getRecipient());
            recipientName = instructor.getName();
            recipientTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
            recipientSectionName = Const.DEFAULT_SECTION;
        }
        if (bundle.isResponseRecipientVisible(response.getId(), question.getRecipientType())) {
            recipientEmail = response.getRecipient();

            boolean shouldRemoveRecipientEmail = bundle.getRoster().isTeamInCourse(recipientEmail)
                    || Const.GENERAL_QUESTION.equals(recipientEmail);
            if (shouldRemoveRecipientEmail) {
                recipientEmail = null;
            }
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

    private static List<ResponseOutput> buildMissingResponsesForInstructor(
            List<FeedbackMissingResponse> responses, SessionResultsBundle bundle) {
        List<ResponseOutput> output = new ArrayList<>();

        for (FeedbackMissingResponse response : responses) {
            output.add(buildSingleMissingResponseForInstructor(response, bundle));
        }

        return output;
    }

    private static ResponseOutput buildSingleMissingResponseForInstructor(
            FeedbackMissingResponse response, SessionResultsBundle bundle) {
        // process giver
        String giverEmail = null;
        String relatedGiverEmail = null;
        FeedbackQuestion question = response.feedbackQuestion();
        if (bundle.isResponseGiverVisible(response.id())) {
            giverEmail = response.giver();
            relatedGiverEmail = response.giver();

            if (bundle.getRoster().isTeamInCourse(giverEmail)) {
                // remove recipient email as it is a team name
                relatedGiverEmail =
                        bundle.getRoster().getTeamToMembersTable().get(giverEmail).iterator().next().getEmail();
                giverEmail = null;
            }
        }
        String giverName = getGiverNameOfResponse(response.id(), response.giver(), question, bundle);
        String giverTeam = bundle.getRoster().getInfoForIdentifier(response.giver()).getTeamName();
        String giverSectionName = response.giverSectionName();
        if (question.getGiverType() == QuestionGiverType.INSTRUCTORS) {
            Instructor instructor = bundle.getRoster().getInstructorForEmail(response.giver());
            giverName = instructor.getName();
            giverTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
            giverSectionName = Const.DEFAULT_SECTION;
        }

        // process recipient
        String recipientEmail = null;
        String recipientName = getRecipientNameOfResponse(response.id(), response.recipient(), question, bundle);
        String recipientTeam =
                bundle.getRoster().getInfoForIdentifier(response.recipient()).getTeamName();
        String recipientSectionName = response.recipientSectionName();
        if (question.getRecipientType() == QuestionRecipientType.INSTRUCTORS) {
            Instructor instructor = bundle.getRoster().getInstructorForEmail(response.recipient());
            recipientName = instructor.getName();
            recipientTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
            recipientSectionName = Const.DEFAULT_SECTION;
        }
        if (bundle.isResponseRecipientVisible(response.id(), question.getRecipientType())) {
            recipientEmail = response.recipient();

            boolean shouldRemoveRecipientEmail = bundle.getRoster().isTeamInCourse(recipientEmail)
                    || Const.GENERAL_QUESTION.equals(recipientEmail);
            if (shouldRemoveRecipientEmail) {
                recipientEmail = null;
            }
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
    private static String getGiverNameOfResponse(UUID responseId, String responseGiver,
            FeedbackQuestion question, SessionResultsBundle bundle) {
        QuestionGiverType giverType = question.getGiverType();

        CourseRoster.ParticipantInfo userInfo = bundle.getRoster().getInfoForIdentifier(responseGiver);
        String name = userInfo.getName();

        if (!bundle.isResponseGiverVisible(responseId)) {
            name = SessionResultsBundle.getAnonGiverName(giverType, name);
        }

        return name;
    }

    /**
     * Gets recipient name of a response from the bundle.
     *
     * <p>Anonymized the name if necessary.
     */
    private static String getRecipientNameOfResponse(UUID responseId, String responseRecipient,
            FeedbackQuestion question, SessionResultsBundle bundle) {
        QuestionRecipientType recipientType = question.getRecipientType();
        if (recipientType == QuestionRecipientType.SELF) {
            // recipient type for self-feedback is the same as the giver type
            recipientType = switch (question.getGiverType()) {
            case TEAMS -> QuestionRecipientType.TEAMS;
            case TEAMS_IN_SAME_SECTION -> QuestionRecipientType.TEAMS_IN_SAME_SECTION;
            case INSTRUCTORS -> QuestionRecipientType.INSTRUCTORS;
            case SELF -> QuestionRecipientType.SELF;
            case STUDENTS -> QuestionRecipientType.STUDENTS;
            case STUDENTS_IN_SAME_SECTION -> QuestionRecipientType.STUDENTS_IN_SAME_SECTION;
            };
        }

        CourseRoster.ParticipantInfo userInfo = bundle.getRoster().getInfoForIdentifier(responseRecipient);
        String name = userInfo.getName();
        if (Const.GENERAL_QUESTION.equals(responseRecipient)) {
            // for general question
            name = Const.USER_NOBODY_TEXT;
        }
        if (!bundle.isResponseRecipientVisible(responseId, recipientType)) {
            name = SessionResultsBundle.getAnonRecipientName(recipientType, name);
        }

        return name;
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
                    giverEmail = comment.getGiver();
                    giverName = bundle.getRoster().getInfoForIdentifier(comment.getGiver()).getName();
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

            Builder withGiverSection(Section giverSection) {
                responseOutput.giverSection = giverSection.getName();
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

            Builder withRecipientSection(Section recipientSection) {
                responseOutput.recipientSection = recipientSection.getName();
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

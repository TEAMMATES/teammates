package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;

/**
 * Represents detailed results for a feedback session.
 */
public class SessionResultsBundle {

    private final List<FeedbackQuestion> questions;
    private final Set<FeedbackQuestion> questionsNotVisibleForPreviewSet;
    private final Set<FeedbackQuestion> questionsWithCommentNotVisibleForPreviewSet;
    private final Map<FeedbackQuestion, List<FeedbackResponse>> questionResponseMap;
    private final Map<FeedbackQuestion, List<FeedbackMissingResponse>> questionMissingResponseMap;
    private final Map<FeedbackResponse, List<FeedbackResponseComment>> responseCommentsMap;
    private final Map<UUID, Boolean> responseGiverVisibilityTable;
    private final Map<UUID, Boolean> responseRecipientVisibilityTable;
    private final Map<UUID, Boolean> commentGiverVisibilityTable;
    private final CourseRoster roster;

    public SessionResultsBundle(List<FeedbackQuestion> questions,
                                Set<FeedbackQuestion> questionsNotVisibleForPreviewSet,
                                Set<FeedbackQuestion> questionsWithCommentNotVisibleForPreviewSet,
                                List<FeedbackResponse> responses,
                                List<FeedbackMissingResponse> missingResponses,
                                Map<UUID, Boolean> responseGiverVisibilityTable,
                                Map<UUID, Boolean> responseRecipientVisibilityTable,
                                Map<FeedbackResponse, List<FeedbackResponseComment>> responseCommentsMap,
                                Map<UUID, Boolean> commentGiverVisibilityTable,
                                CourseRoster roster) {

        this.questions = questions;
        this.questionsNotVisibleForPreviewSet = questionsNotVisibleForPreviewSet;
        this.questionsWithCommentNotVisibleForPreviewSet = questionsWithCommentNotVisibleForPreviewSet;
        this.responseCommentsMap = responseCommentsMap;
        this.responseGiverVisibilityTable = responseGiverVisibilityTable;
        this.responseRecipientVisibilityTable = responseRecipientVisibilityTable;
        this.commentGiverVisibilityTable = commentGiverVisibilityTable;
        this.roster = roster;
        this.questionResponseMap = buildQuestionToResponseMap(responses, FeedbackResponse::getFeedbackQuestion);
        this.questionMissingResponseMap =
            buildQuestionToResponseMap(missingResponses, FeedbackMissingResponse::feedbackQuestion);
    }

    private <T> Map<FeedbackQuestion, List<T>> buildQuestionToResponseMap(
            List<T> responses,
            Function<T, FeedbackQuestion> questionExtractor
    ) {
        Map<FeedbackQuestion, List<T>> questionToResponseMap = new LinkedHashMap<>();
        for (FeedbackQuestion question : questions) {
            questionToResponseMap.put(question, new ArrayList<>());
        }
        for (T response : responses) {
            FeedbackQuestion question = questionExtractor.apply(response);
            List<T> responsesForQuestion = questionToResponseMap.get(question);
            responsesForQuestion.add(response);
        }
        return questionToResponseMap;
    }

    /**
     * Returns true if the giver of a response is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isResponseGiverVisible(UUID responseId, FeedbackParticipantType giverParticipantType) {
        return giverParticipantType == FeedbackParticipantType.NONE
                || responseGiverVisibilityTable.getOrDefault(responseId, false);
    }

    /**
     * Returns true if the recipient of a response is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isResponseRecipientVisible(UUID responseId, FeedbackParticipantType recipientParticipantType) {
        return recipientParticipantType == FeedbackParticipantType.NONE
                || responseRecipientVisibilityTable.getOrDefault(responseId, false);
    }

    /**
     * Returns true if the giver of a comment is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isCommentGiverVisible(FeedbackResponseComment comment) {
        return commentGiverVisibilityTable.get(comment.getId());
    }

    /**
     * Gets the anonymous name for a given name.
     *
     * <p>The anonymous name will be deterministic based on {@code name}.
     */
    public static String getAnonName(FeedbackParticipantType type, String name) {
        String hashedSignedName = getHashOfName(StringHelper.generateSha256Hmac("anon-name:" + name));
        String participantType = type.toSingularFormString();
        return String.format(
            "%s %s %s", Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT, participantType, hashedSignedName);
    }

    public Map<FeedbackQuestion, List<FeedbackResponse>> getQuestionResponseMap() {
        return questionResponseMap;
    }

    public Map<FeedbackQuestion, List<FeedbackMissingResponse>> getQuestionMissingResponseMap() {
        return questionMissingResponseMap;
    }

    private static String getHashOfName(String name) {
        return Long.toString(Math.abs((long) name.hashCode()));
    }

    public List<FeedbackQuestion> getQuestions() {
        return questions;
    }

    public Map<FeedbackResponse, List<FeedbackResponseComment>> getResponseCommentsMap() {
        return responseCommentsMap;
    }

    public CourseRoster getRoster() {
        return roster;
    }

    public Map<UUID, Boolean> getResponseGiverVisibilityTable() {
        return responseGiverVisibilityTable;
    }

    public Map<UUID, Boolean> getResponseRecipientVisibilityTable() {
        return responseRecipientVisibilityTable;
    }

    public Map<UUID, Boolean> getCommentGiverVisibilityTable() {
        return commentGiverVisibilityTable;
    }

    public Set<FeedbackQuestion> getQuestionsNotVisibleForPreviewSet() {
        return questionsNotVisibleForPreviewSet;
    }

    public Set<FeedbackQuestion> getQuestionsWithCommentNotVisibleForPreviewSet() {
        return questionsWithCommentNotVisibleForPreviewSet;
    }
}

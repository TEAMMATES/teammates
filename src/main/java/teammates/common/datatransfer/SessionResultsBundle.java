package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * Represents detailed results for a feedback session.
 */
public class SessionResultsBundle {

    private final Map<String, FeedbackQuestionAttributes> questionsMap;
    private final Map<String, FeedbackQuestionAttributes> questionsNotVisibleForPreviewMap;
    private final Set<String> questionsWithCommentNotVisibleForPreview;
    private final Map<String, List<FeedbackResponseAttributes>> questionResponseMap;
    private final Map<String, List<FeedbackResponseAttributes>> questionMissingResponseMap;
    private final Map<String, List<FeedbackResponseCommentAttributes>> responseCommentsMap;
    private final Map<String, Boolean> responseGiverVisibilityTable;
    private final Map<String, Boolean> responseRecipientVisibilityTable;
    private final Map<Long, Boolean> commentGiverVisibilityTable;
    private final CourseRoster roster;

    public SessionResultsBundle(Map<String, FeedbackQuestionAttributes> questionsMap,
                                Map<String, FeedbackQuestionAttributes> questionsNotVisibleForPreviewMap,
                                Set<String> questionsWithCommentNotVisibleForPreview,
                                List<FeedbackResponseAttributes> responses,
                                List<FeedbackResponseAttributes> missingResponses,
                                Map<String, Boolean> responseGiverVisibilityTable,
                                Map<String, Boolean> responseRecipientVisibilityTable,
                                Map<String, List<FeedbackResponseCommentAttributes>> responseCommentsMap,
                                Map<Long, Boolean> commentGiverVisibilityTable,
                                CourseRoster roster) {

        this.questionsMap = questionsMap;
        this.questionsNotVisibleForPreviewMap = questionsNotVisibleForPreviewMap;
        this.questionsWithCommentNotVisibleForPreview = questionsWithCommentNotVisibleForPreview;
        this.responseCommentsMap = responseCommentsMap;
        this.responseGiverVisibilityTable = responseGiverVisibilityTable;
        this.responseRecipientVisibilityTable = responseRecipientVisibilityTable;
        this.commentGiverVisibilityTable = commentGiverVisibilityTable;
        this.roster = roster;
        this.questionResponseMap = buildQuestionToResponseMap(responses);
        this.questionMissingResponseMap = buildQuestionToResponseMap(missingResponses);
    }

    private Map<String, List<FeedbackResponseAttributes>> buildQuestionToResponseMap(
            List<FeedbackResponseAttributes> responses) {
        // build question to response map
        Map<String, List<FeedbackResponseAttributes>> questionToResponseMap = new LinkedHashMap<>();
        List<FeedbackQuestionAttributes> questions = new ArrayList<>(questionsMap.values());
        for (FeedbackQuestionAttributes question : questions) {
            questionToResponseMap.put(question.getId(), new ArrayList<>());
        }
        for (FeedbackResponseAttributes response : responses) {
            FeedbackQuestionAttributes question = questionsMap.get(response.getFeedbackQuestionId());
            List<FeedbackResponseAttributes> responsesForQuestion = questionToResponseMap.get(question.getId());
            responsesForQuestion.add(response);
        }
        return questionToResponseMap;
    }

    /**
     * Returns true if the giver of a response is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isResponseGiverVisible(FeedbackResponseAttributes response) {
        return isResponseParticipantVisible(true, response);
    }

    /**
     * Returns true if the recipient of a response is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isResponseRecipientVisible(FeedbackResponseAttributes response) {
        return isResponseParticipantVisible(false, response);
    }

    /**
     * Checks if the giver/recipient for a response is visible/hidden from the current user.
     */
    private boolean isResponseParticipantVisible(boolean isGiver, FeedbackResponseAttributes response) {
        FeedbackQuestionAttributes question = questionsMap.get(response.getFeedbackQuestionId());
        FeedbackParticipantType participantType;
        String responseId = response.getId();

        boolean isVisible;
        if (isGiver) {
            isVisible = responseGiverVisibilityTable.get(responseId);
            participantType = question.getGiverType();
        } else {
            isVisible = responseRecipientVisibilityTable.get(responseId);
            participantType = question.getRecipientType();
        }
        boolean isTypeNone = participantType == FeedbackParticipantType.NONE;

        return isVisible || isTypeNone;
    }

    /**
     * Returns true if the giver of a comment is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isCommentGiverVisible(FeedbackResponseCommentAttributes comment) {
        return commentGiverVisibilityTable.get(comment.getId());
    }

    /**
     * Gets the anonymous name for a given name.
     *
     * <p>The anonymous name will be deterministic based on {@code name}.
     */
    public static String getAnonName(FeedbackParticipantType type, String name) {
        String hashedEncryptedName = getHashOfName(getEncryptedName(name));
        String participantType = type.toSingularFormString();
        return String.format(
                Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " %s %s", participantType, hashedEncryptedName);
    }

    public Map<String, List<FeedbackResponseAttributes>> getQuestionResponseMap() {
        return questionResponseMap;
    }

    public Map<String, List<FeedbackResponseAttributes>> getQuestionMissingResponseMap() {
        return questionMissingResponseMap;
    }

    private static String getEncryptedName(String name) {
        return StringHelper.encrypt(name);
    }

    private static String getHashOfName(String name) {
        return Long.toString(Math.abs((long) name.hashCode()));
    }

    public Map<String, FeedbackQuestionAttributes> getQuestionsMap() {
        return questionsMap;
    }

    public Map<String, FeedbackQuestionAttributes> getQuestionsNotVisibleForPreviewMap() {
        return questionsNotVisibleForPreviewMap;
    }

    public Set<String> getQuestionsWithCommentNotVisibleForPreview() {
        return questionsWithCommentNotVisibleForPreview;
    }

    public Map<String, List<FeedbackResponseCommentAttributes>> getResponseCommentsMap() {
        return responseCommentsMap;
    }

    public CourseRoster getRoster() {
        return roster;
    }

    public Map<String, Boolean> getResponseGiverVisibilityTable() {
        return responseGiverVisibilityTable;
    }

    public Map<String, Boolean> getResponseRecipientVisibilityTable() {
        return responseRecipientVisibilityTable;
    }

    public Map<Long, Boolean> getCommentGiverVisibilityTable() {
        return commentGiverVisibilityTable;
    }
}

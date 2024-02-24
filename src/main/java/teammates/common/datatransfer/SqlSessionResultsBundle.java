package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;

/**
 * Represents detailed results for a feedback session.
 */
public class SqlSessionResultsBundle {

    private final List<FeedbackQuestion> questions;
    private final Set<FeedbackQuestion> questionsNotVisibleForPreviewSet;
    private final Set<FeedbackQuestion> questionsWithCommentNotVisibleForPreviewSet;
    private final Map<FeedbackQuestion, List<FeedbackResponse>> questionResponseMap;
    private final Map<FeedbackQuestion, List<FeedbackResponse>> questionMissingResponseMap;
    private final Map<FeedbackResponse, List<FeedbackResponseComment>> responseCommentsMap;
    private final Map<FeedbackResponse, Boolean> responseGiverVisibilityTable;
    private final Map<FeedbackResponse, Boolean> responseRecipientVisibilityTable;
    private final Map<Long, Boolean> commentGiverVisibilityTable;
    private final SqlCourseRoster roster;

    public SqlSessionResultsBundle(List<FeedbackQuestion> questions,
                                Set<FeedbackQuestion> questionsNotVisibleForPreviewSet,
                                Set<FeedbackQuestion> questionsWithCommentNotVisibleForPreviewSet,
                                List<FeedbackResponse> responses,
                                List<FeedbackResponse> missingResponses,
                                Map<FeedbackResponse, Boolean> responseGiverVisibilityTable,
                                Map<FeedbackResponse, Boolean> responseRecipientVisibilityTable,
                                Map<FeedbackResponse, List<FeedbackResponseComment>> responseCommentsMap,
                                Map<Long, Boolean> commentGiverVisibilityTable,
                                SqlCourseRoster roster) {

        this.questions = questions;
        this.questionsNotVisibleForPreviewSet = questionsNotVisibleForPreviewSet;
        this.questionsWithCommentNotVisibleForPreviewSet = questionsWithCommentNotVisibleForPreviewSet;
        this.responseCommentsMap = responseCommentsMap;
        this.responseGiverVisibilityTable = responseGiverVisibilityTable;
        this.responseRecipientVisibilityTable = responseRecipientVisibilityTable;
        this.commentGiverVisibilityTable = commentGiverVisibilityTable;
        this.roster = roster;
        this.questionResponseMap = buildQuestionToResponseMap(responses);
        this.questionMissingResponseMap = buildQuestionToResponseMap(missingResponses);
    }

    private Map<FeedbackQuestion, List<FeedbackResponse>> buildQuestionToResponseMap(
            List<FeedbackResponse> responses) {
        // build question to response map
        Map<FeedbackQuestion, List<FeedbackResponse>> questionToResponseMap = new LinkedHashMap<>();
        for (FeedbackQuestion question : questions) {
            questionToResponseMap.put(question, new ArrayList<>());
        }
        for (FeedbackResponse response : responses) {
            FeedbackQuestion question = response.getFeedbackQuestion();
            List<FeedbackResponse> responsesForQuestion = questionToResponseMap.get(question);
            responsesForQuestion.add(response);
        }
        return questionToResponseMap;
    }

    /**
     * Returns true if the giver of a response is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isResponseGiverVisible(FeedbackResponse response) {
        return isResponseParticipantVisible(true, response);
    }

    /**
     * Returns true if the recipient of a response is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isResponseRecipientVisible(FeedbackResponse response) {
        return isResponseParticipantVisible(false, response);
    }

    /**
     * Checks if the giver/recipient for a response is visible/hidden from the current user.
     */
    private boolean isResponseParticipantVisible(boolean isGiver, FeedbackResponse response) {
        FeedbackQuestion question = response.getFeedbackQuestion();
        FeedbackParticipantType participantType;

        boolean isVisible;
        if (isGiver) {
            isVisible = responseGiverVisibilityTable.get(response);
            participantType = question.getGiverType();
        } else {
            isVisible = responseRecipientVisibilityTable.get(response);
            participantType = question.getRecipientType();
        }
        boolean isTypeNone = participantType == FeedbackParticipantType.NONE;

        return isVisible || isTypeNone;
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
        String hashedEncryptedName = getHashOfName(getEncryptedName(name));
        String participantType = type.toSingularFormString();
        return String.format(
                Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " %s %s", participantType, hashedEncryptedName);
    }

    public Map<FeedbackQuestion, List<FeedbackResponse>> getQuestionResponseMap() {
        return questionResponseMap;
    }

    public Map<FeedbackQuestion, List<FeedbackResponse>> getQuestionMissingResponseMap() {
        return questionMissingResponseMap;
    }

    private static String getEncryptedName(String name) {
        return StringHelper.encrypt(name);
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

    public SqlCourseRoster getRoster() {
        return roster;
    }

    public Map<FeedbackResponse, Boolean> getResponseGiverVisibilityTable() {
        return responseGiverVisibilityTable;
    }

    public Map<FeedbackResponse, Boolean> getResponseRecipientVisibilityTable() {
        return responseRecipientVisibilityTable;
    }

    public Map<Long, Boolean> getCommentGiverVisibilityTable() {
        return commentGiverVisibilityTable;
    }

    public Set<FeedbackQuestion> getQuestionsNotVisibleForPreviewSet() {
        return questionsNotVisibleForPreviewSet;
    }

    public Set<FeedbackQuestion> getQuestionsWithCommentNotVisibleForPreviewSet() {
        return questionsWithCommentNotVisibleForPreviewSet;
    }
}

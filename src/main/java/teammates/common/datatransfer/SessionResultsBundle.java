package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * Represents detailed results for a feedback session.
 */
public class SessionResultsBundle {

    private final FeedbackSessionAttributes feedbackSession;
    private final Map<String, FeedbackQuestionAttributes> questionsMap;
    private final Map<String, List<FeedbackResponseAttributes>> questionResponseMap;
    private final Map<String, List<FeedbackResponseAttributes>> questionMissingResponseMap;
    private final Map<String, List<FeedbackResponseCommentAttributes>> responseCommentsMap;
    private final Map<String, boolean[]> responseVisibilityTable;
    private final Map<Long, boolean[]> commentVisibilityTable;
    private final CourseRoster roster;

    public SessionResultsBundle(FeedbackSessionAttributes feedbackSession,
                                Map<String, FeedbackQuestionAttributes> questionsMap,
                                List<FeedbackResponseAttributes> responses,
                                List<FeedbackResponseAttributes> missingResponses,
                                Map<String, boolean[]> responseVisibilityTable,
                                Map<String, List<FeedbackResponseCommentAttributes>> responseCommentsMap,
                                Map<Long, boolean[]> commentVisibilityTable,
                                CourseRoster roster) {

        this.feedbackSession = feedbackSession;
        this.questionsMap = questionsMap;
        this.responseCommentsMap = responseCommentsMap;
        this.responseVisibilityTable = responseVisibilityTable;
        this.commentVisibilityTable = commentVisibilityTable;
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
        FeedbackQuestionAttributes question = questionsMap.get(response.feedbackQuestionId);
        FeedbackParticipantType participantType;
        String responseId = response.getId();

        boolean isVisible;
        if (isGiver) {
            isVisible = responseVisibilityTable.get(responseId)[Const.VISIBILITY_TABLE_GIVER];
            participantType = question.giverType;
        } else {
            isVisible = responseVisibilityTable.get(responseId)[Const.VISIBILITY_TABLE_RECIPIENT];
            participantType = question.recipientType;
        }
        boolean isTypeNone = participantType == FeedbackParticipantType.NONE;

        return isVisible || isTypeNone;
    }

    /**
     * Returns true if the giver of a comment is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isCommentGiverVisible(FeedbackResponseCommentAttributes comment) {
        return commentVisibilityTable.get(comment.getId())[Const.VISIBILITY_TABLE_GIVER];
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

    public FeedbackSessionAttributes getFeedbackSession() {
        return feedbackSession;
    }

    public Map<String, FeedbackQuestionAttributes> getQuestionsMap() {
        return questionsMap;
    }

    public Map<String, List<FeedbackResponseCommentAttributes>> getResponseCommentsMap() {
        return responseCommentsMap;
    }

    public CourseRoster getRoster() {
        return roster;
    }

    public Map<String, boolean[]> getResponseVisibilityTable() {
        return responseVisibilityTable;
    }

    public Map<Long, boolean[]> getCommentVisibilityTable() {
        return commentVisibilityTable;
    }
}

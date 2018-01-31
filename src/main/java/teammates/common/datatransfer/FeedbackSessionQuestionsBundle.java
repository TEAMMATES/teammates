package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

public class FeedbackSessionQuestionsBundle {

    public FeedbackSessionAttributes feedbackSession;
    public Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionResponseBundle;
    public Map<String, Map<String, String>> recipientList;

    public FeedbackSessionQuestionsBundle(FeedbackSessionAttributes feedbackSession,
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionResponseBundle,
            Map<String, Map<String, String>> recipientList) {
        this.feedbackSession = feedbackSession;
        this.questionResponseBundle = questionResponseBundle;
        this.recipientList = recipientList;
    }

    public Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> getQuestionResponseBundle() {
        return questionResponseBundle;
    }

    public FeedbackSessionAttributes getFeedbackSession() {
        return feedbackSession;
    }

    /**
     * Gets the list of questions in this bundle, sorted by question number.
     * @return A {@code List} of {@code FeedackQuestionAttributes}.
     */
    public List<FeedbackQuestionAttributes> getSortedQuestions() {
        List<FeedbackQuestionAttributes> sortedQuestions =
                new ArrayList<>(this.questionResponseBundle.keySet());

        sortedQuestions.sort(null);

        return sortedQuestions;
    }

    /**
     * Gets the question in the data bundle with id == questionId.
     * @return a FeedbackQuestionAttribute with the specified questionId
     */
    public FeedbackQuestionAttributes getQuestionAttributes(String questionId) {
        List<FeedbackQuestionAttributes> questions =
                new ArrayList<>(this.questionResponseBundle.keySet());

        return questions.stream().filter(question -> question.getId()
                                                     .equals(questionId)).findFirst().orElse(null);
    }

    /**
     * Gets the recipient list for a question, sorted by the recipient's name.
     * @param feedbackQuestionId of the question
     * @return A {@code Map<String key, String value>} where {@code key} is the recipient's email
     *         and {@code value} is the recipients name.
     */
    public Map<String, String> getSortedRecipientList(String feedbackQuestionId) {

        List<Map.Entry<String, String>> sortedList = new ArrayList<>(recipientList.get(feedbackQuestionId).entrySet());

        sortedList.sort(Comparator.comparing((Map.Entry<String, String> obj) -> obj.getValue())
                .thenComparing(obj -> obj.getKey()));

        Map<String, String> result = new LinkedHashMap<>();

        sortedList.forEach(entry -> result.put(entry.getKey(), entry.getValue()));

        return result;
    }

    public Set<String> getRecipientEmails(String feedbackQuestionId) {
        List<Map.Entry<String, String>> emailList = new ArrayList<>(recipientList.get(feedbackQuestionId).entrySet());

        return emailList.stream().map(entry -> entry.getKey()).collect(Collectors.toSet());
    }

    /**
     * Removes question from the bundle if the question has givers or recipients that are anonymous to the instructor
     * or responses that are hidden from the instructor.
     */
    public void hideUnmoderatableQuestions() {
        List<FeedbackQuestionAttributes> questionsToHide = new ArrayList<>();

        for (FeedbackQuestionAttributes question : questionResponseBundle.keySet()) {
            boolean isGiverVisibleToInstructor = question.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
            boolean isRecipientVisibleToInstructor =
                    question.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
            boolean isResponseVisibleToInstructor = question.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS);

            if (!isResponseVisibleToInstructor || !isGiverVisibleToInstructor || !isRecipientVisibleToInstructor) {
                questionsToHide.add(question);
                questionResponseBundle.put(question, new ArrayList<FeedbackResponseAttributes>());
            }
        }

        questionResponseBundle.keySet().removeAll(questionsToHide);
    }

    /**
     * Empties responses for all questions in this bundle.
     * Used to not show existing responses when previewing as instructor
     */
    public void resetAllResponses() {
        for (FeedbackQuestionAttributes question : questionResponseBundle.keySet()) {
            questionResponseBundle.put(question, new ArrayList<FeedbackResponseAttributes>());
        }
    }
}

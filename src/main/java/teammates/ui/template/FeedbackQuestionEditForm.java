package teammates.ui.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;

/**
 * Data model for adding/editing a single question.
 */
public class FeedbackQuestionEditForm {
    private String actionLink;

    private String courseId;
    private String feedbackSessionName;

    private String questionText;
    private String questionDescription;
    private String questionTypeDisplayName;
    private FeedbackQuestionType questionType;
    private int questionIndex;

    // Used for adding a new question
    private String questionTypeOptions;
    private String doneEditingLink;

    private boolean isQuestionHasResponses;
    private List<ElementTag> questionNumberOptions;

    //TODO use element tags or a new class instead of having html in java
    private String questionSpecificEditFormHtml;

    private boolean isEditable;
    private FeedbackQuestionFeedbackPathSettings feedbackPathSettings;
    private FeedbackQuestionVisibilitySettings visibilitySettings;

    private String questionId;

    public static FeedbackQuestionEditForm getNewQnForm(String doneEditingLink, FeedbackSessionAttributes feedbackSession,
                                                        String questionTypeChoiceOptions, List<ElementTag> qnNumOptions,
                                                        String newQuestionEditForm) {

        FeedbackQuestionEditForm newQnForm = new FeedbackQuestionEditForm();

        newQnForm.doneEditingLink = doneEditingLink;
        newQnForm.actionLink = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD;
        newQnForm.courseId = feedbackSession.getCourseId();
        newQnForm.feedbackSessionName = feedbackSession.getFeedbackSessionName();
        newQnForm.questionIndex = -1;

        newQnForm.questionTypeOptions = questionTypeChoiceOptions;

        newQnForm.questionNumberOptions = qnNumOptions;

        FeedbackQuestionFeedbackPathSettings feedbackPathSettings = new FeedbackQuestionFeedbackPathSettings();

        newQnForm.feedbackPathSettings = feedbackPathSettings;

        feedbackPathSettings.setNumOfEntitiesToGiveFeedbackToValue(1);

        newQnForm.questionSpecificEditFormHtml = newQuestionEditForm;
        newQnForm.isEditable = true;

        FeedbackQuestionVisibilitySettings visibilitySettings =
                                        getDefaultVisibilityOptions();
        newQnForm.visibilitySettings = visibilitySettings;

        return newQnForm;
    }

    private static FeedbackQuestionVisibilitySettings getDefaultVisibilityOptions() {
        Map<String, Boolean> isGiverNameVisible = new HashMap<>();
        Map<String, Boolean> isRecipientNameVisible = new HashMap<>();
        Map<String, Boolean> isResponsesVisible = new HashMap<>();

        FeedbackParticipantType[] participantTypes = {
                FeedbackParticipantType.INSTRUCTORS,
                FeedbackParticipantType.RECEIVER
        };

        for (FeedbackParticipantType participant : participantTypes) {
            isGiverNameVisible.put(participant.name(), true);
            isRecipientNameVisible.put(participant.name(), true);
            isResponsesVisible.put(participant.name(), true);
        }

        return new FeedbackQuestionVisibilitySettings(new ArrayList<String>(), isResponsesVisible,
                                                      isGiverNameVisible, isRecipientNameVisible,
                                                      "Please select a visibility option <span class='caret'></span>");
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public boolean isQuestionHasResponses() {
        return isQuestionHasResponses;
    }

    public void setQuestionHasResponses(boolean isQuestionHasResponses) {
        this.isQuestionHasResponses = isQuestionHasResponses;
    }

    public List<ElementTag> getQuestionNumberOptions() {
        return questionNumberOptions;
    }

    public void setQuestionNumberOptions(List<ElementTag> questionNumberOptions) {
        this.questionNumberOptions = questionNumberOptions;
    }

    public String getQuestionSpecificEditFormHtml() {
        return questionSpecificEditFormHtml;
    }

    public void setQuestionSpecificEditFormHtml(String questionSpecificEditFormHtml) {
        this.questionSpecificEditFormHtml = questionSpecificEditFormHtml;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public String getAction() {
        return actionLink;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setAction(String action) {
        this.actionLink = action;
    }

    public String getQuestionTypeOptions() {
        return questionTypeOptions;
    }

    public String getDoneEditingLink() {
        return doneEditingLink;
    }

    public FeedbackQuestionFeedbackPathSettings getFeedbackPathSettings() {
        return feedbackPathSettings;
    }

    public void setFeedbackPathSettings(FeedbackQuestionFeedbackPathSettings generalSettings) {
        this.feedbackPathSettings = generalSettings;
    }

    public void setDoneEditingLink(String doneEditingLink) {
        this.doneEditingLink = doneEditingLink;
    }

    public void setQuestionTypeOptions(String questionTypeOptions) {
        this.questionTypeOptions = questionTypeOptions;
    }

    public FeedbackQuestionVisibilitySettings getVisibilitySettings() {
        return visibilitySettings;
    }

    public void setVisibilitySettings(FeedbackQuestionVisibilitySettings visibilitySettings) {
        this.visibilitySettings = visibilitySettings;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionId() {
        return this.questionId;
    }

    public String getQuestionTypeDisplayName() {
        return questionTypeDisplayName;
    }

    public void setQuestionTypeDisplayName(String questionTypeDisplayName) {
        this.questionTypeDisplayName = questionTypeDisplayName;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public void setQuestionType(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }

    public String getQuestionType() {
        return this.questionType.toString();
    }

}

package teammates.ui.template;

import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.util.Url;

/**
 * Data model for editing a single question 
 * 
 *
 */
public class FeedbackQuestionEditForm {
    
    private Url action;
    
    private String courseId;
    private String feedbackSessionName;
    
    // Used for adding a new question
    public List<String> questionTypeOptions;
    public Url doneEditingLink;
    
    
    private int numOfQuestionsOnPage;
    
    private boolean isQuestionHasResponses;
    private List<ElementTag> questionNumberOptions;
    
    
    private FeedbackQuestionAttributes question;
    private FeedbackQuestionDetails questionDetails;
    private String questionText;
    //TODO use element tags or a new class instead of having html in java
    private String questionSpecificEditFormHtml;
    
    private List<ElementTag> giverParticipantOptions;
    private List<ElementTag> recipientParticipantOptions;
    
    private boolean isNumberOfEntitiesToGiveFeedbackToChecked;
    private int numOfEntitiesToGiveFeedbackToValue;
    
    
    private List<String> visibilityMessages;
    private Map<String, Boolean> isGiverNameVisible;
    private Map<String, Boolean> isRecipientNameVisible;
    private Map<String, Boolean> isResponseVisible;
    
    public FeedbackQuestionEditForm() {
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
    public FeedbackQuestionAttributes getQuestion() {
        return question;
    }
    public void setQuestion(FeedbackQuestionAttributes question) {
        this.question = question;
    }
    public FeedbackQuestionDetails getQuestionDetails() {
        return questionDetails;
    }
    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        this.questionDetails = questionDetails;
    }
    public int getNumOfQuestionsOnPage() {
        return numOfQuestionsOnPage;
    }
    public void setNumOfQuestionsOnPage(int numOfQuestionsOnPage) {
        this.numOfQuestionsOnPage = numOfQuestionsOnPage;
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
    public List<ElementTag> getGiverParticipantOptions() {
        return giverParticipantOptions;
    }
    public String getQuestionText() {
        return questionText;
    }
    
    public void setGiverParticipantOptions(List<ElementTag> giverParticipantOptions) {
        this.giverParticipantOptions = giverParticipantOptions;
    }
    public List<ElementTag> getRecipientParticipantOptions() {
        return recipientParticipantOptions;
    }
    public void setRecipientParticipantOptions(List<ElementTag> recipientParticipantOptions) {
        this.recipientParticipantOptions = recipientParticipantOptions;
    }
    public boolean isNumberOfEntitiesToGiveFeedbackToChecked() {
        return isNumberOfEntitiesToGiveFeedbackToChecked;
    }
    public void setNumberOfEntitiesToGiveFeedbackToChecked(boolean isNumberOfEntitiesToGiveFeedbackToChecked) {
        this.isNumberOfEntitiesToGiveFeedbackToChecked = isNumberOfEntitiesToGiveFeedbackToChecked;
    }
    public int getNumOfEntitiesToGiveFeedbackToValue() {
        return numOfEntitiesToGiveFeedbackToValue;
    }
    public void setNumOfEntitiesToGiveFeedbackToValue(int numOfEntitiesToGiveFeedbackToValue) {
        this.numOfEntitiesToGiveFeedbackToValue = numOfEntitiesToGiveFeedbackToValue;
    }
    public List<String> getVisibilityMessages() {
        return visibilityMessages;
    }
    public void setVisibilityMessages(List<String> visibilityMessages) {
        this.visibilityMessages = visibilityMessages;
    }
    public Map<String, Boolean> getIsGiverNameVisible() {
        return isGiverNameVisible;
    }
    public void setIsGiverNameVisible(Map<String, Boolean> isGiverNameVisible) {
        this.isGiverNameVisible = isGiverNameVisible;
    }
    public Map<String, Boolean> getIsRecipientNameVisible() {
        return isRecipientNameVisible;
    }
    public void setIsRecipientNameVisible(Map<String, Boolean> isRecipientNameVisible) {
        this.isRecipientNameVisible = isRecipientNameVisible;
    }
    public Map<String, Boolean> getIsResponseVisible() {
        return isResponseVisible;
    }
    public void setIsResponseVisible(Map<String, Boolean> isResponseVisible) {
        this.isResponseVisible = isResponseVisible;
    }





    public Url getAction() {
        return action;
    }




    public void setAction(Url action) {
        this.action = action;
    }




    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }




    public List<String> getQuestionTypeOptions() {
        return questionTypeOptions;
    }




    public Url getDoneEditingLink() {
        return doneEditingLink;
    }
    
    
    
}

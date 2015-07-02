package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.util.Url;

/**
 * Data model for adding/editing a single question 
 *
 */
public class FeedbackQuestionEditForm {
    //TODO switch the rest of the Strings to use Url
    private Url action;
    
    private String courseId;
    private String feedbackSessionName;
    
    private String questionNumberSuffix;
    
    // Used for adding a new question
    private String questionTypeOptions;
    private Url doneEditingLink;
    
    private int numOfQuestionsOnPage;
    
    private boolean isQuestionHasResponses;
    private List<ElementTag> questionNumberOptions;
    
    private FeedbackQuestionAttributes question;
    private String questionText;
    
    //TODO use element tags or a new class instead of having html in java
    private String questionSpecificEditFormHtml;
    
    private boolean isEditable;
    private FeedbackQuestionFeedbackPathSettings feedbackPathSettings;
    private FeedbackQuestionVisibilitySettings visibilitySettings;
    
    
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
   
    public String getQuestionText() {
        return questionText;
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

    public String getQuestionTypeOptions() {
        return questionTypeOptions;
    }

    public Url getDoneEditingLink() {
        return doneEditingLink;
    }

    public FeedbackQuestionFeedbackPathSettings getFeedbackPathSettings() {
        return feedbackPathSettings;
    }

    public void setFeedbackPathSettings(FeedbackQuestionFeedbackPathSettings generalSettings) {
        this.feedbackPathSettings = generalSettings;
    }

    public String getQuestionNumberSuffix() {
        return questionNumberSuffix;
    }

    public void setQuestionNumberSuffix(String questionNumberSuffix) {
        this.questionNumberSuffix = questionNumberSuffix;
    }

    public void setDoneEditingLink(Url doneEditingLink) {
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

}

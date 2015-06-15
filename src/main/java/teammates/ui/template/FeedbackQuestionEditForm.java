package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.util.Url;

/**
 * Data model for adding/editing a single question 
 *
 */
public class FeedbackQuestionEditForm {
    
    private Url action;
    
    private String courseId;
    private String feedbackSessionName;
    
    // Used for adding a new question
    public String questionTypeOptions;
    public Url doneEditingLink;
    
    
    private int numOfQuestionsOnPage;
    
    private boolean isQuestionHasResponses;
    private List<ElementTag> questionNumberOptions;
    
    
    private FeedbackQuestionAttributes question;
    private FeedbackQuestionDetails questionDetails;
    private String questionText;
    //TODO use element tags or a new class instead of having html in java
    private String questionSpecificEditFormHtml;
    
    private FeedbackQuestionGeneralSettings generalSettings;
    
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

    public FeedbackQuestionGeneralSettings getGeneralSettings() {
        return generalSettings;
    }

    public void setGeneralSettings(FeedbackQuestionGeneralSettings generalSettings) {
        this.generalSettings = generalSettings;
    }

    
}

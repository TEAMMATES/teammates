package teammates.storage.entity;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import teammates.common.datatransfer.FeedbackQuestionType;


import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class FeedbackResponse {
    
    // Format is feedbackQuestionId%giverEmail%receiver
    // i.e. if response is feedback for team: qnId%giver@gmail.com%Team1
    //         if response is feedback for person: qnId%giver@gmail.com%reciever@email.com
    @PrimaryKey
    @Persistent
    private String feedbackResponseId;
    
    @Persistent
    private String feedbackSessionName;
    
    @Persistent
    private String courseId;
    
    @Persistent
    private String feedbackQuestionId;
    
    @Persistent
    private FeedbackQuestionType feedbackQuestionType;
    
    @Persistent
    private String giverEmail;
    
    @Persistent
    private String giverSection;

    @Persistent
    private String receiver; //TODO: rename to receiverEmail, will require database conversion
    
    @Persistent
    private String receiverSection;

    @Persistent
    private Text answer; //TODO: rename to responseMetaData, will require database conversion

    public String getId() {
        return feedbackResponseId;
    }

    /* Auto-generated. Do not set this.
    public void setFeedbackResponseId(String feedbackResponseId) {
        this.feedbackResponseId = feedbackResponseId;
    }*/

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public void setFeedbackSessionName(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getFeedbackQuestionId() {
        return feedbackQuestionId;
    }

    public void setFeedbackQuestionId(String feedbackQuestionId) {
        this.feedbackQuestionId = feedbackQuestionId;
    }

    public FeedbackQuestionType getFeedbackQuestionType() {
        return feedbackQuestionType;
    }

    public void setFeedbackQuestionType(FeedbackQuestionType feedbackQuestionType) {
        this.feedbackQuestionType = feedbackQuestionType;
    }

    public String getGiverEmail() {
        return giverEmail;
    }

    public void setGiverEmail(String giverEmail) {
        this.giverEmail = giverEmail;
    }

    public String getGiverSection() {
        return giverSection;
    }

    public void setGiverSection(String giverSection) {
        this.giverSection = giverSection;
    }

    public String getRecipientEmail() {
        return receiver;
    }

    public void setRecipientEmail(String receiverEmail) {
        this.receiver = receiverEmail;
    }

    public String getRecipientSection() {
        return receiverSection;
    }

    public void setRecipientSection(String recipientSection) {
        this.receiverSection = recipientSection;
    }

    public Text getResponseMetaData() {
        return answer;
    }

    public void setAnswer(Text answer) {
        this.answer = answer;
    }

    public FeedbackResponse(String feedbackSessionName, String courseId,
            String feedbackQuestionId, FeedbackQuestionType feedbackQuestionType,
            String giverEmail, String giverSection, String recipient, String recipientSection, Text answer) {
        this.feedbackSessionName = feedbackSessionName;
        this.courseId = courseId;
        this.feedbackQuestionId = feedbackQuestionId;
        this.feedbackQuestionType = feedbackQuestionType;
        this.giverEmail = giverEmail;
        this.giverSection = giverSection;
        this.receiver = recipient;
        this.receiverSection = recipientSection;
        this.answer = answer;
                
        this.feedbackResponseId = feedbackQuestionId + "%" + giverEmail + "%" + receiver;                                
    }
}

package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import teammates.common.util.FieldValidator;
import teammates.common.util.Utils;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Sanitizer;
import teammates.storage.entity.FeedbackResponseComment;

import com.google.appengine.api.datastore.Text;

/**
 * Represents a data transfer object for {@link FeedbackResponseComment} entities.
 */
public class FeedbackResponseCommentAttributes extends BaseCommentAttributes {

    private Long feedbackResponseCommentId = null;
    public String feedbackSessionName;
    public String feedbackQuestionId;
    
    /* Response giver section */
    public String giverSection;
    /* Response receiver section */
    public String receiverSection;
    public String feedbackResponseId;
    public List<FeedbackParticipantType> showCommentTo;
    public List<FeedbackParticipantType> showGiverNameTo;
    public boolean isVisibilityFollowingFeedbackQuestion = false;

    public FeedbackResponseCommentAttributes() {
        this.feedbackResponseCommentId = null;
        this.courseId = null;
        this.feedbackSessionName = null;
        this.feedbackQuestionId = null;
        this.giverEmail = null;
        this.feedbackResponseId = null;
        this.createdAt = null;
        this.commentText = null;
        this.giverSection = "None";
        this.receiverSection = "None";
        this.showCommentTo = new ArrayList<FeedbackParticipantType>();
        this.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
        this.lastEditorEmail = null;
        this.lastEditedAt = null;
    }
    
    public FeedbackResponseCommentAttributes(String courseId, String feedbackSessionName, String feedbackQuestionId,
            String giverEmail, String feedbackResponseId, Date createdAt, Text commentText) {
        this(courseId, feedbackSessionName, feedbackQuestionId, giverEmail, 
                feedbackResponseId, createdAt, commentText, "None", "None");
    }

    public FeedbackResponseCommentAttributes(String courseId, String feedbackSessionName, String feedbackQuestionId,
                                             String giverEmail, String feedbackResponseId, Date createdAt,
                                             Text commentText, String giverSection, String receiverSection) {
        super(courseId, giverEmail, createdAt, commentText);
        this.feedbackSessionName = feedbackSessionName;
        this.feedbackQuestionId = feedbackQuestionId;
        this.feedbackResponseId = feedbackResponseId;
        this.giverSection = giverSection;
        this.receiverSection = receiverSection;
        this.showCommentTo = new ArrayList<FeedbackParticipantType>();
        this.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
    }
    
    public FeedbackResponseCommentAttributes(FeedbackResponseComment comment) {
        super(comment.getCourseId(), comment.getGiverEmail(), comment.getCreatedAt(), comment.getCommentText());
        this.feedbackResponseCommentId = comment.getFeedbackResponseCommentId();
        this.feedbackSessionName = comment.getFeedbackSessionName();
        this.feedbackQuestionId = comment.getFeedbackQuestionId();
        this.feedbackResponseId = comment.getFeedbackResponseId();
        this.sendingState = comment.getSendingState() != null ? comment.getSendingState() : CommentSendingState.SENT;
        this.giverSection = comment.getGiverSection() != null ? comment.getGiverSection() : "None";
        this.receiverSection = comment.getReceiverSection() != null ? comment.getReceiverSection() : "None";
        this.lastEditorEmail = comment.getLastEditorEmail() != null ?
                comment.getLastEditorEmail() : comment.getGiverEmail();
        this.lastEditedAt = comment.getLastEditedAt() != null ? comment.getLastEditedAt() : comment.getCreatedAt();
        if (comment.getIsVisibilityFollowingFeedbackQuestion() != null
                && !comment.getIsVisibilityFollowingFeedbackQuestion()) {
            this.showCommentTo = comment.getShowCommentTo();
            this.showGiverNameTo = comment.getShowGiverNameTo();
        } else {
            setDefaultVisibilityOptions();
        }
    }

    public Long getId() {
        return this.feedbackResponseCommentId;
    }

    public void setId(Long commentId) {
        this.feedbackResponseCommentId = commentId;
    }
    
    private void setDefaultVisibilityOptions() {
        isVisibilityFollowingFeedbackQuestion = true;
        this.showCommentTo = new ArrayList<FeedbackParticipantType>();
        this.showGiverNameTo = new ArrayList<FeedbackParticipantType>();
    }
    
    public boolean isVisibleTo(FeedbackParticipantType viewerType){
        return showCommentTo.contains(viewerType);
    }
    
    @Override
    public List<String> getInvalidityInfo() {
        List<String> errors = super.getInvalidityInfo();
        FieldValidator validator = new FieldValidator();
        String error = validator.getInvalidityInfo(FieldType.FEEDBACK_SESSION_NAME, feedbackSessionName);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        // TODO: handle the new attributes showCommentTo and showGiverNameTo

        return errors;
    }

    @Override
    public FeedbackResponseComment toEntity() {
        return new FeedbackResponseComment(courseId, feedbackSessionName, feedbackQuestionId, giverEmail,
                                           feedbackResponseId, sendingState, createdAt, commentText,
                                           giverSection, receiverSection, showCommentTo, showGiverNameTo,
                                           lastEditorEmail, lastEditedAt);
    }

    @Override
    public String getEntityTypeAsString() {
        return "FeedbackResponseComment";
    }

    @Override
    public String getJsonString() {
        return Utils.getTeammatesGson().toJson(this, FeedbackResponseCommentAttributes.class);
    }
    
    @Override
    public void sanitizeForSaving() {
        super.sanitizeForSaving();
        this.feedbackSessionName = this.feedbackSessionName.trim();
        this.feedbackSessionName = Sanitizer.sanitizeForHtml(feedbackSessionName);
        this.feedbackQuestionId = Sanitizer.sanitizeForHtml(feedbackQuestionId);
        this.feedbackResponseId = Sanitizer.sanitizeForHtml(feedbackResponseId);
        if (commentText != null) {
            //replacing "\n" with "\n<br>" here is to make comment text support displaying breakline
            String sanitizedText = Sanitizer.sanitizeForHtml(commentText.getValue()).replace("\n", "\n<br>");
            this.commentText = new Text(sanitizedText);
        }
    }
    
    @Override
    public String toString() {
        //TODO: print visibilityOptions also
        return "FeedbackResponseCommentAttributes ["
                + "feedbackResponseCommentId = " + feedbackResponseCommentId 
                + ", courseId = " + courseId 
                + ", feedbackSessionName = " + feedbackSessionName
                + ", feedbackQuestionId = " + feedbackQuestionId
                + ", giverEmail = " + giverEmail 
                + ", feedbackResponseId = " + feedbackResponseId
                + ", commentText = " + commentText.getValue() 
                + ", createdAt = " + createdAt
                + ", lastEditorEmail = " + lastEditorEmail
                + ", lastEditedAt = " + lastEditedAt + "]";
    }
    
    public static void sortFeedbackResponseCommentsByCreationTime(List<FeedbackResponseCommentAttributes> frcs) {
        Collections.sort(frcs, new Comparator<FeedbackResponseCommentAttributes>() {
            public int compare(FeedbackResponseCommentAttributes frc1, FeedbackResponseCommentAttributes frc2) {
                return frc1.createdAt.compareTo(frc2.createdAt);
            }
        });
    }
    
    public String getEditedAtTextForSessionsView(Boolean isGiverAnonymous) {
        return getEditedAtText(isGiverAnonymous, this.lastEditorEmail, this.lastEditedAt.toString());        
    }

}

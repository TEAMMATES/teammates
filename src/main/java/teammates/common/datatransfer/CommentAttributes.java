package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.Utils;
import teammates.common.util.FieldValidator.FieldType;
import teammates.storage.entity.Comment;

import com.google.appengine.api.datastore.Text;

/**
 * A data transfer object for {@link Comment} entities.
 */
public class CommentAttributes extends BaseCommentAttributes {

    private Long commentId = null;
    public CommentRecipientType recipientType = CommentRecipientType.PERSON;
    public Set<String> recipients;
    public CommentStatus status = CommentStatus.FINAL;
    //TODO: rename CommentRecipientType to CommentParticipantType
    public List<CommentRecipientType> showCommentTo;
    public List<CommentRecipientType> showGiverNameTo;
    public List<CommentRecipientType> showRecipientNameTo;

    public CommentAttributes() {

    }

    public CommentAttributes(String courseId, String giverEmail, CommentRecipientType recipientType,
            Set<String> recipients, Date createdAt, Text commentText) {
        super(courseId, giverEmail, createdAt, commentText);
        this.recipientType = recipientType != null ? recipientType : CommentRecipientType.PERSON;
        this.recipients = recipients;
        this.showCommentTo = new ArrayList<CommentRecipientType>();
        this.showGiverNameTo = new ArrayList<CommentRecipientType>();
        this.showRecipientNameTo = new ArrayList<CommentRecipientType>();
    }

    public CommentAttributes(Comment comment) {
        super(comment.getCourseId(), comment.getGiverEmail(), comment.getCreatedAt(), comment.getCommentText());
        this.commentId = comment.getId();
        this.recipientType = comment.getRecipientType();
        this.status = comment.getStatus();
        this.sendingState = comment.getSendingState() != null? comment.getSendingState() : CommentSendingState.SENT;
        this.showCommentTo = comment.getShowCommentTo();
        this.showGiverNameTo = comment.getShowGiverNameTo();
        this.showRecipientNameTo = comment.getShowRecipientNameTo();
        this.recipients = comment.getRecipients();
        this.lastEditorEmail = comment.getLastEditorEmail() != null ? comment.getLastEditorEmail() : comment.getGiverEmail();
        this.lastEditedAt = comment.getLastEditedAt() != null ? comment.getLastEditedAt() : comment.getCreatedAt();
    }

    public Long getId() {
        return getCommentId();
    }
    
    public Long getCommentId() {
        return this.commentId;
    }

    // Use only to match existing and known Comment
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public List<String> getInvalidityInfo() {

        List<String> errors = super.getInvalidityInfo();
        FieldValidator validator = new FieldValidator();
        String error;

        if (recipients != null && recipientType != null) {
            switch (recipientType) {
            case PERSON:
                for (String recipientId : recipients) {
                    error = validator.getInvalidityInfo(FieldType.EMAIL, recipientId);
                    if (!error.isEmpty()) {
                        errors.add(error);
                    }
                }
                break;
            case TEAM:
                for (String recipientId : recipients) {
                    error = validator.getInvalidityInfo(FieldType.TEAM_NAME, recipientId);
                    if (!error.isEmpty()) {
                        errors.add(error);
                    }
                }
                break;
            case SECTION:
                for (String recipientId : recipients) {
                    error = validator.getInvalidityInfo(FieldType.SECTION_NAME, recipientId);
                    if (!error.isEmpty()) {
                        errors.add(error);
                    }
                }
                break;
            case COURSE:
                for (String recipientId : recipients) {
                    error = validator.getInvalidityInfo(FieldType.COURSE_ID, recipientId);
                    if (!error.isEmpty()) {
                        errors.add(error);
                    }
                }
                break;
            default:// cases for NONE or null
                break;
            }
        }

        return errors;
    }

    public Comment toEntity() {
        return new Comment(courseId, giverEmail, recipientType, recipients, status,
                sendingState,
                showCommentTo, 
                showGiverNameTo, 
                showRecipientNameTo, 
                commentText, createdAt, lastEditorEmail, lastEditedAt);
    }
    
    public Boolean isVisibleTo(CommentRecipientType targetViewer){
        if (this.showCommentTo == null) {
            return false;
        }
        return showCommentTo.contains(targetViewer);
    }

    @Override
    public String toString() {
        return "CommentAttributes [commentId = " + commentId +
                ", courseId = " + courseId +
                ", giverEmail = " + giverEmail +
                ", recipientType = " + recipientType +
                ", recipient = " + recipients +
                ", status = " + status +
                ", showCommentTo = " + showCommentTo +
                ", showGiverNameTo = " + showGiverNameTo +
                ", showRecipientNameTo = " + showRecipientNameTo +
                ", commentText = " + commentText.getValue() +
                ", createdAt = " + createdAt +
                ", lastEditorEmail = " + lastEditorEmail +
                ", lastEditedAt = " + lastEditedAt + "]";
    }

    @Override
    public String getEntityTypeAsString() {
        return "Comment";
    }

    @Override
    public String getJsonString() {
        return Utils.getTeammatesGson().toJson(this, CommentAttributes.class);
    }
    
    @Override
    public void sanitizeForSaving() {
        super.sanitizeForSaving();

        if(recipients != null){
            HashSet<String> sanitizedRecipients = new HashSet<String>();
            for (String recipientId : recipients) {
                sanitizedRecipients.add(Sanitizer.sanitizeForHtml(recipientId));
            }
            recipients = sanitizedRecipients;
        }
        
        if (commentText != null) {
            //replacing "\n" with "\n<br>" here is to make comment text support displaying breakline
            String sanitizedText = Sanitizer.sanitizeForHtml(commentText.getValue()).replace("\n", "\n<br>");
            this.commentText = new Text(sanitizedText);
        }
        
        if(recipientType != null){
            sanitizeForVisibilityOptions();
        }
        
        removeIrrelevantVisibilityOptions();
    }

    private void sanitizeForVisibilityOptions() {
        switch(recipientType){
        case PERSON:
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentRecipientType.PERSON);
            break;
        case TEAM:
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.PERSON);
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentRecipientType.TEAM);
            break;
        case SECTION:
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.PERSON);
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.TEAM);
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentRecipientType.SECTION);
            break;
        case COURSE:
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.PERSON);
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.TEAM);
            removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType.SECTION);
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentRecipientType.COURSE);
            break;
        default:
            break;
        }
    }
    
    private void removeIrrelevantVisibilityOptions() {
        if (this.showGiverNameTo != null) {
            Iterator<CommentRecipientType> iterGiver = this.showGiverNameTo.iterator();
            while (iterGiver.hasNext()) {
                if (!this.isVisibleTo(iterGiver.next())) {
                    iterGiver.remove();
                }
            }
        }
        if (this.showRecipientNameTo != null) {
            Iterator<CommentRecipientType> iterRecipient = this.showRecipientNameTo.iterator();
            while (iterRecipient.hasNext()) {
                if (!this.isVisibleTo(iterRecipient.next())) {
                    iterRecipient.remove();
                }
            }
        }
    }

    private void removeCommentRecipientTypeInVisibilityOptions(CommentRecipientType typeToRemove){
        removeCommentRecipientTypeIn(showCommentTo, typeToRemove);
        removeCommentRecipientTypeIn(showGiverNameTo, typeToRemove);
        removeCommentRecipientTypeIn(showRecipientNameTo, typeToRemove);
    }
    
    private void removeCommentRecipientTypeIn(List<CommentRecipientType> visibilityOptions, 
            CommentRecipientType typeToRemove){
        if(visibilityOptions == null) return;
        
        Iterator<CommentRecipientType> iter = visibilityOptions.iterator();
        while(iter.hasNext()){
            CommentRecipientType otherType = iter.next();
            if(otherType == typeToRemove){
                iter.remove();
            }
        }
    }

    public static void sortCommentsByCreationTime(List<CommentAttributes> comments) {
        Collections.sort(comments, new Comparator<CommentAttributes>() {
            public int compare(CommentAttributes comment1, CommentAttributes comment2) {
                return comment1.createdAt.compareTo(comment2.createdAt);
            }
        });
    }

    public static void sortCommentsByCreationTimeDescending(List<CommentAttributes> comments) {
        Collections.sort(comments, new Comparator<CommentAttributes>() {
            public int compare(CommentAttributes comment1, CommentAttributes comment2) {
                return comment2.createdAt.compareTo(comment1.createdAt);
            }
        });
    }

}

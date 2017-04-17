package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.Comment;

/**
 * A data transfer object for {@link Comment} entities.
 */
public class CommentAttributes extends EntityAttributes implements Comparable<CommentAttributes> {

    public String courseId;
    public String giverEmail;
    public CommentParticipantType recipientType = CommentParticipantType.PERSON;
    public Set<String> recipients;
    public CommentStatus status = CommentStatus.FINAL;
    public transient CommentSendingState sendingState = CommentSendingState.SENT;
    public List<CommentParticipantType> showCommentTo;
    public List<CommentParticipantType> showGiverNameTo;
    public List<CommentParticipantType> showRecipientNameTo;
    public Date createdAt;
    public String lastEditorEmail;
    public Date lastEditedAt;
    public Text commentText;
    private Long commentId;

    public CommentAttributes() {
        // attributes to be set after construction
    }

    public CommentAttributes(String courseId, String giverEmail, CommentParticipantType recipientType,
                             Set<String> recipients, Date createdAt, Text commentText) {
        this.courseId = courseId;
        this.giverEmail = giverEmail;
        this.recipientType = recipientType == null ? CommentParticipantType.PERSON : recipientType;
        this.recipients = recipients;
        this.commentText = SanitizationHelper.sanitizeForRichText(commentText);
        this.createdAt = createdAt;
        this.lastEditorEmail = giverEmail;
        this.lastEditedAt = createdAt;
    }

    public CommentAttributes(Comment comment) {
        this.commentId = comment.getId();
        this.courseId = comment.getCourseId();
        this.giverEmail = comment.getGiverEmail();
        this.recipientType = comment.getRecipientType();
        this.status = comment.getStatus();
        this.sendingState = comment.getSendingState() == null ? CommentSendingState.SENT : comment.getSendingState();
        this.showCommentTo = comment.getShowCommentTo();
        this.showGiverNameTo = comment.getShowGiverNameTo();
        this.showRecipientNameTo = comment.getShowRecipientNameTo();
        this.recipients = comment.getRecipients();
        this.createdAt = comment.getCreatedAt();
        this.commentText = SanitizationHelper.sanitizeForRichText(comment.getCommentText());
        this.lastEditorEmail = comment.getLastEditorEmail() == null
                             ? comment.getGiverEmail()
                             : comment.getLastEditorEmail();
        this.lastEditedAt = comment.getLastEditedAt() == null ? comment.getCreatedAt() : comment.getLastEditedAt();
    }

    public Long getCommentId() {
        return this.commentId;
    }

    public String getCommentText() {
        return commentText.getValue();
    }

    public CommentParticipantType getRecipientType() {
        return this.recipientType;
    }

    public List<CommentParticipantType> getShowCommentTo() {
        return showCommentTo;
    }

    public boolean isPendingNotification() {
        return sendingState.equals(CommentSendingState.PENDING);
    }

    // Use only to match existing and known Comment
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    @Override
    public List<String> getInvalidityInfo() {

        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();

        addNonEmptyError(validator.getInvalidityInfoForCourseId(courseId), errors);

        addNonEmptyError(validator.getInvalidityInfoForEmail(giverEmail), errors);

        if (recipients != null && recipientType != null) {
            switch (recipientType) {
            case PERSON :
                for (String recipientId : recipients) {
                    addNonEmptyError(validator.getInvalidityInfoForEmail(recipientId), errors);
                }
                break;
            case TEAM :
                for (String recipientId : recipients) {
                    addNonEmptyError(validator.getInvalidityInfoForTeamName(recipientId), errors);
                }
                break;
            case SECTION :
                for (String recipientId : recipients) {
                    addNonEmptyError(validator.getInvalidityInfoForSectionName(recipientId), errors);
                }
                break;
            case COURSE :
                for (String recipientId : recipients) {
                    addNonEmptyError(validator.getInvalidityInfoForCourseId(recipientId), errors);
                }
                break;
            default : // cases for NONE or null
                break;
            }
        }

        return errors;
    }

    @Override
    public Comment toEntity() {
        return new Comment(courseId, giverEmail, recipientType, recipients, status, sendingState, showCommentTo,
                showGiverNameTo, showRecipientNameTo, commentText, createdAt, lastEditorEmail, lastEditedAt);
    }

    public Boolean isVisibleTo(CommentParticipantType targetViewer) {
        if (this.showCommentTo == null) {
            return false;
        }
        return showCommentTo.contains(targetViewer);
    }

    @Override
    public String toString() {
        return "CommentAttributes [commentId = " + commentId
               + ", courseId = " + courseId
               + ", giverEmail = " + giverEmail
               + ", recipientType = " + recipientType
               + ", recipient = " + recipients
               + ", status = " + status
               + ", showCommentTo = " + showCommentTo
               + ", showGiverNameTo = " + showGiverNameTo
               + ", showRecipientNameTo = " + showRecipientNameTo
               + ", commentText = " + commentText.getValue()
               + ", createdAt = " + createdAt
               + ", lastEditorEmail = " + lastEditorEmail
               + ", lastEditedAt = " + lastEditedAt + "]";
    }

    @Override
    public String getIdentificationString() {
        return toString();
    }

    @Override
    public String getEntityTypeAsString() {
        return "Comment";
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, CommentAttributes.class);
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }

    @Override
    public void sanitizeForSaving() {
        this.courseId = this.courseId.trim();
        this.commentText = SanitizationHelper.sanitizeForRichText(commentText);
        this.courseId = SanitizationHelper.sanitizeForHtml(courseId);
        this.giverEmail = SanitizationHelper.sanitizeForHtml(giverEmail);

        if (recipients != null) {
            HashSet<String> sanitizedRecipients = new HashSet<String>();
            for (String recipientId : recipients) {
                sanitizedRecipients.add(SanitizationHelper.sanitizeForHtml(recipientId));
            }
            recipients = sanitizedRecipients;
        }

        if (recipientType != null) {
            sanitizeForVisibilityOptions();
        }

        removeIrrelevantVisibilityOptions();
    }

    private void sanitizeForVisibilityOptions() {
        switch (recipientType) {
        case PERSON :
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentParticipantType.PERSON);
            break;
        case TEAM :
            removeCommentRecipientTypeInVisibilityOptions(CommentParticipantType.PERSON);
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentParticipantType.TEAM);
            break;
        case SECTION :
            removeCommentRecipientTypeInVisibilityOptions(CommentParticipantType.PERSON);
            removeCommentRecipientTypeInVisibilityOptions(CommentParticipantType.TEAM);
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentParticipantType.SECTION);
            break;
        case COURSE :
            removeCommentRecipientTypeInVisibilityOptions(CommentParticipantType.PERSON);
            removeCommentRecipientTypeInVisibilityOptions(CommentParticipantType.TEAM);
            removeCommentRecipientTypeInVisibilityOptions(CommentParticipantType.SECTION);
            removeCommentRecipientTypeIn(showRecipientNameTo, CommentParticipantType.COURSE);
            break;
        default :
            break;
        }
    }

    private void removeIrrelevantVisibilityOptions() {
        if (this.showGiverNameTo != null) {
            Iterator<CommentParticipantType> iterGiver = this.showGiverNameTo.iterator();
            while (iterGiver.hasNext()) {
                if (!this.isVisibleTo(iterGiver.next())) {
                    iterGiver.remove();
                }
            }
        }
        if (this.showRecipientNameTo != null) {
            Iterator<CommentParticipantType> iterRecipient = this.showRecipientNameTo.iterator();
            while (iterRecipient.hasNext()) {
                if (!this.isVisibleTo(iterRecipient.next())) {
                    iterRecipient.remove();
                }
            }
        }
    }

    private void removeCommentRecipientTypeInVisibilityOptions(CommentParticipantType typeToRemove) {
        removeCommentRecipientTypeIn(showCommentTo, typeToRemove);
        removeCommentRecipientTypeIn(showGiverNameTo, typeToRemove);
        removeCommentRecipientTypeIn(showRecipientNameTo, typeToRemove);
    }

    private void removeCommentRecipientTypeIn(List<CommentParticipantType> visibilityOptions,
            CommentParticipantType typeToRemove) {
        if (visibilityOptions == null) {
            return;
        }

        Iterator<CommentParticipantType> iter = visibilityOptions.iterator();
        while (iter.hasNext()) {
            CommentParticipantType otherType = iter.next();
            if (otherType.equals(typeToRemove)) {
                iter.remove();
            }
        }
    }

    public static void sortCommentsByCreationTime(List<CommentAttributes> comments) {
        Collections.sort(comments, new Comparator<CommentAttributes>() {
            @Override
            public int compare(CommentAttributes comment1, CommentAttributes comment2) {
                return comment1.createdAt.compareTo(comment2.createdAt);
            }
        });
    }

    public static void sortCommentsByCreationTimeDescending(List<CommentAttributes> comments) {
        Collections.sort(comments, new Comparator<CommentAttributes>() {
            @Override
            public int compare(CommentAttributes comment1, CommentAttributes comment2) {
                return comment2.createdAt.compareTo(comment1.createdAt);
            }
        });
    }

    @Override
    public int compareTo(CommentAttributes o) {
        if (o == null) {
            return 1;
        }
        return o.createdAt.compareTo(createdAt);
    }

    public String getEditedAtText(Boolean isGiverAnonymous) {
        if (this.lastEditedAt == null || this.lastEditedAt.equals(this.createdAt)) {
            return "";
        }
        String displayTimeAs = TimeHelper.formatDateTimeForComments(this.lastEditedAt);
        return "(last edited "
             + (isGiverAnonymous ? "" : "by " + this.lastEditorEmail + " ")
             + "at " + displayTimeAs + ")";

    }
}

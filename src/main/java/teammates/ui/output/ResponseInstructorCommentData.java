package teammates.ui.output;

import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.visibility.CommentVisibilityType;
import teammates.common.util.Const;
import teammates.storage.entity.ResponseInstructorComment;

/**
 * The API output format of {@link ResponseInstructorComment}.
 */
public class ResponseInstructorCommentData implements ApiOutput {
    private UUID responseInstructorCommentId;

    private String commentGiverName;
    private String lastEditorName;

    private String commentText;
    private long createdAt;
    private long lastEditedAt;

    private List<CommentVisibilityType> showGiverNameTo;
    private List<CommentVisibilityType> showCommentTo;

    private ResponseInstructorCommentData() {
        // for Jackson deserialization
    }

    public ResponseInstructorCommentData(ResponseInstructorComment frc) {
        this.commentGiverName = frc.getGiver().getDisplayName();
        this.lastEditorName = frc.getLastEditedBy() == null
                ? Const.UNKNOWN_USER : frc.getLastEditedBy().getDisplayName();
        this.responseInstructorCommentId = frc.getId();
        this.commentText = frc.getCommentText();
        this.showGiverNameTo = frc.getShowGiverNameTo();
        this.showCommentTo = frc.getShowCommentTo();
        this.createdAt = frc.getCreatedAt().toEpochMilli();
        this.lastEditedAt = frc.getUpdatedAt().toEpochMilli();
    }

    public ResponseInstructorCommentData(ResponseInstructorComment frc, boolean isGiverVisible) {
        this(frc);
        if (!isGiverVisible) {
            this.commentGiverName = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
            this.lastEditorName = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
        }
    }

    public String getCommentText() {
        return commentText;
    }

    public UUID getResponseInstructorCommentId() {
        return responseInstructorCommentId;
    }

    public String getCommentGiverName() {
        return commentGiverName;
    }

    public List<CommentVisibilityType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public List<CommentVisibilityType> getShowCommentTo() {
        return showCommentTo;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getLastEditorName() {
        return lastEditorName;
    }

    public long getLastEditedAt() {
        return lastEditedAt;
    }

}

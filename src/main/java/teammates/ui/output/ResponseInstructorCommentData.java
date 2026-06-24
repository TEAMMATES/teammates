package teammates.ui.output;

import java.util.UUID;

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

    private ResponseInstructorCommentData() {
        // for Jackson deserialization
    }

    public ResponseInstructorCommentData(ResponseInstructorComment frc) {
        this.commentGiverName = frc.getGiver().getDisplayName();
        this.lastEditorName = frc.getLastEditedBy() == null
                ? Const.UNKNOWN_USER : frc.getLastEditedBy().getDisplayName();
        this.responseInstructorCommentId = frc.getId();
        this.commentText = frc.getCommentText();
        this.createdAt = frc.getCreatedAt().toEpochMilli();
        this.lastEditedAt = frc.getUpdatedAt().toEpochMilli();
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

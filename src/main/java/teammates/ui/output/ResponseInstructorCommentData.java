package teammates.ui.output;

import java.util.UUID;

import teammates.storage.entity.ResponseInstructorComment;

/**
 * The API output format of {@link ResponseInstructorComment}.
 */
public class ResponseInstructorCommentData implements ApiOutput {
    private UUID responseInstructorCommentId;
    private UUID giverId;

    private String commentGiverName;

    private String commentText;
    private long createdAt;

    private ResponseInstructorCommentData() {
        // for Jackson deserialization
    }

    public ResponseInstructorCommentData(ResponseInstructorComment frc) {
        this.commentGiverName = frc.getGiver().getDisplayName();
        this.responseInstructorCommentId = frc.getId();
        this.giverId = frc.getGiverId();
        this.commentText = frc.getCommentText();
        this.createdAt = frc.getCreatedAt().toEpochMilli();
    }

    public String getCommentText() {
        return commentText;
    }

    public UUID getResponseInstructorCommentId() {
        return responseInstructorCommentId;
    }

    public UUID getGiverId() {
        return giverId;
    }

    public String getCommentGiverName() {
        return commentGiverName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

}

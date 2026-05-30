package teammates.ui.output;

import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.util.Const;
import teammates.storage.entity.ResponseInstructorComment;

/**
 * The API output format of {@link ResponseInstructorComment}.
 */
public class ResponseInstructorCommentData extends ApiOutput {
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
        this.lastEditorName = frc.getLastEditedBy().getDisplayName();
        this.responseInstructorCommentId = frc.getId();
        this.commentText = frc.getCommentText();
        this.showGiverNameTo = convertToFeedbackVisibilityType(frc.getShowGiverNameTo());
        this.showCommentTo = convertToFeedbackVisibilityType(frc.getShowCommentTo());
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

    /**
     * Converts a list of feedback participant type to a list of comment visibility type.
     */
    private List<CommentVisibilityType> convertToFeedbackVisibilityType(
            List<ViewerType> viewerTypes) {
        return viewerTypes.stream().map(viewerType -> {
            switch (viewerType) {
            case INSTRUCTORS:
                return CommentVisibilityType.INSTRUCTORS;
            case STUDENTS:
                return CommentVisibilityType.STUDENTS;
            case GIVER:
                return CommentVisibilityType.GIVER;
            case OWN_TEAM_MEMBERS:
                return CommentVisibilityType.GIVER_TEAM_MEMBERS;
            case RECEIVER:
                return CommentVisibilityType.RECIPIENT;
            case RECEIVER_TEAM_MEMBERS:
                return CommentVisibilityType.RECIPIENT_TEAM_MEMBERS;
            default:
                assert false : "Unknown ViewerType " + viewerType;
                break;
            }
            return null;
        }).toList();
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

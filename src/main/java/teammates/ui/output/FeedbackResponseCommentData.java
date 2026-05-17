package teammates.ui.output;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.storage.entity.FeedbackResponseComment;

/**
 * The API output format of {@link FeedbackResponseComment}.
 */
public class FeedbackResponseCommentData extends ApiOutput {

    String commentGiver;
    String lastEditorEmail;

    private UUID feedbackResponseCommentId;
    private String commentText;
    private long createdAt;
    private long lastEditedAt;
    private boolean isVisibilityFollowingFeedbackQuestion;

    private List<CommentVisibilityType> showGiverNameTo;
    private List<CommentVisibilityType> showCommentTo;

    private FeedbackResponseCommentData() {
        // for Jackson deserialization
    }

    public FeedbackResponseCommentData(FeedbackResponseComment frc) {
        // TODO: combine with CommentOutput to simplify output classes
        this.feedbackResponseCommentId = frc.getId();
        this.commentText = frc.getCommentText();
        this.commentGiver = frc.getGiver().getIdentifier();
        this.showGiverNameTo = convertToFeedbackVisibilityType(frc.getShowGiverNameTo());
        this.showCommentTo = convertToFeedbackVisibilityType(frc.getShowCommentTo());
        this.createdAt = frc.getCreatedAt().toEpochMilli();
        this.lastEditedAt = frc.getUpdatedAt().toEpochMilli();
        this.lastEditorEmail = frc.getLastEditedBy().getIdentifier();
        this.isVisibilityFollowingFeedbackQuestion = frc.getIsVisibilityFollowingFeedbackQuestion();
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
        }).collect(Collectors.toList());
    }

    public String getCommentText() {
        return commentText;
    }

    public UUID getFeedbackResponseCommentId() {
        return feedbackResponseCommentId;
    }

    public String getCommentGiver() {
        return commentGiver;
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

    public String getLastEditorEmail() {
        return lastEditorEmail;
    }

    public long getLastEditedAt() {
        return lastEditedAt;
    }

    public boolean isVisibilityFollowingFeedbackQuestion() {
        return isVisibilityFollowingFeedbackQuestion;
    }
}

package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Assumption;

/**
 * The API output format of {@link teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes}.
 */
public class FeedbackResponseCommentData extends ApiOutput {

    String commentGiver;
    String lastEditorEmail;

    private long feedbackResponseCommentId;
    private String commentText;
    private long createdAt;
    private long lastEditedAt;
    private boolean isVisibilityFollowingFeedbackQuestion;

    private List<CommentVisibilityType> showGiverNameTo;
    private List<CommentVisibilityType> showCommentTo;

    public FeedbackResponseCommentData(FeedbackResponseCommentAttributes frc) {
        this.feedbackResponseCommentId = frc.getId();
        this.commentText = frc.getCommentText();
        this.commentGiver = frc.getCommentGiver();
        this.showGiverNameTo = convertToFeedbackVisibilityType(frc.getShowGiverNameTo());
        this.showCommentTo = convertToFeedbackVisibilityType(frc.getShowCommentTo());
        this.createdAt = frc.getCreatedAt().toEpochMilli();
        this.lastEditedAt = frc.getLastEditedAt().toEpochMilli();
        this.lastEditorEmail = frc.getLastEditorEmail();
        this.isVisibilityFollowingFeedbackQuestion = frc.isVisibilityFollowingFeedbackQuestion();
    }

    /**
     * Converts a list of feedback participant type to a list of comment visibility type.
     */
    private List<CommentVisibilityType> convertToFeedbackVisibilityType(
            List<FeedbackParticipantType> feedbackParticipantTypeList) {
        return feedbackParticipantTypeList.stream().map(feedbackParticipantType -> {
            switch (feedbackParticipantType) {
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
                Assumption.fail("Unknown feedbackParticipantType" + feedbackParticipantType);
                break;
            }
            return null;
        }).collect(Collectors.toList());
    }

    public String getCommentText() {
        return commentText;
    }

    public long getFeedbackResponseCommentId() {
        return feedbackResponseCommentId;
    }

    public String getFeedbackCommentText() {
        return commentText;
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

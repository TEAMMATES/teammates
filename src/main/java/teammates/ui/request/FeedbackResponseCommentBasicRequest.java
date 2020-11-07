package teammates.ui.request;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Assumption;
import teammates.ui.output.CommentVisibilityType;

/**
 * The basic request of modifying a feedback response comment.
 */
class FeedbackResponseCommentBasicRequest extends BasicRequest {

    private String commentText;

    private List<CommentVisibilityType> showCommentTo;
    private List<CommentVisibilityType> showGiverNameTo;

    FeedbackResponseCommentBasicRequest(String commentText,
                                        List<CommentVisibilityType> showCommentTo,
                                        List<CommentVisibilityType> showGiverNameTo) {
        this.commentText = commentText;
        this.showCommentTo = showCommentTo;
        this.showGiverNameTo = showGiverNameTo;
    }

    @Override
    public void validate() {
        assertTrue(commentText != null, "Comment Text can't be null");
    }

    public String getCommentText() {
        return commentText;
    }

    public List<FeedbackParticipantType> getShowCommentTo() {
        return convertToFeedbackParticipantType(showCommentTo);
    }

    public List<FeedbackParticipantType> getShowGiverNameTo() {
        return convertToFeedbackParticipantType(showGiverNameTo);
    }

    /**
     * Converts a list of comment visibility type to a list of feedback participant type.
     */
    private List<FeedbackParticipantType> convertToFeedbackParticipantType(
            List<CommentVisibilityType> commentVisibilityTypes) {
        return commentVisibilityTypes.stream().map(commentVisibilityType -> {
            switch (commentVisibilityType) {
            case GIVER:
                return FeedbackParticipantType.GIVER;
            case RECIPIENT:
                return FeedbackParticipantType.RECEIVER;
            case GIVER_TEAM_MEMBERS:
                return FeedbackParticipantType.OWN_TEAM_MEMBERS;
            case RECIPIENT_TEAM_MEMBERS:
                return FeedbackParticipantType.RECEIVER_TEAM_MEMBERS;
            case STUDENTS:
                return FeedbackParticipantType.STUDENTS;
            case INSTRUCTORS:
                return FeedbackParticipantType.INSTRUCTORS;
            default:
                Assumption.fail("Unknown commentVisibilityType " + commentVisibilityType);
                break;
            }
            return null;
        }).collect(Collectors.toList());
    }
}

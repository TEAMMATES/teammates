package teammates.ui.request;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.ui.output.CommentVisibilityType;

/**
 * The basic request of modifying a feedback response comment.
 */
class FeedbackResponseCommentBasicRequest extends BasicRequest {

    private String commentText;

    private List<CommentVisibilityType> showCommentTo;
    private List<CommentVisibilityType> showGiverNameTo;

    @JsonCreator
    FeedbackResponseCommentBasicRequest(String commentText,
                                        List<CommentVisibilityType> showCommentTo,
                                        List<CommentVisibilityType> showGiverNameTo) {
        this.commentText = commentText;
        this.showCommentTo = showCommentTo;
        this.showGiverNameTo = showGiverNameTo;
    }

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        assertTrue(commentText != null, "Comment Text can't be null");
    }

    public String getCommentText() {
        return commentText;
    }

    public List<ViewerType> getShowCommentTo() {
        return convertToViewerType(showCommentTo);
    }

    public List<ViewerType> getShowGiverNameTo() {
        return convertToViewerType(showGiverNameTo);
    }

    /**
     * Converts a list of comment visibility type to a list of feedback participant type.
     */
    private List<ViewerType> convertToViewerType(
            List<CommentVisibilityType> commentVisibilityTypes) {
        return commentVisibilityTypes.stream().map(commentVisibilityType -> {
            switch (commentVisibilityType) {
            case GIVER:
                return ViewerType.GIVER;
            case RECIPIENT:
                return ViewerType.RECEIVER;
            case GIVER_TEAM_MEMBERS:
                return ViewerType.OWN_TEAM_MEMBERS;
            case RECIPIENT_TEAM_MEMBERS:
                return ViewerType.RECEIVER_TEAM_MEMBERS;
            case STUDENTS:
                return ViewerType.STUDENTS;
            case INSTRUCTORS:
                return ViewerType.INSTRUCTORS;
            default:
                assert false : "Unknown commentVisibilityType " + commentVisibilityType;
                break;
            }
            return null;
        }).collect(Collectors.toList());
    }
}

package teammates.test.scenariobuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;

/**
 * Builder for ResponseInstructorComment entities used in test scenarios.
 */
public final class GivenResponseInstructorComment extends GivenBase<ResponseInstructorComment> {
    public GivenResponseInstructorComment(GivenData given, UUID responseInstructorCommentId) {
        super(given);
        this.entity = defaultResponseInstructorComment(responseInstructorCommentId);
    }

    /**
     * Sets the feedback response for the comment.
     */
    public GivenResponseInstructorComment feedbackResponse(String feedbackResponseAlias) {
        assert entity.getFeedbackResponse() == null : "Feedback response has already been set for this comment";
        FeedbackResponse feedbackResponse = given.getOrCreate(
                feedbackResponseAlias, given.dataBundle.feedbackResponses, given::feedbackResponse);
        entity.setFeedbackResponse(feedbackResponse);
        return this;
    }

    /**
     * Sets an instructor as the giver.
     */
    public GivenResponseInstructorComment giver(String instructorAlias) {
        assert entity.getGiver() == null : "Giver has already been set for this comment";
        Instructor instructor = given.getOrCreate(
                instructorAlias, given.dataBundle.instructors, (String iAlias) -> given.instructor(iAlias,
                        i -> i.course(getFeedbackResponseCourseAlias())));
        entity.setGiver(new ResponseGiver(instructor));
        return this;
    }

    /**
     * Sets an instructor as the last editor.
     */
    public GivenResponseInstructorComment lastEditedBy(String instructorAlias) {
        assert isLastEditedByEmpty() : "Last editor has already been set for this comment";
        Instructor instructor = given.getOrCreate(
                instructorAlias, given.dataBundle.instructors, (String iAlias) -> given.instructor(iAlias,
                        i -> i.course(getFeedbackResponseCourseAlias())));
        entity.setLastEditedBy(new ResponseGiver(instructor));
        return this;
    }

    /**
     * Sets the comment text.
     */
    public GivenResponseInstructorComment commentText(String commentText) {
        entity.setCommentText(commentText);
        return this;
    }

    /**
     * Sets who can see the comment.
     */
    public GivenResponseInstructorComment showCommentTo(ViewerType... viewerTypes) {
        entity.setShowCommentTo(viewerTypesList(viewerTypes));
        return this;
    }

    /**
     * Sets who can see the giver name.
     */
    public GivenResponseInstructorComment showGiverNameTo(ViewerType... viewerTypes) {
        entity.setShowGiverNameTo(viewerTypesList(viewerTypes));
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getFeedbackResponse() == null) {
            this.feedbackResponse("default");
        }

        if (entity.getGiver() == null) {
            this.giver("default:response-instructor-comment-giver:" + entity.getId());
        }

        if (isLastEditedByEmpty()) {
            entity.setLastEditedBy(entity.getGiver());
        }

        entity.getFeedbackResponse().addResponseInstructorComment(entity);
    }

    private String getFeedbackResponseCourseAlias() {
        if (entity.getFeedbackResponse() == null) {
            this.feedbackResponse("default");
        }

        return given.getAlias(entity.getFeedbackResponse().getFeedbackQuestion().getFeedbackSession().getCourse());
    }

    private static List<ViewerType> defaultViewerTypes() {
        return viewerTypesList(ViewerType.GIVER, ViewerType.RECEIVER, ViewerType.INSTRUCTORS);
    }

    private static List<ViewerType> viewerTypesList(ViewerType... viewerTypes) {
        return new ArrayList<>(Arrays.asList(viewerTypes));
    }

    private boolean isLastEditedByEmpty() {
        return !entity.getLastEditedBy().isGiverUser() && !entity.getLastEditedBy().isGiverTeam();
    }

    private ResponseInstructorComment defaultResponseInstructorComment(UUID responseInstructorCommentId) {
        ResponseInstructorComment responseInstructorComment = new ResponseInstructorComment(
                null, "comment:" + responseInstructorCommentId, defaultViewerTypes(), defaultViewerTypes(), null);
        responseInstructorComment.setId(responseInstructorCommentId);
        return responseInstructorComment;
    }
}

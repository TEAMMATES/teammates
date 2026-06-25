package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
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
        entity.setGiver(instructor);
        return this;
    }

    /**
     * Sets the comment text.
     */
    public GivenResponseInstructorComment commentText(String commentText) {
        entity.setCommentText(commentText);
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

        String responseCourseId =
                entity.getFeedbackResponse().getFeedbackQuestion().getFeedbackSession().getCourseId();
        assert entity.getGiver().getCourseId().equals(responseCourseId)
                : "Giver's course '" + entity.getGiver().getCourseId() + "' does not match the feedback response's "
                + "course '" + responseCourseId + "'. Set the feedback response before the giver, or pre-create "
                + "the giver in the correct course.";

        entity.getFeedbackResponse().addResponseInstructorComment(entity);
    }

    private String getFeedbackResponseCourseAlias() {
        if (entity.getFeedbackResponse() == null) {
            this.feedbackResponse("default");
        }

        return given.getAlias(entity.getFeedbackResponse().getFeedbackQuestion().getFeedbackSession().getCourse());
    }

    private ResponseInstructorComment defaultResponseInstructorComment(UUID responseInstructorCommentId) {
        ResponseInstructorComment responseInstructorComment =
                new ResponseInstructorComment(null, "comment:" + responseInstructorCommentId);
        responseInstructorComment.setId(responseInstructorCommentId);
        return responseInstructorComment;
    }
}

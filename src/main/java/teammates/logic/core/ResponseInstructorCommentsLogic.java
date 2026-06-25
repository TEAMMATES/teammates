package teammates.logic.core;

import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.ResponseInstructorCommentsDb;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.ui.request.ResponseInstructorCommentUpdateRequest;

/**
 * Handles operations related to feedback response comments.
 *
 * @see ResponseInstructorComment
 * @see ResponseInstructorCommentsDb
 */
public final class ResponseInstructorCommentsLogic {

    private static final ResponseInstructorCommentsLogic instance = new ResponseInstructorCommentsLogic();
    private ResponseInstructorCommentsDb frcDb;
    private FeedbackResponsesLogic frLogic;

    private ResponseInstructorCommentsLogic() {
        // prevent initialization
    }

    public static ResponseInstructorCommentsLogic inst() {
        return instance;
    }

    /**
     * Initialize dependencies for {@code ResponseInstructorCommentsLogic}.
     */
    void initLogicDependencies(ResponseInstructorCommentsDb frcDb, FeedbackResponsesLogic frLogic) {
        this.frcDb = frcDb;
        this.frLogic = frLogic;
    }

    /**
     * Gets an feedback response comment by feedback response comment id.
     * @param id of feedback response comment.
     * @return the specified feedback response comment.
     */
    public ResponseInstructorComment getResponseInstructorComment(UUID id) {
        return frcDb.getResponseInstructorComment(id);
    }

    /**
     * Creates a feedback response comment.
     *
     * @throws EntityDoesNotExistException if the feedback response does not exist
     * @throws InvalidParametersException if the comment is invalid
     */
    public ResponseInstructorComment createResponseInstructorComment(UUID feedbackResponseId, Instructor giver,
            String commentText)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackResponse feedbackResponse = frLogic.getFeedbackResponse(feedbackResponseId);
        if (feedbackResponse == null) {
            throw new EntityDoesNotExistException("The feedback response does not exist.");
        }

        ResponseInstructorComment frc = new ResponseInstructorComment(giver, commentText);
        feedbackResponse.addResponseInstructorComment(frc);

        validateResponseInstructorComment(frc);

        return frcDb.persistResponseInstructorComment(frc);
    }

    /**
     * Deletes a ResponseInstructorComment.
     *
     * <p>Fails silently if the comment does not exist.</p>
     */
    public void deleteResponseInstructorComment(UUID frcId) {
        ResponseInstructorComment frc = getResponseInstructorComment(frcId);
        if (frc == null) {
            return;
        }
        frcDb.removeResponseInstructorComment(frc);
    }

    /**
     * Updates a feedback response comment by {@link ResponseInstructorComment}.
     *
     * @return updated comment
     * @throws InvalidParametersException if attributes to update are not valid
     */
    public ResponseInstructorComment updateResponseInstructorComment(ResponseInstructorComment responseInstructorComment)
            throws InvalidParametersException {
        validateResponseInstructorComment(responseInstructorComment);
        return responseInstructorComment;
    }

    /**
     * Updates a feedback response comment.
     * @throws EntityDoesNotExistException if the comment does not exist
     */
    public ResponseInstructorComment updateResponseInstructorComment(UUID frcId,
            ResponseInstructorCommentUpdateRequest updateRequest, Instructor updater)
            throws EntityDoesNotExistException {
        ResponseInstructorComment comment = frcDb.getResponseInstructorComment(frcId);
        if (comment == null) {
            throw new EntityDoesNotExistException("Trying to update a feedback response comment that does not exist.");
        }

        comment.setCommentText(updateRequest.getCommentText());

        return comment;
    }

    /**
     * Gets all feedback response comments for the given feedback response IDs.
     */
    public List<ResponseInstructorComment> getResponseInstructorCommentsForResponses(List<UUID> feedbackResponseIds) {
        return frcDb.getResponseInstructorCommentsForResponses(feedbackResponseIds);
    }

    private void validateResponseInstructorComment(ResponseInstructorComment responseInstructorComment)
            throws InvalidParametersException {
        if (!responseInstructorComment.isValid()) {
            throw new InvalidParametersException(responseInstructorComment.getInvalidityInfo());
        }
    }
}

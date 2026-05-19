package teammates.ui.webapi;

import java.util.Objects;
import java.util.UUID;

import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Basic action class for feedback response comment related operation.
 */
abstract class BasicCommentSubmissionAction extends BasicFeedbackSubmissionAction {

    static final String FEEDBACK_RESPONSE_COMMENT_EMPTY = "Comment cannot be empty";

    /**
     * Validates comment of corresponding response doesn't exist in DB.
     */
    void verifyCommentNotExist(UUID feedbackResponseId) throws InvalidOperationException {
        FeedbackResponseComment comment = logic
                .getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);

        if (comment != null) {
            throw new InvalidOperationException("Comment has already been created for the response in submission");
        }
    }

    /**
     * Verify response ownership for student.
     */
    void verifyResponseOwnershipForStudent(Student student, FeedbackResponse response)
            throws UnauthorizedAccessException {
        Objects.requireNonNull(student);
        if (Objects.equals(response.getGiver().getGiverUser(), student)
                || Objects.equals(response.getGiver().getGiverTeam(), student.getTeam())) {
            return;
        }

        throw new UnauthorizedAccessException("Response [" + response.getId() + "] is not accessible to "
                + student.getName());
    }

    /**
     * Verify response ownership for instructor.
     */
    void verifyResponseOwnerShipForInstructor(Instructor instructor, FeedbackResponse response)
            throws UnauthorizedAccessException {
        Objects.requireNonNull(instructor);
        if (Objects.equals(response.getGiver().getGiverUser(), instructor)) {
            return;
        }

        throw new UnauthorizedAccessException("Response [" + response.getId() + "] is not accessible to "
                + instructor.getName());
    }
}

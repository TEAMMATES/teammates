package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * Basic action class for feedback response comment related operation.
 */
abstract class BasicCommentSubmissionAction extends BasicFeedbackSubmissionAction {

    static final String FEEDBACK_RESPONSE_COMMENT_EMPTY = "Comment cannot be empty";

    /**
     * Validates comment of corresponding response doesn't exist in SQL DB.
     */
    void verifyCommentNotExist(UUID feedbackResponseId) throws InvalidOperationException {
        FeedbackResponseComment comment =
                sqlLogic.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);

        if (comment != null) {
            throw new InvalidOperationException("Comment has already been created for the response in submission");
        }
    }

    /**
     * Verify response ownership for student.
     */
    void verifyResponseOwnerShipForStudent(Student student, FeedbackResponse response,
                                           FeedbackQuestion question)
            throws UnauthorizedAccessException {
        if (question.getGiverType() == FeedbackParticipantType.TEAMS
                && !response.getGiver().equals(student.getTeamName())) {
            throw new UnauthorizedAccessException("Response [" + response.getId() + "] is not accessible to "
                    + student.getTeam());
        } else if (question.getGiverType() == FeedbackParticipantType.STUDENTS
                && !response.getGiver().equals(student.getEmail())) {
            throw new UnauthorizedAccessException("Response [" + response.getId() + "] is not accessible to "
                    + student.getName());
        }
    }

    /**
     * Verify response ownership for instructor.
     */
    void verifyResponseOwnerShipForInstructor(Instructor instructor, FeedbackResponse response)
            throws UnauthorizedAccessException {
        if (!response.getGiver().equals(instructor.getEmail())) {
            throw new UnauthorizedAccessException("Response [" + response.getId() + "] is not accessible to "
                    + instructor.getName());
        }
    }
}

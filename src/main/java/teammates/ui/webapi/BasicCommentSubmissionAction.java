package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.SanitizationHelper;
import teammates.logic.entity.FeedbackQuestion;
import teammates.logic.entity.FeedbackResponse;
import teammates.logic.entity.FeedbackResponseComment;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;

/**
 * Basic action class for feedback response comment related operation.
 */
abstract class BasicCommentSubmissionAction extends BasicFeedbackSubmissionAction {

    static final String FEEDBACK_RESPONSE_COMMENT_EMPTY = "Comment cannot be empty";

    /**
     * Validates comment of corresponding response doesn't exist in SQL DB.
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
    void verifyResponseOwnershipForStudent(Student student, FeedbackResponse response,
            FeedbackQuestion question)
            throws UnauthorizedAccessException {
        if (question.getGiverType() == FeedbackParticipantType.TEAMS
                && !response.getGiver().equals(student.getTeamName())) {
            throw new UnauthorizedAccessException("Response [" + response.getId() + "] is not accessible to "
                    + student.getTeam());
        } else if (question.getGiverType() == FeedbackParticipantType.STUDENTS
                && !SanitizationHelper.areEmailsEqual(response.getGiver(), student.getEmail())) {
            throw new UnauthorizedAccessException("Response [" + response.getId() + "] is not accessible to "
                    + student.getName());
        }
    }

    /**
     * Verify response ownership for instructor.
     */
    void verifyResponseOwnerShipForInstructor(Instructor instructor, FeedbackResponse response)
            throws UnauthorizedAccessException {
        if (!SanitizationHelper.areEmailsEqual(response.getGiver(), instructor.getEmail())) {
            throw new UnauthorizedAccessException("Response [" + response.getId() + "] is not accessible to "
                    + instructor.getName());
        }
    }
}

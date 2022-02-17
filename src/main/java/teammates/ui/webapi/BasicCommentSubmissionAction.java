package teammates.ui.webapi;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Basic action class for feedback response comment related operation.
 */
abstract class BasicCommentSubmissionAction extends BasicFeedbackSubmissionAction {

    static final String FEEDBACK_RESPONSE_COMMENT_EMPTY = "Comment cannot be empty";

    /**
     * Validates the questionType of the corresponding question.
     */
    void validQuestionForCommentInSubmission(FeedbackQuestionAttributes feedbackQuestion) {
        if (!feedbackQuestion.getQuestionDetailsCopy().isFeedbackParticipantCommentsOnResponsesAllowed()) {
            throw new InvalidHttpParameterException("Invalid question type for comment in submission");
        }
    }

    /**
     * Validates comment doesn't exist of corresponding response.
     */
    void verifyCommentNotExist(String feedbackResponseId) throws InvalidOperationException {
        FeedbackResponseCommentAttributes comment =
                logic.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);

        if (comment != null) {
            throw new InvalidOperationException("Comment has already been created for the response in submission");
        }

    }

    /**
     * Verify response ownership for student.
     */
    void verifyResponseOwnerShipForStudent(StudentAttributes student, FeedbackResponseAttributes response,
                                           FeedbackQuestionAttributes question)
            throws UnauthorizedAccessException {

        if (question.getGiverType() == FeedbackParticipantType.TEAMS
                && !response.getGiver().equals(student.getTeam())) {
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
    void verifyResponseOwnerShipForInstructor(InstructorAttributes instructor,
                                              FeedbackResponseAttributes response)
            throws UnauthorizedAccessException {
        if (!response.getGiver().equals(instructor.getEmail())) {
            throw new UnauthorizedAccessException("Response [" + response.getId() + "] is not accessible to "
                    + instructor.getName());
        }
    }
}

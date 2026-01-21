package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * Basic action class for feedback response comment related operation.
 */
abstract class BasicCommentSubmissionAction extends BasicFeedbackSubmissionAction {

    static final String FEEDBACK_RESPONSE_COMMENT_EMPTY = "Comment cannot be empty";

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
    void verifyResponseOwnerShipForInstructor(InstructorAttributes instructor,
                                              FeedbackResponseAttributes response)
            throws UnauthorizedAccessException {
        if (!response.getGiver().equals(instructor.getEmail())) {
            throw new UnauthorizedAccessException("Response [" + response.getId() + "] is not accessible to "
                    + instructor.getName());
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

    /**
     * Parses feedback response ID parameter, trying SQL UUID first, then Datastore encrypted ID.
     *
     * @param feedbackResponseIdParam the raw feedback response ID parameter
     * @return ParsedFeedbackResponseId containing either SQL UUID or Datastore ID
     * @throws InvalidHttpParameterException if the parameter is neither a valid UUID nor encrypted ID
     */
    protected ParsedFeedbackResponseId parseFeedbackResponseId(String feedbackResponseIdParam) {
        try {
            UUID sqlId = getUuidFromString(Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseIdParam);
            return ParsedFeedbackResponseId.forSql(sqlId);
        } catch (InvalidHttpParameterException uuidException) {
            try {
                String datastoreId = StringHelper.decrypt(feedbackResponseIdParam);
                return ParsedFeedbackResponseId.forDatastore(datastoreId);
            } catch (InvalidParametersException ipe) {
                InvalidHttpParameterException exception = new InvalidHttpParameterException(ipe);
                exception.addSuppressed(uuidException);
                throw exception;
            }
        }
    }

    /**
     * Container for parsed feedback response ID, supporting both SQL and Datastore formats.
     */
    protected static final class ParsedFeedbackResponseId {
        final UUID sqlId;
        final String datastoreId;
        final boolean isSql;

        private ParsedFeedbackResponseId(UUID sqlId, String datastoreId, boolean isSql) {
            this.sqlId = sqlId;
            this.datastoreId = datastoreId;
            this.isSql = isSql;
        }

        static ParsedFeedbackResponseId forSql(UUID id) {
            return new ParsedFeedbackResponseId(id, null, true);
        }

        static ParsedFeedbackResponseId forDatastore(String id) {
            return new ParsedFeedbackResponseId(null, id, false);
        }
    }
}

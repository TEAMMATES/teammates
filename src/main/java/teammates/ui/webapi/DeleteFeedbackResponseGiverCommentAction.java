package teammates.ui.webapi;

import java.util.Objects;
import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.Intent;

/**
 * Deletes the giver comment for a feedback response.
 */
public class DeleteFeedbackResponseGiverCommentAction extends BasicFeedbackSubmissionAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID feedbackResponseId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        FeedbackResponse response = logic.getFeedbackResponse(feedbackResponseId);
        if (response == null) {
            throw new EntityNotFoundException("The feedback response does not exist.");
        }

        FeedbackQuestion question = response.getFeedbackQuestion();
        FeedbackSession session = question.getFeedbackSession();
        String courseId = question.getCourseId();
        Intent intent = getEnumRequestParamValue(Const.ParamsNames.INTENT, Intent.class);

        switch (intent) {
        case STUDENT_SUBMISSION:
            Student student = getStudentOfCourseForSubmission(courseId, false);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            checkAccessControlForStudentFeedbackSubmission(student, session);
            verifySessionOpenExceptForModeration(session, student);
            verifyResponseOwnershipForStudent(student, response);
            break;
        case INSTRUCTOR_SUBMISSION:
            Instructor instructorAsFeedbackParticipant = getInstructorOfCourseForSubmission(courseId, false);
            verifyInstructorCanSeeQuestionIfInModeration(question);
            checkAccessControlForInstructorFeedbackSubmission(instructorAsFeedbackParticipant, session);
            verifySessionOpenExceptForModeration(session, instructorAsFeedbackParticipant);
            verifyResponseOwnershipForInstructor(instructorAsFeedbackParticipant, response);
            break;
        default:
            throw new InvalidHttpParameterException("Invalid intent for this action");
        }
    }

    @Override
    public JsonResult execute() {
        UUID feedbackResponseId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        try {
            logic.deleteFeedbackResponseGiverComment(feedbackResponseId);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        return new JsonResult("Successfully deleted feedback response giver comment.");
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
    void verifyResponseOwnershipForInstructor(Instructor instructor, FeedbackResponse response)
            throws UnauthorizedAccessException {
        Objects.requireNonNull(instructor);
        if (Objects.equals(response.getGiver().getGiverUser(), instructor)) {
            return;
        }

        throw new UnauthorizedAccessException("Response [" + response.getId() + "] is not accessible to "
                + instructor.getName());
    }
}

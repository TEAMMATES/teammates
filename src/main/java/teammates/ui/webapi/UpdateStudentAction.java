package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.StudentUpdateRequest;

/**
 * Action: Edits details of a student in a course.
 */
public class UpdateStudentAction extends Action {
    /** Message indicating that the student to be edited could not be found in the system. */
    public static final String STUDENT_NOT_FOUND_FOR_EDIT = "The student you tried to edit does not exist.";

    /** Message indicating that the student information was successfully updated. */
    public static final String SUCCESSFUL_UPDATE = "Student has been updated";
    /**
     * Message indicating that the student information was successfully updated,
     * and email was successfully sent to the student's new email address.
     */
    public static final String SUCCESSFUL_UPDATE_WITH_EMAIL = SUCCESSFUL_UPDATE + " and email sent";
    /**
     * Message indicating that the student information was successfully updated,
     * but email failed to be sent to the student's new email address.
     */
    public static final String SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED = SUCCESSFUL_UPDATE + " but email failed to send";
    /**
     * Message indicating that the update operation failed because the requested new email address
     * for the student is already being used by another student in the system.
     */
    public static final String ERROR_EMAIL_ALREADY_EXISTS = "Trying to update to an email that is already in use";

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        UUID studentId = getUuidRequestParamValue(Const.ParamsNames.STUDENT_SQL_ID);
        Student existingStudent = logic.getStudent(studentId);
        if (existingStudent == null) {
            throw new EntityNotFoundException(STUDENT_NOT_FOUND_FOR_EDIT);
        }

        Instructor instructor = logic.getInstructorByGoogleId(existingStudent.getCourseId(), userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(existingStudent.getCourseId()), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID studentId = getUuidRequestParamValue(Const.ParamsNames.STUDENT_SQL_ID);
        StudentUpdateRequest updateRequest = getAndValidateRequestBody(StudentUpdateRequest.class);

        Student existingStudent = logic.getStudent(studentId);
        if (existingStudent == null) {
            throw new EntityNotFoundException(STUDENT_NOT_FOUND_FOR_EDIT);
        }

        try {
            logic.updateStudent(studentId, updateRequest);
        } catch (EnrollException e) {
            throw new InvalidOperationException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(ERROR_EMAIL_ALREADY_EXISTS, e);
        }

        if (updateRequest.getIsSessionSummarySendEmail()) {
            String courseId = existingStudent.getCourseId();
            boolean emailSent = sendEmail(courseId, updateRequest.getEmail());
            String statusMessage = emailSent ? SUCCESSFUL_UPDATE_WITH_EMAIL
                    : SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED;
            return new JsonResult(statusMessage);
        }

        return new JsonResult(SUCCESSFUL_UPDATE);
    }

    /**
     * Sends the feedback session summary as an email.
     *
     * @return The true if email was sent successfully or false otherwise.
     */
    private boolean sendEmail(String courseId, String studentEmail) {
        EmailWrapper email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                courseId, studentEmail, EmailType.STUDENT_EMAIL_CHANGED);
        EmailSendingStatus status = emailSender.sendEmail(email);
        return status.isSuccess();
    }

}

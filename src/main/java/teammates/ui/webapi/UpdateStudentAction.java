package teammates.ui.webapi;

import java.util.Arrays;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.StudentUpdateRequest;

/**
 * Action: Edits details of a student in a course.
 */
public class UpdateStudentAction extends Action {
    /** Message indicating that the student to be edited could not be found in the system. */
    public static final String STUDENT_NOT_FOUND_FOR_EDIT = "The student you tried to edit does not exist. "
            + "If the student was created during the last few minutes, "
            + "try again in a few more minutes as the student may still be being saved.";
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
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (isCourseMigrated(courseId)) {
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(
                    instructor, sqlLogic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
        } else {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(
                    instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        if (!isCourseMigrated(courseId)) {
            return executeWithDatastore(courseId, studentEmail);
        }

        Student existingStudent = sqlLogic.getStudentForEmail(courseId, studentEmail);
        if (existingStudent == null) {
            throw new EntityNotFoundException(STUDENT_NOT_FOUND_FOR_EDIT);
        }

        StudentUpdateRequest updateRequest = getAndValidateRequestBody(StudentUpdateRequest.class);

        Course course = sqlLogic.getCourse(courseId);
        Section section = sqlLogic.getSectionOrCreate(courseId, updateRequest.getSection());
        Team team = sqlLogic.getTeamOrCreate(section, updateRequest.getTeam());
        Student studentToUpdate = new Student(course, updateRequest.getName(), updateRequest.getEmail(),
                updateRequest.getComments(), team);

        try {
            //we swap out email before we validate
            //TODO: this is duct tape at the moment, need to refactor how we do the validation
            String newEmail = studentToUpdate.getEmail();
            studentToUpdate.setEmail(existingStudent.getEmail());
            sqlLogic.validateSectionsAndTeams(Arrays.asList(studentToUpdate), courseId);
            studentToUpdate.setEmail(newEmail);

            studentToUpdate.setId(existingStudent.getId());
            Student updatedStudent = sqlLogic.updateStudentCascade(studentToUpdate);
            taskQueuer.scheduleStudentForSearchIndexing(courseId, updatedStudent.getEmail());

            if (!studentEmail.equals(updateRequest.getEmail()) && updateRequest.getIsSessionSummarySendEmail()) {
                boolean emailSent = sendEmail(courseId, updateRequest.getEmail());
                String statusMessage = emailSent ? SUCCESSFUL_UPDATE_WITH_EMAIL
                        : SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED;
                return new JsonResult(statusMessage);
            }
        } catch (EnrollException e) {
            throw new InvalidOperationException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(ERROR_EMAIL_ALREADY_EXISTS, e);
        }

        return new JsonResult(SUCCESSFUL_UPDATE);
    }

    private JsonResult executeWithDatastore(String courseId, String studentEmail)
            throws InvalidHttpRequestBodyException, InvalidOperationException {
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            throw new EntityNotFoundException(STUDENT_NOT_FOUND_FOR_EDIT);
        }

        StudentUpdateRequest updateRequest = getAndValidateRequestBody(StudentUpdateRequest.class);
        StudentAttributes studentToUpdate = StudentAttributes.builder(courseId, updateRequest.getEmail())
                .withName(updateRequest.getName())
                .withSectionName(updateRequest.getSection())
                .withTeamName(updateRequest.getTeam())
                .withComment(updateRequest.getComments())
                .build();

        try {
            //we swap out email before we validate
            //TODO: this is duct tape at the moment, need to refactor how we do the validation
            String newEmail = studentToUpdate.getEmail();
            studentToUpdate.setEmail(student.getEmail());
            logic.validateSectionsAndTeams(Arrays.asList(studentToUpdate), student.getCourse());
            studentToUpdate.setEmail(newEmail);

            StudentAttributes updatedStudent = logic.updateStudentCascade(
                    StudentAttributes.updateOptionsBuilder(courseId, studentEmail)
                            .withName(updateRequest.getName())
                            .withNewEmail(updateRequest.getEmail())
                            .withTeamName(updateRequest.getTeam())
                            .withSectionName(updateRequest.getSection())
                            .withComment(updateRequest.getComments())
                            .build());
            taskQueuer.scheduleStudentForSearchIndexing(updatedStudent.getCourse(), updatedStudent.getEmail());

            if (!student.getEmail().equals(updateRequest.getEmail())) {
                logic.resetStudentGoogleId(updateRequest.getEmail(), courseId);

                if (updateRequest.getIsSessionSummarySendEmail()) {
                    boolean emailSent = sendEmail(courseId, updateRequest.getEmail());
                    String statusMessage = emailSent ? SUCCESSFUL_UPDATE_WITH_EMAIL
                            : SUCCESSFUL_UPDATE_BUT_EMAIL_FAILED;
                    return new JsonResult(statusMessage);
                }
            }
        } catch (EnrollException e) {
            throw new InvalidOperationException(e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException("Trying to update to an email that is already in use", e);
        }

        return new JsonResult(SUCCESSFUL_UPDATE);
    }

    /**
     * Sends the feedback session summary as an email.
     *
     * @return The true if email was sent successfully or false otherwise.
     */
    private boolean sendEmail(String courseId, String studentEmail) {
        if (isCourseMigrated(courseId)) {
            EmailWrapper email = sqlEmailGenerator.generateFeedbackSessionSummaryOfCourse(
                    courseId, studentEmail, EmailType.STUDENT_EMAIL_CHANGED);
            EmailSendingStatus status = emailSender.sendEmail(email);
            return status.isSuccess();
        } else {
            EmailWrapper email = emailGenerator.generateFeedbackSessionSummaryOfCourse(
                    courseId, studentEmail, EmailType.STUDENT_EMAIL_CHANGED);
            EmailSendingStatus status = emailSender.sendEmail(email);
            return status.isSuccess();
        }
    }

}

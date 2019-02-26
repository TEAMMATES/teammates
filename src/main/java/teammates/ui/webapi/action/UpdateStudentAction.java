package teammates.ui.webapi.action;

import java.util.Arrays;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.EmailGenerator;
import teammates.ui.webapi.request.StudentUpdateRequest;

/**
 * Action: Edits details of a student in a course.
 */
public class UpdateStudentAction extends Action {
    private static final Logger log = Logger.getLogger();

    @Override
    protected AuthType getMinAuthLevel() {
        return authType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            return new JsonResult(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_EDIT, HttpStatus.SC_NOT_FOUND);
        }

        StudentUpdateRequest updateRequest = getAndValidateRequestBody(StudentUpdateRequest.class);
        boolean emailSent = false;

        try {
            logic.validateSectionsAndTeams(Arrays.asList(student), student.course);
            logic.validateTeams(Arrays.asList(student), student.course);
            logic.updateStudentCascade(
                    StudentAttributes.updateOptionsBuilder(courseId, studentEmail)
                            .withName(updateRequest.getName())
                            .withNewEmail(updateRequest.getEmail())
                            .withTeamName(updateRequest.getTeam())
                            .withSectionName(updateRequest.getSection())
                            .withComment(updateRequest.getComments())
                            .build());

            if (!student.email.equals(updateRequest.getEmail())) {
                logic.resetStudentGoogleId(updateRequest.getEmail(), courseId);
                boolean isSessionSummarySendEmail =
                        getBooleanRequestParamValue(Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK);

                if (isSessionSummarySendEmail) {
                    emailSent = sendEmail(courseId, updateRequest.getEmail());
                }
            }
        } catch (EnrollException | InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        } catch (EntityDoesNotExistException ednee) {
            return new JsonResult(ednee.getMessage(), HttpStatus.SC_NOT_FOUND);
        } catch (EntityAlreadyExistsException e) {
            return new JsonResult("Trying to update to an email that is already used",
                    HttpStatus.SC_CONFLICT);
        }

        StringBuilder responseMessage = new StringBuilder("Student has been updated");
        if (emailSent) {
            responseMessage.append(" and email sent");
        }

        return new JsonResult(responseMessage.toString());
    }

    private boolean sendEmail(String courseId, String studentEmail) {
        try {
            EmailWrapper email =
                    new EmailGenerator().generateFeedbackSessionSummaryOfCourse(courseId, studentEmail);
            emailSender.sendEmail(email);
            return true;
        } catch (Exception e) {
            log.severe("Error while sending session summary email"
                    + TeammatesException.toStringWithStackTrace(e));
            return false;
        }
    }
}

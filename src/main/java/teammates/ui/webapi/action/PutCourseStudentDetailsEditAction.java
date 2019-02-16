package teammates.ui.webapi.action;

import java.util.Arrays;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.logic.api.EmailGenerator;

/**
 * Action: Edits details of a student in a course.
 */
public class PutCourseStudentDetailsEditAction extends Action {
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
    public ActionResult execute() throws EntityNotFoundException {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);

        if (student == null) {
            return new JsonResult(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_EDIT, HttpStatus.SC_NOT_FOUND);
        }

        student.name = getRequestParamValue(Const.ParamsNames.STUDENT_NAME);
        student.email = getRequestParamValue(Const.ParamsNames.NEW_STUDENT_EMAIL);
        student.team = getRequestParamValue(Const.ParamsNames.TEAM_NAME);
        student.section = getRequestParamValue(Const.ParamsNames.SECTION_NAME);
        student.comments = getRequestParamValue(Const.ParamsNames.COMMENTS);

        student.name = SanitizationHelper.sanitizeName(student.name);
        student.email = SanitizationHelper.sanitizeEmail(student.email);
        student.team = SanitizationHelper.sanitizeName(student.team);
        student.section = SanitizationHelper.sanitizeName(student.section);
        student.comments = SanitizationHelper.sanitizeTextField(student.comments);

        try {
            StudentAttributes originalStudentAttribute = logic.getStudentForEmail(courseId, studentEmail);
            student.updateWithExistingRecord(originalStudentAttribute);

            boolean isSectionChanged = student.isSectionChanged(originalStudentAttribute);
            boolean isTeamChanged = student.isTeamChanged(originalStudentAttribute);

            if (isSectionChanged) {
                logic.validateSectionsAndTeams(Arrays.asList(student), courseId);
            } else if (isTeamChanged) {
                logic.validateTeams(Arrays.asList(student), courseId);
            }

            try {
                logic.updateStudentCascade(
                        StudentAttributes.updateOptionsBuilder(courseId, studentEmail)
                                .withName(student.name)
                                .withNewEmail(student.email)
                                .withTeamName(student.team)
                                .withSectionName(student.section)
                                .withComment(student.comments)
                                .build());
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            } catch (InvalidParametersException e) {
                return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
            } catch (EntityAlreadyExistsException e) {
                return new JsonResult("Trying to update to an email that is already used", HttpStatus.SC_BAD_REQUEST);
            }

            boolean isSessionSummarySendEmail =
                    getBooleanRequestParamValue(Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK);
            boolean isEmailChanged = student.isEmailChanged(originalStudentAttribute);
            if (isEmailChanged) {
                try {
                    logic.resetStudentGoogleId(student.email, courseId);
                } catch (EntityDoesNotExistException e) {
                    throw new EntityNotFoundException(e);
                }
                if (isSessionSummarySendEmail) {
                    try {
                        EmailWrapper email =
                                new EmailGenerator().generateFeedbackSessionSummaryOfCourse(courseId, student.email);
                        emailSender.sendEmail(email);
                    } catch (Exception e) {
                        log.severe("Error while sending session summary email"
                                + TeammatesException.toStringWithStackTrace(e));
                    }
                }
            }
            return new JsonResult(isSessionSummarySendEmail && isEmailChanged
                    ? Const.StatusMessages.STUDENT_EDITED_AND_EMAIL_SENT
                    : Const.StatusMessages.STUDENT_EDITED);

        } catch (EnrollException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

    }
}

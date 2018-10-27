package teammates.ui.newcontroller;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EmailSendingException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Action: joins a course for a student/instructor.
 */
public class JoinCourseAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    protected boolean checkSpecificAccessControl() {
        return true;
    }

    @Override
    protected ActionResult execute() {
        String regkey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        switch (entityType) {
        case Const.EntityType.STUDENT:
            return joinCourseForStudent(regkey);
        case Const.EntityType.INSTRUCTOR:
            String institute = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);
            return joinCourseForInstructor(regkey, institute);
        default:
            return new JsonResult("Error: invalid entity type", HttpStatus.SC_BAD_REQUEST);
        }
    }

    private JsonResult joinCourseForStudent(String regkey) {
        StudentAttributes student = logic.getStudentForRegistrationKey(regkey);
        if (student == null) {
            return new JsonResult("No student with given registration key: " + regkey, HttpStatus.SC_NOT_FOUND);
        }

        try {
            logic.joinCourseForStudent(regkey, userInfo.id);
        } catch (JoinCourseException | InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        sendJoinEmail(student.course, student.name, student.email, false);

        return new JsonResult("Student successfully joined course", HttpStatus.SC_OK);
    }

    private JsonResult joinCourseForInstructor(String regkey, String institute) {
        InstructorAttributes instructor = logic.getInstructorForRegistrationKey(regkey);
        if (instructor == null) {
            return new JsonResult("No instructor with given registration key: " + regkey, HttpStatus.SC_NOT_FOUND);
        }

        try {
            logic.joinCourseForInstructor(regkey, userInfo.id, institute);
        } catch (JoinCourseException | InvalidParametersException | EntityDoesNotExistException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        sendJoinEmail(instructor.courseId, instructor.name, instructor.email, true);

        return new JsonResult("Instructor successfully joined course", HttpStatus.SC_OK);
    }

    private void sendJoinEmail(String courseId, String userName, String userEmail, boolean isInstructor) {
        CourseAttributes course = logic.getCourse(courseId);
        EmailWrapper email = emailGenerator.generateUserCourseRegisteredEmail(
                userName, userEmail, userInfo.id, isInstructor, course);
        try {
            emailSender.sendEmail(email);
        } catch (EmailSendingException e) {
            log.severe("User course register email failed to send: " + TeammatesException.toStringWithStackTrace(e));
        }
    }

}

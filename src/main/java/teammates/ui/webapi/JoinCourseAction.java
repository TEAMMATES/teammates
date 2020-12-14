package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;

/**
 * Action: joins a course for a student/instructor.
 */
class JoinCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        // Any user can use a join link as long as its parameters are valid
    }

    @Override
    JsonResult execute() {
        String regKey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        switch (entityType) {
        case Const.EntityType.STUDENT:
            return joinCourseForStudent(regKey);
        case Const.EntityType.INSTRUCTOR:
            String institute = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);
            String mac = getRequestParamValue(Const.ParamsNames.INSTITUTION_MAC);
            return joinCourseForInstructor(regKey, institute, mac);
        default:
            return new JsonResult("Error: invalid entity type", HttpStatus.SC_BAD_REQUEST);
        }
    }

    private JsonResult joinCourseForStudent(String regkey) {
        StudentAttributes student;

        try {
            student = logic.joinCourseForStudent(regkey, userInfo.id);
        } catch (EntityDoesNotExistException ednee) {
            return new JsonResult(ednee.getMessage(), HttpStatus.SC_NOT_FOUND);
        } catch (EntityAlreadyExistsException eaee) {
            return new JsonResult(eaee.getMessage(), HttpStatus.SC_BAD_REQUEST);
        } catch (InvalidParametersException ipe) {
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        sendJoinEmail(student.course, student.name, student.email, false);

        return new JsonResult("Student successfully joined course", HttpStatus.SC_OK);
    }

    private JsonResult joinCourseForInstructor(String regkey, String institute, String mac) {
        InstructorAttributes instructor;

        try {
            instructor = logic.joinCourseForInstructor(regkey, userInfo.id, institute, mac);
        } catch (EntityDoesNotExistException ednee) {
            return new JsonResult(ednee.getMessage(), HttpStatus.SC_NOT_FOUND);
        } catch (EntityAlreadyExistsException eaee) {
            return new JsonResult(eaee.getMessage(), HttpStatus.SC_BAD_REQUEST);
        } catch (InvalidParametersException ipe) {
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        sendJoinEmail(instructor.courseId, instructor.name, instructor.email, true);

        return new JsonResult("Instructor successfully joined course", HttpStatus.SC_OK);
    }

    private void sendJoinEmail(String courseId, String userName, String userEmail, boolean isInstructor) {
        CourseAttributes course = logic.getCourse(courseId);
        EmailWrapper email = emailGenerator.generateUserCourseRegisteredEmail(
                userName, userEmail, userInfo.id, isInstructor, course);
        emailSender.sendEmail(email);
    }

}

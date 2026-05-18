package teammates.ui.webapi;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.InvalidOperationException;

/**
 * Action: joins a course for a student/instructor.
 */
public class JoinCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        // Any user can use a join link as long as its parameters are valid
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        String regKey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        switch (entityType) {
        case Const.EntityType.STUDENT:
            return joinCourseForStudent(regKey);
        case Const.EntityType.INSTRUCTOR:
            return joinCourseForInstructor(regKey);
        default:
            throw new InvalidHttpParameterException("Error: invalid entity type");
        }
    }

    private JsonResult joinCourseForStudent(String regkey) throws InvalidOperationException {
        Student student;

        try {
            student = logic.joinCourseForStudent(regkey, authContext.accountId());
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException(eaee);
        }

        sendJoinEmail(student.getCourseId(), student.getName(), student.getEmail(), false);

        return new JsonResult("Student successfully joined course");
    }

    private JsonResult joinCourseForInstructor(String regkey) throws InvalidOperationException {
        Instructor instructor;

        try {
            instructor = logic.joinCourseForInstructor(regkey, authContext.accountId());
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException(eaee);
        }

        sendJoinEmail(instructor.getCourseId(), instructor.getName(), instructor.getEmail(), true);

        return new JsonResult("Instructor successfully joined course");
    }

    private void sendJoinEmail(String courseId, String userName, String userEmail, boolean isInstructor) {
        Course course = logic.getCourse(courseId);
        EmailWrapper email = emailGenerator.generateUserCourseRegisteredEmail(
                userName, userEmail, authContext.id(), isInstructor, course);
        emailSender.sendEmail(email);
    }
}

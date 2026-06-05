package teammates.ui.webapi;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.request.RegKeyRequest;

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
    public JsonResult execute() throws InvalidOperationException, InvalidHttpRequestBodyException {
        RegKeyRequest requestBody = getAndValidateRequestBody(RegKeyRequest.class);
        return joinCourse(requestBody.getKey());
    }

    private JsonResult joinCourse(String regKey) throws InvalidOperationException {
        User user;

        try {
            user = logic.joinCourse(regKey, authContext.account());
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException(eaee);
        }

        sendJoinEmail(user.getCourseId(), user.getName(), user.getEmail(), user instanceof Instructor);

        return new JsonResult("User successfully joined course");
    }

    private void sendJoinEmail(String courseId, String userName, String userEmail, boolean isInstructor) {
        Course course = logic.getCourse(courseId);
        EmailWrapper email = emailGenerator.generateUserCourseRegisteredEmail(
                userName, userEmail, authContext.account().getGoogleId(), isInstructor, course);
        emailSender.sendEmail(email);
    }
}

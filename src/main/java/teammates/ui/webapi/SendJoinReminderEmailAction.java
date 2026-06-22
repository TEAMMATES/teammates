package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.User;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Send join reminder emails to register for a course.
 */
public class SendJoinReminderEmailAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID userId = getNullableUuidRequestParamValue(Const.ParamsNames.USER_ID);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (userId != null) {
            User user = logic.getUser(userId);
            if (user == null) {
                throw new EntityNotFoundException("User with ID " + userId + " does not exist.");
            }
            String targetCourseId = user.getCourseId();
            if (user instanceof Instructor) {
                gateKeeper.verifyInstructorHasPrivilege(requestContext, targetCourseId,
                        Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
            } else {
                gateKeeper.verifyInstructorHasPrivilege(requestContext, targetCourseId,
                        Const.InstructorPermissions.CAN_MODIFY_STUDENT);
            }
        }

        if (courseId != null) {
            Course course = logic.getCourse(courseId);
            if (course == null) {
                throw new EntityNotFoundException("Course with ID " + courseId + " does not exist.");
            }
            gateKeeper.verifyInstructorHasPrivilege(requestContext, course.getId(),
                    Const.InstructorPermissions.CAN_MODIFY_STUDENT);
        }
    }

    @Override
    public JsonResult execute() {
        UUID userId = getNullableUuidRequestParamValue(Const.ParamsNames.USER_ID);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        if ((userId == null) == (courseId == null)) {
            // TODO: Split this into two separate actions for better clarity and separation of concerns.
            throw new InvalidHttpParameterException(
                    "Exactly one of userId or courseId must be provided.");
        }

        try {
            // User specific reminder
            if (userId != null) {
                User user = logic.getUser(userId);
                if (user == null) {
                    throw new EntityNotFoundException("User with ID " + userId + " does not exist.");
                }
                Instructor inviter = getInstructorFromRequest(user.getCourseId());
                return new JsonResult(logic.sendJoinReminderForUser(userId, inviter));
            }
            // Course wide reminder
            return new JsonResult(logic.sendJoinReminderForStudentsInCourse(courseId));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }
}

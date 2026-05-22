package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Action: deletes a student from a course.
 */
public class DeleteStudentAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        Student student = logic.getStudent(userId);
        if (student == null) {
            throw new EntityNotFoundException("Student with user ID " + userId + " does not exist.");
        }

        if (authContext.isAdmin()) {
            return;
        }

        Instructor instructor = logic.getInstructorByGoogleId(student.getCourseId(), getCurrentUserGoogleId());
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(student.getCourseId()), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);

        logic.deleteStudentCascade(userId);

        return new JsonResult("Student is successfully deleted.");
    }

}

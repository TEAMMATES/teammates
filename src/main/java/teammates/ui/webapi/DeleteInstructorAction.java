package teammates.ui.webapi;

import java.util.List;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Deletes an instructor from a course, unless it's the last instructor in the course.
 */
public class DeleteInstructorAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        //allow access to admins or instructor with modify permission
        if (authContext.isAdmin()) {
            return;
        }

        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        Instructor instructorToDelete = logic.getInstructor(userId);
        if (instructorToDelete == null) {
            return;
        }

        Instructor instructor = logic.getInstructorByGoogleId(instructorToDelete.getCourseId(), getCurrentUserGoogleId());
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(instructorToDelete.getCourseId()),
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);
        Instructor instructor = logic.getInstructor(userId);

        if (instructor == null) {
            return new JsonResult("Instructor is successfully deleted.");
        }

        // Deleting last instructor from the course is not allowed (even by admins)
        if (!hasAlternativeInstructor(instructor.getCourseId(), instructor.getEmail())) {
            throw new InvalidOperationException(
                    "The instructor you are trying to delete is the last instructor in the course. "
                            + "Deleting the last instructor from the course is not allowed.");
        }

        logic.deleteInstructorCascade(userId);

        return new JsonResult("Instructor is successfully deleted.");
    }

    /**
     * Returns true if there is at least one joined instructor (other than the instructor to delete)
     * with the privilege of modifying instructors and at least one instructor visible to the students.
     *
     * @param courseId                Id of the course
     * @param instructorToDeleteEmail Email of the instructor who is being deleted
     */
    private boolean hasAlternativeInstructor(String courseId, String instructorToDeleteEmail) {
        List<Instructor> instructors = logic.getInstructorsByCourse(courseId);
        boolean hasAlternativeModifyInstructor = false;
        boolean hasAlternativeVisibleInstructor = false;

        for (Instructor instr : instructors) {
            hasAlternativeModifyInstructor = hasAlternativeModifyInstructor || instr.isRegistered()
                    && !SanitizationHelper.areEmailsEqual(instr.getEmail(), instructorToDeleteEmail)
                    && instr.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);

            hasAlternativeVisibleInstructor = hasAlternativeVisibleInstructor
                    || instr.isDisplayedToStudents()
                    && !SanitizationHelper.areEmailsEqual(instr.getEmail(), instructorToDeleteEmail);

            if (hasAlternativeModifyInstructor && hasAlternativeVisibleInstructor) {
                return true;
            }
        }
        return false;
    }
}

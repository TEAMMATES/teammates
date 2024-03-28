package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;

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
        if (userInfo.isAdmin) {
            return;
        }

        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Admin or Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        if (isCourseMigrated(courseId)) {
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(
                    instructor, sqlLogic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
        } else {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(
                    instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
        }
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        String instructorId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
            InstructorAttributes instructor;
            if (instructorId != null) {
                instructor = logic.getInstructorForGoogleId(courseId, instructorId);
            } else if (instructorEmail != null) {
                instructor = logic.getInstructorForEmail(courseId, instructorEmail);
            } else {
                throw new InvalidHttpParameterException("Instructor to delete not specified");
            }

            if (instructor == null) {
                return new JsonResult("Instructor is successfully deleted.");
            }

            // Deleting last instructor from the course is not allowed (even by admins)
            if (!hasAlternativeInstructorOld(courseId, instructor.getEmail())) {
                throw new InvalidOperationException(
                        "The instructor you are trying to delete is the last instructor in the course. "
                        + "Deleting the last instructor from the course is not allowed.");
            }

            logic.deleteInstructorCascade(courseId, instructor.getEmail());

            return new JsonResult("Instructor is successfully deleted.");
        }

        Instructor instructor;
        if (instructorId != null) {
            instructor = sqlLogic.getInstructorByGoogleId(courseId, instructorId);
        } else if (instructorEmail != null) {
            instructor = sqlLogic.getInstructorForEmail(courseId, instructorEmail);
        } else {
            throw new InvalidHttpParameterException("Instructor to delete not specified");
        }

        if (instructor == null) {
            return new JsonResult("Instructor is successfully deleted.");
        }

        // Deleting last instructor from the course is not allowed (even by admins)
        if (!hasAlternativeInstructor(courseId, instructor.getEmail())) {
            throw new InvalidOperationException(
                    "The instructor you are trying to delete is the last instructor in the course. "
                    + "Deleting the last instructor from the course is not allowed.");
        }

        sqlLogic.deleteInstructorCascade(courseId, instructor.getEmail());

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
        List<Instructor> instructors = sqlLogic.getInstructorsByCourse(courseId);
        boolean hasAlternativeModifyInstructor = false;
        boolean hasAlternativeVisibleInstructor = false;

        for (Instructor instr : instructors) {
            hasAlternativeModifyInstructor = hasAlternativeModifyInstructor || instr.isRegistered()
                    && !instr.getEmail().equals(instructorToDeleteEmail)
                    && instr.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);

            hasAlternativeVisibleInstructor = hasAlternativeVisibleInstructor
                    || instr.isDisplayedToStudents() && !instr.getEmail().equals(instructorToDeleteEmail);

            if (hasAlternativeModifyInstructor && hasAlternativeVisibleInstructor) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there is at least one joined instructor (other than the instructor to delete)
     * with the privilege of modifying instructors and at least one instructor visible to the students.
     * For courses that have not been migrated.
     *
     * @param courseId                Id of the course
     * @param instructorToDeleteEmail Email of the instructor who is being deleted
     */
    private boolean hasAlternativeInstructorOld(String courseId, String instructorToDeleteEmail) {
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        boolean hasAlternativeModifyInstructor = false;
        boolean hasAlternativeVisibleInstructor = false;

        for (InstructorAttributes instr : instructors) {

            hasAlternativeModifyInstructor = hasAlternativeModifyInstructor || instr.isRegistered()
                    && !instr.getEmail().equals(instructorToDeleteEmail)
                    && instr.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);

            hasAlternativeVisibleInstructor = hasAlternativeVisibleInstructor
                    || instr.isDisplayedToStudents() && !instr.getEmail().equals(instructorToDeleteEmail);

            if (hasAlternativeModifyInstructor && hasAlternativeVisibleInstructor) {
                return true;
            }
        }
        return false;
    }
}

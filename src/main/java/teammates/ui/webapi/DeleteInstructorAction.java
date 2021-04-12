package teammates.ui.webapi;

import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Deletes an instructor from a course, unless it's the last instructor in the course.
 */
class DeleteInstructorAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        //allow access to admins or instructor with modify permission
        if (userInfo.isAdmin) {
            return;
        }

        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Admin or Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    JsonResult execute() {
        String instructorId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor;
        if (instructorId != null) {
            instructor = logic.getInstructorForGoogleId(courseId, instructorId);
        } else if (instructorEmail != null) {
            instructor = logic.getInstructorForEmail(courseId, instructorEmail);
        } else {
            throw new InvalidHttpParameterException("Instructor to delete not specified");
        }
        if (instructor == null) {
            return new JsonResult("Instructor is successfully deleted.", HttpStatus.SC_OK);
        }

        // Deleting last instructor from the course is not allowed if you're not the admin
        if (!userInfo.isAdmin && !hasAlternativeInstructor(courseId, instructor.email)) {
            return new JsonResult("The instructor you are trying to delete is the last instructor in the course. "
                    + "Deleting the last instructor from the course is not allowed.", HttpStatus.SC_BAD_REQUEST);
        }

        logic.deleteInstructorCascade(courseId, instructor.email);

        return new JsonResult("Instructor is successfully deleted.", HttpStatus.SC_OK);
    }

    /**
     * Returns true if there is at least one joined instructor (other than the instructor to delete)
     * with the privilege of modifying instructors and at least one instructor visible to the students.
     *
     * @param courseId                Id of the course
     * @param instructorToDeleteEmail Email of the instructor who is being deleted
     */
    private boolean hasAlternativeInstructor(String courseId, String instructorToDeleteEmail) {
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        boolean hasAlternativeModifyInstructor = false;
        boolean hasAlternativeVisibleInstructor = false;

        for (InstructorAttributes instr : instructors) {

            hasAlternativeModifyInstructor = hasAlternativeModifyInstructor || (instr.isRegistered()
                    && !instr.getEmail().equals(instructorToDeleteEmail)
                    && instr.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));

            hasAlternativeVisibleInstructor = hasAlternativeVisibleInstructor
                    || (instr.isDisplayedToStudents() && !instr.getEmail().equals(instructorToDeleteEmail));

            if (hasAlternativeModifyInstructor && hasAlternativeVisibleInstructor) {
                return true;
            }
        }
        return false;
    }
}

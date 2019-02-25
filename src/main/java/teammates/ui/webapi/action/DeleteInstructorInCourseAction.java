package teammates.ui.webapi.action;

import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: deletes an instructor for a course by another instructor.
 */
public class DeleteInstructorInCourseAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String instructorEmail = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        if (!hasAlternativeInstructor(courseId, instructorEmail)) {
            return new JsonResult("The instructor you are trying to delete is the last instructor in the course. "
                    + "Deleting the last instructor from the course is not allowed.", HttpStatus.SC_BAD_REQUEST);
        }

        logic.deleteInstructor(courseId, instructorEmail);

        return new JsonResult("The instructor has been deleted from the course.", HttpStatus.SC_OK);
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
        boolean isAlternativeInstructorWithModifyInstructorPermissionPresent = false;
        boolean isAlternativeVisibleInstructorPresent = false;

        for (InstructorAttributes instr : instructors) {

            isAlternativeInstructorWithModifyInstructorPermissionPresent =
                    instr.isRegistered()
                            && !instr.getEmail().equals(instructorToDeleteEmail)
                            && instr.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);

            if (isAlternativeInstructorWithModifyInstructorPermissionPresent) {
                break;
            }
        }

        for (InstructorAttributes instr : instructors) {

            isAlternativeVisibleInstructorPresent = instr.isDisplayedToStudents()
                    && !instr.getEmail().equals(instructorToDeleteEmail);

            if (isAlternativeVisibleInstructorPresent) {
                break;
            }
        }

        return isAlternativeInstructorWithModifyInstructorPermissionPresent && isAlternativeVisibleInstructorPresent;
    }
}

package teammates.ui.webapi.action;

import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.request.InstructorPrivilegeUpdateRequest;

/**
 * Update instructor privilege.
 */
public class UpdateInstructorPrivilegeAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());

        if (instructor == null) {
            throw new UnauthorizedAccessException("Not instructor of the course");
        }

        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        String idOfInstructorToUpdate = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        InstructorAttributes instructorToUpdate = logic.getInstructorForGoogleId(courseId, idOfInstructorToUpdate);

        if (instructorToUpdate == null) {
            return new JsonResult("Instructor does not exist.", HttpStatus.SC_NOT_FOUND);
        }

        InstructorPrivilegeUpdateRequest request = getAndValidateRequestBody(InstructorPrivilegeUpdateRequest.class);
        String sectionName = request.getSectionName();
        String sessionName = request.getFeedbackSessionName();

        // general course level privilege for instructor
        instructorToUpdate.privileges.updatePrivilege(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, request.isCanModifyCourse());
        instructorToUpdate.privileges.updatePrivilege(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, request.isCanModifySession());
        instructorToUpdate.privileges.updatePrivilege(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, request.isCanModifyStudent());
        instructorToUpdate.privileges.updatePrivilege(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, request.isCanModifyInstructor());

        if (sectionName == null) {
            // updates course level privilege for instructor
            instructorToUpdate.privileges.updatePrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS,
                    request.isCanViewStudentInSections());
            instructorToUpdate.privileges.updatePrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                    request.isCanViewSessionInSections());
            instructorToUpdate.privileges.updatePrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                    request.isCanModifySessionCommentsInSections());
            instructorToUpdate.privileges.updatePrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                    request.isCanSubmitSessionInSections());
        } else {
            // updates section level privileges for instructor
            instructorToUpdate.privileges.updatePrivilege(
                    sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS,
                    request.isCanViewStudentInSections());

            // updates session level privileges
            if (sessionName == null) {
                instructorToUpdate.privileges.updatePrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                        request.isCanSubmitSessionInSections());
                instructorToUpdate.privileges.updatePrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                        request.isCanViewSessionInSections());
                instructorToUpdate.privileges.updatePrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                        request.isCanModifySessionCommentsInSections());
            } else {
                instructorToUpdate.privileges.updatePrivilege(sectionName, sessionName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                        request.isCanSubmitSessionInSections());
                instructorToUpdate.privileges.updatePrivilege(sectionName, sessionName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                        request.isCanViewSessionInSections());
                instructorToUpdate.privileges.updatePrivilege(sectionName, sessionName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                        request.isCanModifySessionCommentsInSections());
            }
        }

        instructorToUpdate.privileges.validatePrivileges();
        updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToUpdate);

        try {
            logic.updateInstructor(
                    InstructorAttributes
                            .updateOptionsWithEmailBuilder(instructorToUpdate.courseId, instructorToUpdate.getEmail())
                            .withPrivileges(instructorToUpdate.privileges)
                            .build());
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        } catch (EntityDoesNotExistException ednee) {
            return new JsonResult(ednee.getMessage(), HttpStatus.SC_NOT_FOUND);
        }

        return new JsonResult("The instructor " + instructorToUpdate.getName()
                + "'s privilege has been updated.", HttpStatus.SC_OK);
    }


    /**
     * Checks if there are any other registered instructors that can modify instructors.
     * If there are none, the instructor currently being edited will be granted the privilege
     * of modifying instructors automatically.
     *
     * @param courseId         Id of the course.
     * @param instructorToEdit Instructor that will be edited.
     *                         This may be modified within the method.
     */
    private void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, InstructorAttributes instructorToEdit) {
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        int numOfInstrCanModifyInstructor = 0;
        InstructorAttributes instrWithModifyInstructorPrivilege = null;
        for (InstructorAttributes instructor : instructors) {
            if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR)) {
                numOfInstrCanModifyInstructor++;
                instrWithModifyInstructorPrivilege = instructor;
            }
        }
        boolean isLastRegInstructorWithPrivilege = numOfInstrCanModifyInstructor <= 1
                && instrWithModifyInstructorPrivilege != null
                && (!instrWithModifyInstructorPrivilege.isRegistered()
                || instrWithModifyInstructorPrivilege.googleId
                .equals(instructorToEdit.googleId));
        if (isLastRegInstructorWithPrivilege) {
            instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, true);
        }
    }
}

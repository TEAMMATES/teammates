package teammates.ui.webapi.action;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.InstructorPrivilegeData;
import teammates.ui.webapi.request.InstructorPrivilegeUpdateRequest;

/**
 * Update instructor privilege by someone with instructor modify permission.
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

        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        String idOfInstructorToUpdate = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        InstructorAttributes instructorToUpdate = logic.getInstructorForGoogleId(courseId, idOfInstructorToUpdate);

        if (instructorToUpdate == null) {
            return new JsonResult("Instructor does not exist.", HttpStatus.SC_NOT_FOUND);
        }

        InstructorPrivilegeUpdateRequest request = getAndValidateRequestBody(InstructorPrivilegeUpdateRequest.class);

        String sectionName = request.getSectionName();
        String sessionName = request.getFeedbackSessionName();

        Map<String, Boolean> courseLevelPrivilegesMap = request.getAllPresentCourseLevelPriviledges();
        Map<String, Boolean> sectionLevelPrivilegesMap = request.getAllPresentSectionLevelPriviledges();
        Map<String, Boolean> sessionLevelPrivilegesMap = request.getAllPresentSessionLevelPriviledges();

        if (sectionName == null && sessionName == null) {
            updateCourseLevelPrivileges(courseLevelPrivilegesMap, instructorToUpdate);
            updateCourseLevelPrivileges(sectionLevelPrivilegesMap, instructorToUpdate);
            updateCourseLevelPrivileges(sessionLevelPrivilegesMap, instructorToUpdate);
        } else if (sessionName == null) {
            updateSectionLevelPrivileges(sectionName, sectionLevelPrivilegesMap, instructorToUpdate);
            updateSectionLevelPrivileges(sectionName, sessionLevelPrivilegesMap, instructorToUpdate);
        } else {
            updateSessionLevelPrivileges(sectionName, sessionName, sessionLevelPrivilegesMap, instructorToUpdate);
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

        InstructorPrivilegeData response = new InstructorPrivilegeData();

        response.setCanModifyInstructor(instructorToUpdate.privileges.isAllowedForPrivilege(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        response.setCanModifyStudent(instructorToUpdate.privileges.isAllowedForPrivilege(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        response.setCanModifySession(instructorToUpdate.privileges.isAllowedForPrivilege(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        response.setCanModifyCourse(instructorToUpdate.privileges.isAllowedForPrivilege(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));

        if (sessionName == null && sectionName == null) {
            response.setCanViewStudentInSections(instructorToUpdate.privileges.isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
            response.setCanViewSessionInSections(instructorToUpdate.privileges.isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
            response.setCanSubmitSessionInSections(instructorToUpdate.privileges.isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
            response.setCanModifySessionCommentsInSections(instructorToUpdate.privileges.isAllowedForPrivilege(
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        } else {
            response.setCanViewStudentInSections(instructorToUpdate.privileges.isAllowedForPrivilege(
                    sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));

            if (sessionName == null) {
                response.setCanViewSessionInSections(instructorToUpdate.privileges.isAllowedForPrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                response.setCanSubmitSessionInSections(instructorToUpdate.privileges.isAllowedForPrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
                response.setCanModifySessionCommentsInSections(instructorToUpdate.privileges.isAllowedForPrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
            } else {
                response.setCanViewSessionInSections(instructorToUpdate.privileges.isAllowedForPrivilege(
                        sectionName, sessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                response.setCanSubmitSessionInSections(instructorToUpdate.privileges.isAllowedForPrivilege(
                        sectionName, sessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
                response.setCanModifySessionCommentsInSections(
                        instructorToUpdate.privileges.isAllowedForPrivilege(sectionName, sessionName,
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
            }
        }

        return new JsonResult(response);
    }

    private void updateCourseLevelPrivileges(Map<String, Boolean> privilegesMap, InstructorAttributes toUpdate) {
        for (Map.Entry<String, Boolean> entry : privilegesMap.entrySet()) {
            toUpdate.privileges.updatePrivilege(entry.getKey(), entry.getValue());
        }
    }

    private void updateSectionLevelPrivileges(
            String sectionName, Map<String, Boolean> privilegesMap, InstructorAttributes toUpdate) {
        for (Map.Entry<String, Boolean> entry : privilegesMap.entrySet()) {
            toUpdate.privileges.updatePrivilege(sectionName, entry.getKey(), entry.getValue());
        }
    }

    private void updateSessionLevelPrivileges(
            String sectionName, String sessionName, Map<String, Boolean> privilegesMap, InstructorAttributes toUpdate) {
        for (Map.Entry<String, Boolean> entry : privilegesMap.entrySet()) {
            toUpdate.privileges.updatePrivilege(sectionName, sessionName, entry.getKey(), entry.getValue());
        }
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

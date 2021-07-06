package teammates.ui.webapi;

import java.util.Map;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.core.InstructorsLogic;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.request.InstructorPrivilegeUpdateRequest;

/**
 * Update instructor privilege by instructors with instructor modify permission.
 */
class UpdateInstructorPrivilegeAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());

        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        String emailOfInstructorToUpdate = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        InstructorAttributes instructorToUpdate = logic.getInstructorForEmail(courseId, emailOfInstructorToUpdate);

        if (instructorToUpdate == null) {
            return new JsonResult("Instructor does not exist.", HttpStatus.SC_NOT_FOUND);
        }

        InstructorPrivilegeUpdateRequest request = getAndValidateRequestBody(InstructorPrivilegeUpdateRequest.class);

        String sectionName = request.getSectionName();
        String sessionName = request.getFeedbackSessionName();

        Map<String, Boolean> courseLevelPrivilegesMap = request.getAllPresentCourseLevelPrivileges();
        Map<String, Boolean> sectionLevelPrivilegesMap = request.getAllPresentSectionLevelPrivileges();
        Map<String, Boolean> sessionLevelPrivilegesMap = request.getAllPresentSessionLevelPrivileges();

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
        InstructorsLogic.inst().updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToUpdate);

        try {
            instructorToUpdate = logic.updateInstructor(
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

        response.constructCourseLevelPrivilege(instructorToUpdate.privileges);

        if (sessionName != null) {
            response.constructSessionLevelPrivilege(instructorToUpdate.privileges, sectionName, sessionName);
        } else if (sectionName != null) {
            response.constructSectionLevelPrivilege(instructorToUpdate.privileges, sectionName);
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
}

package teammates.ui.webapi;

import java.util.Map;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.request.InstructorPrivilegeUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Update instructor privilege by instructors with instructor modify permission.
 */
class UpdateInstructorPrivilegeAction extends Action {

    private static final Logger log = Logger.getLogger();

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
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        String emailOfInstructorToUpdate = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        InstructorAttributes instructorToUpdate = logic.getInstructorForEmail(courseId, emailOfInstructorToUpdate);

        if (instructorToUpdate == null) {
            throw new EntityNotFoundException("Instructor does not exist.");
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

        instructorToUpdate.getPrivileges().validatePrivileges();
        logic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToUpdate);

        try {
            instructorToUpdate = logic.updateInstructor(
                    InstructorAttributes
                            .updateOptionsWithEmailBuilder(instructorToUpdate.getCourseId(), instructorToUpdate.getEmail())
                            .withPrivileges(instructorToUpdate.getPrivileges())
                            .build());
        } catch (InstructorUpdateException | InvalidParametersException e) {
            // Should not happen as only privilege is updated
            log.severe("Unexpected error", e);
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }

        InstructorPrivilegeData response = new InstructorPrivilegeData();

        response.constructCourseLevelPrivilege(instructorToUpdate.getPrivileges());

        if (sessionName != null) {
            response.constructSessionLevelPrivilege(instructorToUpdate.getPrivileges(), sectionName, sessionName);
        } else if (sectionName != null) {
            response.constructSectionLevelPrivilege(instructorToUpdate.getPrivileges(), sectionName);
        }

        return new JsonResult(response);
    }

    private void updateCourseLevelPrivileges(Map<String, Boolean> privilegesMap, InstructorAttributes toUpdate) {
        for (Map.Entry<String, Boolean> entry : privilegesMap.entrySet()) {
            toUpdate.getPrivileges().updatePrivilege(entry.getKey(), entry.getValue());
        }
    }

    private void updateSectionLevelPrivileges(
            String sectionName, Map<String, Boolean> privilegesMap, InstructorAttributes toUpdate) {
        for (Map.Entry<String, Boolean> entry : privilegesMap.entrySet()) {
            toUpdate.getPrivileges().updatePrivilege(sectionName, entry.getKey(), entry.getValue());
        }
    }

    private void updateSessionLevelPrivileges(
            String sectionName, String sessionName, Map<String, Boolean> privilegesMap, InstructorAttributes toUpdate) {
        for (Map.Entry<String, Boolean> entry : privilegesMap.entrySet()) {
            toUpdate.getPrivileges().updatePrivilege(sectionName, sessionName, entry.getKey(), entry.getValue());
        }
    }
}

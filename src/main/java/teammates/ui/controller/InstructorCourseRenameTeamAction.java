package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.StatusMessageColor;
import teammates.common.util.Sanitizer;
import teammates.common.util.StatusMessage;
import teammates.logic.api.GateKeeper;

public class InstructorCourseRenameTeamAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        String teamToEdit = getRequestParamValue(Const.ParamsNames.TEAM_TO_EDIT);
        String newTeamName = Sanitizer.sanitizeName(getRequestParamValue(Const.ParamsNames.TEAM_NAME));
        
        
        try {
            logic.updateTeamName(courseId, teamToEdit, newTeamName);
            statusToUser.add(new StatusMessage(Const.StatusMessages.TEAM_EDITED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Team <span class=\"bold\">" + teamToEdit + "'s</span> details in "
                            + "Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
                            + "New Team Name: " + newTeamName;
        } catch (InvalidParametersException | EnrollException | EntityAlreadyExistsException e) {
            setStatusForException(e);
        }
       
        RedirectResult response = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE);
        response.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return response;
    }

}

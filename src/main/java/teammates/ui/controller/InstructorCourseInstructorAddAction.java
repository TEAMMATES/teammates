package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.logic.api.GateKeeper;

/**
 * Action: add another instructor for an existent course of an instructor
 */
public class InstructorCourseInstructorAddAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String instructorName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME);
        Assumption.assertNotNull(instructorName);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Assumption.assertNotNull(instructorEmail);
        String instructorRole = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ROLE_NAME);
        Assumption.assertNotNull(instructorRole);
        String displayedName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME);
        displayedName = (displayedName == null || displayedName.isEmpty()) ?
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER : displayedName;
        boolean isModifyCourseChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE) != null;
        boolean isModifyInstructorChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR) != null;
        boolean isModifySessionChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) != null;
        boolean isModifyStudentChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT) != null;
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        
        InstructorAttributes instructorToAdd = constructorNewInstructor(
                courseId, instructorName, instructorEmail, instructorRole, displayedName);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, isModifyCourseChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, isModifyInstructorChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, isModifySessionChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, isModifyStudentChecked);
        instructorToAdd.instructorPrivilegesAsText = instructorToAdd.getTextFromInstructorPrivileges();
        
        /* Process adding the instructor and setup status to be shown to user and admin */
        try {
            logic.createInstructor(instructorToAdd);
            logic.sendRegistrationInviteToInstructor(courseId, instructorEmail);
            
            statusToUser.add(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED,
                    instructorName, instructorEmail));
            statusToAdmin = "New instructor (<span class=\"bold\"> " + instructorEmail + "</span>)"
                    + " for Course <span class=\"bold\">[" + courseId + "]</span> created.<br>";
        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e, Const.StatusMessages.COURSE_INSTRUCTOR_EXISTS);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
        
        RedirectResult redirectResult = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
        redirectResult.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return redirectResult;
    }

    private InstructorAttributes constructorNewInstructor(String courseId,
            String instructorName, String instructorEmail,
            String instructorRole, String displayedName) {
        instructorName = Sanitizer.sanitizeName(instructorName);
        instructorEmail = Sanitizer.sanitizeEmail(instructorEmail);
        instructorRole = Sanitizer.sanitizeName(instructorRole);
        displayedName = Sanitizer.sanitizeName(displayedName);
        InstructorPrivileges privileges = new InstructorPrivileges(instructorRole);
        InstructorAttributes instructorToAdd = new InstructorAttributes(null, courseId, instructorName, instructorEmail, instructorRole,
                displayedName, privileges);
        return instructorToAdd;
    }

}

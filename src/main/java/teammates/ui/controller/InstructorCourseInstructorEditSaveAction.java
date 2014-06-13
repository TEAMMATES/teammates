package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.logic.api.GateKeeper;

public class InstructorCourseInstructorEditSaveAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        //TODO: Allow editing of the instructors whom hasn't join the course yet
        // Need to change the corresponding UI with extra parameters in the request to have this feature
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String instructorId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        Assumption.assertNotNull(instructorId);
        String instructorName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME);
        Assumption.assertNotNull(instructorName);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Assumption.assertNotNull(instructorEmail);
        String instructorRole = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ROLE_NAME);
        Assumption.assertNotNull(instructorRole);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(instructor, logic.getCourse(courseId));

        /* Process saving editing changes and setup status to be shown to user and admin */
        InstructorAttributes instructorToEdit = updateInstructorAttributes(
                courseId, instructorId, instructorName, instructorEmail,
                instructorRole);
        
        try {
            logic.updateInstructorByGoogleId(instructorId, instructorToEdit);
            
            statusToUser.add(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED);
            statusToAdmin = "Instructor <span class=\"bold\"> " + instructorName + "</span>"
                    + " for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
                    + "New Name: " + instructorName + "<br>New Email: " + instructorEmail;
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
        
        /* Create redirection to 'Edit' page with corresponding course id */
        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
        result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return result;
    }

    private InstructorAttributes updateInstructorAttributes(String courseId,
            String instructorId, String instructorName, String instructorEmail,
            String instructorRole) {
        InstructorAttributes instructorToEdit = logic.getInstructorForGoogleId(courseId, instructorId);
        instructorToEdit.name = Sanitizer.sanitizeName(instructorName);
        instructorToEdit.email = Sanitizer.sanitizeEmail(instructorEmail);
        instructorToEdit.role = Sanitizer.sanitizeName(instructorRole);
        // TODO: remove this hard-coded thing!
        instructorToEdit.displayedName = "Co-owner";
        instructorToEdit.privileges = new InstructorPrivileges(instructorRole);
        instructorToEdit.instructorPrivilegesAsText = instructorToEdit.getTextFromInstructorPrivileges();
        return instructorToEdit;
    }
}

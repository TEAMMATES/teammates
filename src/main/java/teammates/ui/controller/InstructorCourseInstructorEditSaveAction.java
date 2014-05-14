package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
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
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));

        /* Process saving editing changes and setup status to be shown to user and admin */
        InstructorAttributes instructorToEdit = logic.getInstructorForGoogleId(courseId, instructorId);
        instructorToEdit.name = Sanitizer.sanitizeName(instructorName);
        instructorToEdit.email = Sanitizer.sanitizeEmail(instructorEmail);
        
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
}

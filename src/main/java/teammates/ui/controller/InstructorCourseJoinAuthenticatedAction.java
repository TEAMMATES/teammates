package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

/**
 * This action handles instructors who attempt to join a course after
 * the instructor has been forced to re-authenticate himself by 
 * {@link InstructorCourseJoinAction}. This action does the actual
 * joining of the instructor to the course.
 */
public class InstructorCourseJoinAuthenticatedAction extends Action {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        //TODO Remove excessive logging from this method
        String key = getRequestParamValue(Const.ParamsNames.REGKEY);
        Assumption.assertNotNull(key);
        
        new GateKeeper().verifyLoggedInUserPrivileges();
        
        String logMsg = null;
        try {
            logic.joinCourseForInstructor(key, account.googleId);
        } catch (InvalidParametersException
                | EntityAlreadyExistsException e) {
            setStatusForException(e, Sanitizer.sanitizeForHtml(e.getMessage()));
            logMsg = "GAE-level exception not thrown explicitly by Logic <br/>" + e.toString();
        } catch (JoinCourseException e) {
            // Does not sanitize for html to allow insertion of mailto link
            setStatusForException(e, e.getMessage());
            
            InstructorAttributes instructor = logic.getInstructorForRegistrationKey(key);
            if (instructor != null) {
                logMsg = "Instructor object for key exists.<br/>Instructor object information:"
                    + "<br/>Course: " + instructor.courseId
                    + "<br/>Name: " + instructor.name 
                    + "<br/>Email: " + instructor.email
                    + "<br/>Id: " + instructor.googleId;
            } else {
                logMsg = "Instructor object for key not found.";
            }
        }
        
        final String joinedCourseMsg = "Action Instructor Joins Course"
                + "<br/>Google ID: " + account.googleId
                + "<br/>Key : " + StringHelper.decrypt(key); 
        if(statusToAdmin != null && !statusToAdmin.trim().isEmpty()) {
            statusToAdmin += "<br/><br/>" + joinedCourseMsg;
        } else {
            statusToAdmin = joinedCourseMsg;
        }
        
        if(logMsg != null){
            log.info(logMsg);
        }
        
        RedirectResult response = createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        
        InstructorAttributes instructor  = logic.getInstructorForRegistrationKey(key);
        if(instructor != null) {
            response.addResponseParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, instructor.courseId);    
        }
        
        return response;
    }
}

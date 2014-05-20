package teammates.ui.controller;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.logic.api.GateKeeper;

/**
 * This action handles students who attempt to join a course after
 * the student has been forced to re-authenticate himself by 
 * {@link StudentCourseJoinAction}. This action does the actual
 * joining of the student to the course.
 */
public class StudentCourseJoinAuthenticatedAction extends Action {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        //TODO Remove excessive logging from this method
        String key = getRequestParamValue(Const.ParamsNames.REGKEY);
        Assumption.assertNotNull(key);

        new GateKeeper().verifyLoggedInUserPrivileges();
        
        String logMsg = null;
        try {
            logic.joinCourseForStudent(key, account.googleId);
        } catch (InvalidParametersException
                | EntityAlreadyExistsException e) {
            setStatusForException(e, Sanitizer.sanitizeForHtml(e.getMessage()));
            //logMsg = "GAE-level exception not thrown explicitly by Logic <br/>" + e.toString();
            
            //TODO: this branch seems to be unreachable, to be removed  
        } catch (JoinCourseException e) {
            // Does not sanitize for html to allow insertion of mailto link
            setStatusForException(e, e.getMessage());
            
            StudentAttributes student = logic.getStudentForRegistrationKey(key);
            if (student != null) {
                logMsg = "Student object for key exists.<br/>Student object information:"
                    + "<br/>Course: " + student.course
                    + "<br/>Name: " + student.name 
                    + "<br/>Email: " + student.email
                    + "<br/>Id: " + student.googleId;
            } else {
                logMsg = "Student object for key not found.";
            }
        }
        
        final String studentInfo = "Action Student Joins Course"
                + "<br/>Google ID: " + account.googleId
                + "<br/>Key : " + key; 
        if(statusToAdmin != null && !statusToAdmin.trim().isEmpty()) {
            statusToAdmin += "<br/><br/>" + studentInfo;
        } else {
            statusToAdmin = studentInfo;
        }
        
        if(logMsg != null){
            log.info(logMsg);
        }
        
        RedirectResult response = createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
        
        StudentAttributes student  = logic.getStudentForRegistrationKey(key);
        if(student != null) {
            response.addResponseParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, student.course);    
        }
        
        return response;
    }
}

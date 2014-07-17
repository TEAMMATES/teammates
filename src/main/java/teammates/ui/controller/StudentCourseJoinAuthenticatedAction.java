package teammates.ui.controller;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.JoinCourseException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

/**
 * This action handles students who attempt to join a course after
 * the student has been forced to re-authenticate himself by 
 * {@link StudentCourseJoinAction}. This action does the actual
 * joining of the student to the course.
 */
public class StudentCourseJoinAuthenticatedAction extends Action {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        Assumption.assertNotNull(regkey);
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
        Assumption.assertNotNull(nextUrl);
        
        ensureStudentExists();
        
        try {
            logic.joinCourseForStudent(regkey, account.googleId);
        } catch (JoinCourseException e) {
            // Does not sanitize for html to allow insertion of mailto link
            setStatusForException(e, e.getMessage());
        }
        
        final String studentInfo = "Action Student Joins Course"
                + "<br/>Google ID: " + account.googleId
                + "<br/>Key : " + regkey; 
        if(statusToAdmin != null && !statusToAdmin.trim().isEmpty()) {
            statusToAdmin += "<br/><br/>" + studentInfo;
        } else {
            statusToAdmin = studentInfo;
        }
        
        RedirectResult response = createRedirectResult(nextUrl);
        response.addResponseParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, getStudent().course);    
        
        return response;
    }

    private void ensureStudentExists() {
        StudentAttributes student = getStudent();
        if (student == null) {
            log.info("Student object not found for regkey: " + regkey);
            throw new UnauthorizedAccessException("Invalid regkey given: " + regkey);
        }
    }

    private StudentAttributes getStudent() {
        if (student == null) {
            student = logic.getStudentForRegistrationKey(regkey);
        }
        
        return student;
    }
    
    
}

package teammates.ui.controller;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Url;

/**
 * This action handles students that attempts to join a course.
 * It asks the student for confirmation that the logged in account 
 * belongs to him before redirecting him to the actual join action, 
 * {@link StudentCourseJoinAuthenticatedAction}.
 * <br/><br/>
 * This is done to prevent students from accidentally linking 
 * his registration key with another student's google account.
 */
public class StudentCourseJoinAction extends Action {
    
    private StudentCourseJoinConfirmationPageData data;
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        Assumption.assertPostParamNotNull(Const.ParamsNames.REGKEY, regkey);
        String nextUrl = getNextUrl();
        
        statusToAdmin = "Action Student Clicked Join Link"
                + account.googleId == null ? "<br/>Email: " + account.email   
                        : "<br/>Google ID: " + account.googleId
                + "<br/>Key: " + regkey;
        
        student = student == null ? logic.getStudentForRegistrationKey(regkey) : student;
        
        if (student == null) {
            throw new UnauthorizedAccessException("No student with given registration key:" + regkey);
        } else if (student.isRegistered()) {
            // this branch only means that the user is already registered as the other cases:
            // mismatch of regkey / regkey used by another user are handled by authentication
            // in Action.java during initialisation
            statusToUser.add("You are already a student of Course: " + student.course);
            return createRedirectToNextUrl(student, nextUrl);
        }
        
        if (logic.getCurrentUser() == null) {
            return createRedirectToAuthenticatedJoinPage(nextUrl);
        }
        
        data = new StudentCourseJoinConfirmationPageData(account, regkey, nextUrl);
        
        return createShowPageResult(
                Const.ViewURIs.STUDENT_COURSE_JOIN_CONFIRMATION, data);
    }

    protected String getNextUrl() {
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
        if (nextUrl == null) {
            nextUrl = Const.ActionURIs.STUDENT_HOME_PAGE;
        }
        
        return nextUrl;
    }

    protected ActionResult createRedirectToAuthenticatedJoinPage(String nextUrl) {
        // send straight to next page as the user can choose to login as he wishes
        String redirectUrl = Url.addParamToUrl(
                Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED,
                Const.ParamsNames.REGKEY, regkey);
        
        redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.NEXT_URL, nextUrl);
        
        return createRedirectResult(redirectUrl);
    }

    protected ActionResult createRedirectToNextUrl(StudentAttributes student, String nextUrl) {

        // Redirect to given url if user is already registered
        log.info("User already registered as student in course: " + student.course);
        
        String redirectUrl = Url.addParamToUrl(
                nextUrl, Const.ParamsNames.REGKEY, regkey);
        
        return createRedirectResult(redirectUrl);
    }
}

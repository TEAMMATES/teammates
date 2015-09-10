package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.logic.api.Logic;

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
                        + (account.googleId == null ? "<br/>Email: " + account.email   
                                                    : "<br/>Google ID: " + account.googleId + "<br/>Key: " + regkey);
        
        if (logic.getCurrentUser() == null) {
            return createRedirectToAuthenticatedJoinPage(nextUrl);
        }
        
        String confirmUrl = Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED 
                + "?" + Const.ParamsNames.REGKEY + "=" + regkey 
                + "&" + Const.ParamsNames.NEXT_URL + "=" + nextUrl;
        data = new StudentCourseJoinConfirmationPageData(account, student, confirmUrl, Logic.getLogoutUrl(confirmUrl));
        excludeStudentDetailsFromResponseParams();
        
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
        String redirectUrl = new Url(Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED)
                .withRegistrationKey(regkey)
                .withParam(Const.ParamsNames.NEXT_URL, nextUrl)
                .toString();
        
        excludeStudentDetailsFromResponseParams();
        
        return createRedirectResult(redirectUrl);
    }
}

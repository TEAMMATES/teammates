package teammates.ui.controller;

import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.ui.pagedata.StudentCourseJoinConfirmationPageData;

/**
 * This action handles students that attempts to join a course.
 * It asks the student for confirmation that the logged in account
 * belongs to him before redirecting him to the actual join action,
 * {@link StudentCourseJoinAuthenticatedAction}.
 * <br><br>
 * This is done to prevent students from accidentally linking
 * his registration key with another student's google account.
 */
public class StudentCourseJoinAction extends Action {
    
    @Override
    public ActionResult execute() {
        Assumption.assertPostParamNotNull(Const.ParamsNames.REGKEY, regkey);
        String nextUrl = getNextUrl();
        
        statusToAdmin = "Action Student Clicked Join Link"
                        + (account.googleId == null ? "<br>Email: " + account.email
                                                    : "<br>Google ID: " + account.googleId + "<br>Key: " + regkey);
        
        if (gateKeeper.getCurrentUser() == null) {
            return createRedirectToAuthenticatedJoinPage(nextUrl);
        }
        
        String confirmUrl = Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED
                + "?" + Const.ParamsNames.REGKEY + "=" + regkey
                + "&" + Const.ParamsNames.NEXT_URL + "=" + Sanitizer.sanitizeForNextUrl(nextUrl);
        String nextUrlType = getPageTypeOfUrl(nextUrl);
        // the student is redirected to join page because he/she is not registered in the course
        boolean isRedirectResult = !Const.SystemParams.PAGES_ACCESSIBLE_WITHOUT_REGISTRATION.contains(nextUrlType);
        boolean isNextUrlAccessibleWithoutLogin =
                        Const.SystemParams.PAGES_ACCESSIBLE_WITHOUT_GOOGLE_LOGIN.contains(nextUrlType);
        String courseId = student.course;
        StudentCourseJoinConfirmationPageData data = new StudentCourseJoinConfirmationPageData(account, student, confirmUrl,
                                                         gateKeeper.getLogoutUrl(Sanitizer.sanitizeForNextUrl(confirmUrl)),
                                                         isRedirectResult, courseId, isNextUrlAccessibleWithoutLogin);
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
        String redirectUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED)
                .withRegistrationKey(regkey)
                .withParam(Const.ParamsNames.NEXT_URL, nextUrl)
                .toString();
        
        excludeStudentDetailsFromResponseParams();
        
        return createRedirectResult(redirectUrl);
    }
    
    /**
     * Gets the page type out of a URL, e.g the type of
     * <code>/page/xyz?param1=value1&amp;param2=value2</code> is <code>/page/xyz</code>.
     * The page type is assumed to be in the form of /page/ followed by alphabets
     * (case-insensitive) only, as per the design of {@link Const.ActionURIs}.
     */
    public static String getPageTypeOfUrl(String url) {
        /* 
         * Regex meaning: from the beginning of the string, tries to match /page/
         * followed by one or more case-insensitive alphabets, followed by ? and
         * any amount of any character until the end of the string.
         * Returns everything before ? if matches or the original string otherwise.
         */
        return url.replaceFirst("^(/page/[A-Za-z]+)\\?.*$", "$1");
    }
    
}

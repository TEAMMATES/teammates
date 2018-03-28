package teammates.ui.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.ui.pagedata.StudentProfilePageData;

/**
 * Action: showing the profile page for a student in a course.
 */
public class StudentProfilePageAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() {
        account.studentProfile = logic.getStudentProfile(account.googleId);
        String isEditingPhoto = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_PHOTOEDIT);
        if (isEditingPhoto == null) {
            isEditingPhoto = "false";
        }

        if (account.studentProfile == null) {
            log.severe("Student Profile returned as null for " + account.toString());
            return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
        }

        String whole_message = SanitizationHelper.sanitizeForHtmlTag(account.studentProfile.toString());
        String part_of_message = null;
        String googleId = null;
        String pattern = "(googleId\")(.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(whole_message);
        if (m.find()) {
            part_of_message = m.group(2);
            Pattern ptrn = Pattern.compile("\\B(:.*?\")\\B");
            Matcher matcher = ptrn.matcher(part_of_message);
            if (matcher.find()) {
                googleId = matcher.group();
                googleId = googleId.substring(3, googleId.length() - 1);
            }
        }

        StudentProfilePageData data = new StudentProfilePageData(account, sessionToken, isEditingPhoto);
        statusToAdmin = "studentProfile Page Load <br> Profile: "
                + googleId;

        return createShowPageResult(Const.ViewURIs.STUDENT_PROFILE_PAGE, data);
    }

}

package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.Logger;
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

        StudentProfilePageData data = new StudentProfilePageData(account, sessionToken, isEditingPhoto);
        statusToAdmin = "studentProfile Page Load <br> Profile: " + account.googleId;

        return createShowPageResult(Const.ViewURIs.STUDENT_PROFILE_PAGE, data);
    }

}

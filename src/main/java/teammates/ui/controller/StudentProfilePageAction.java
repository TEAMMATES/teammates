package teammates.ui.controller;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.ui.pagedata.StudentProfilePageData;

/**
 * Action: showing the profile page for a student in a course.
 */
public class StudentProfilePageAction extends Action {

    @Override
    protected ActionResult execute() {
        StudentProfileAttributes spa = logic.getStudentProfile(account.googleId);
        if (spa == null) {
            // create one on the fly
            spa = StudentProfileAttributes.builder(account.googleId).build();
        }
        String isEditingPhoto = getRequestParamValue(Const.ParamsNames.STUDENT_PROFILE_PHOTOEDIT);
        if (isEditingPhoto == null) {
            isEditingPhoto = "false";
        }

        StudentProfilePageData data = new StudentProfilePageData(account, spa, sessionToken, isEditingPhoto);
        statusToAdmin = "studentProfile Page Load <br> Profile: " + account.googleId;

        return createShowPageResult(Const.ViewURIs.STUDENT_PROFILE_PAGE, data);
    }

}

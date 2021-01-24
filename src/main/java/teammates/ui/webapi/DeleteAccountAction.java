package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;

/**
 * Action: deletes an existing account (either student or instructor).
 */
class DeleteAccountAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        String instructorId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        StudentProfileAttributes studentProfileAttributes = logic.getStudentProfile(Const.ParamsNames.INSTRUCTOR_ID);
        if (studentProfileAttributes != null && !studentProfileAttributes.pictureKey.equals("")) {
            fileStorage.delete(studentProfileAttributes.pictureKey);
        }
        logic.deleteAccountCascade(instructorId);
        return new JsonResult("Account is successfully deleted.", HttpStatus.SC_OK);
    }

}

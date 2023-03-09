package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * Action: deletes an existing account (either student or instructor).
 */
class DeleteAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        AccountAttributes accountInfo = logic.getAccount(googleId);

        if (accountInfo == null || accountInfo.isMigrated()) {
            List<Instructor> instructorsToDelete = sqlLogic.getInstructorsByGoogleId(googleId);

            for (Instructor instructor : instructorsToDelete) {
                sqlLogic.deleteUser(instructor);
            }

            List<Student> studentsToDelete = sqlLogic.getStudentsByGoogleId(googleId);

            for (Student student : studentsToDelete) {
                sqlLogic.deleteUser(student);
            }

            sqlLogic.deleteAccount(googleId);
        } else {
            logic.deleteAccountCascade(googleId);
        }

        return new JsonResult("Account is successfully deleted.");
    }

}

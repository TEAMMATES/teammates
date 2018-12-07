package teammates.ui.controller;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.AdminStudentGoogleIdResetPageData;

/**
 * This Action is used in AdminSearchPage to reset the google id of a
 * registered student in the searched results. Selected student in a
 * specific course will have its google id attribute reset to null.
 * Reset is done through Ajax and once the reset is successfully completed,
 * an notification will be sent to the original email address associated with the student.
 */
public class AdminStudentGoogleIdResetAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        gateKeeper.verifyAdminPrivileges(account);

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String studentCourseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String wrongGoogleId = getRequestParamValue(Const.ParamsNames.STUDENT_ID);

        AdminStudentGoogleIdResetPageData data = new AdminStudentGoogleIdResetPageData(account, sessionToken);

        if (studentEmail != null && studentCourseId != null) {
            try {
                logic.resetStudentGoogleId(studentEmail, studentCourseId);
                taskQueuer.scheduleCourseRegistrationInviteToStudent(studentCourseId, studentEmail, true);
            } catch (InvalidParametersException e) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_GOOGLEID_RESET_FAIL,
                                                   StatusMessageColor.DANGER));
                statusToAdmin = Const.StatusMessages.STUDENT_GOOGLEID_RESET_FAIL + "<br>"
                              + "Email: " + studentEmail + "<br>"
                              + "CourseId: " + studentCourseId + "<br>"
                              + "Failed with error<br>"
                              + e.getMessage();
                isError = true;
            }

            StudentAttributes updatedStudent = logic.getStudentForEmail(studentCourseId, studentEmail);

            if (updatedStudent.googleId == null || updatedStudent.googleId.isEmpty()) {

                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_GOOGLEID_RESET, StatusMessageColor.SUCCESS));
                statusToUser.add(new StatusMessage("Email : " + studentEmail, StatusMessageColor.SUCCESS));
                statusToUser.add(new StatusMessage("CourseId : " + studentCourseId, StatusMessageColor.SUCCESS));

                statusToAdmin = Const.StatusMessages.STUDENT_GOOGLEID_RESET + "<br>"
                              + "Email: " + studentEmail + "<br>"
                              + "CourseId: " + studentCourseId;

                data.statusForAjax = Const.StatusMessages.STUDENT_GOOGLEID_RESET + "<br>"
                                   + "Email : " + studentEmail + "<br>"
                                   + "CourseId : " + studentCourseId;

                data.isGoogleIdReset = true;
                deleteAccountIfNeeded(wrongGoogleId);
            } else {
                data.isGoogleIdReset = false;
                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_GOOGLEID_RESET_FAIL,
                                                   StatusMessageColor.DANGER));
                statusToAdmin = Const.StatusMessages.STUDENT_GOOGLEID_RESET_FAIL + "<br>"
                              + "Email: " + studentEmail + "<br>"
                              + "CourseId: " + studentCourseId + "<br>";
                data.statusForAjax = Const.StatusMessages.STUDENT_GOOGLEID_RESET_FAIL + "<br>"
                                   + "Email : " + studentEmail + "<br>"
                                   + "CourseId : " + studentCourseId;
            }

            isError = false;
            return createAjaxResult(data);
        }

        isError = true;
        return createAjaxResult(data);
    }

    private void deleteAccountIfNeeded(String wrongGoogleId) {
        if (logic.getStudentsForGoogleId(wrongGoogleId).isEmpty()
                && logic.getInstructorsForGoogleId(wrongGoogleId).isEmpty()) {
            logic.deleteAccount(wrongGoogleId);
        }
    }
}

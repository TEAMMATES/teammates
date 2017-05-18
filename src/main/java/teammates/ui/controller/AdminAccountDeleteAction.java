package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class AdminAccountDeleteAction extends Action {

    @Override
    protected ActionResult execute() {

        gateKeeper.verifyAdminPrivileges(account);

        String instructorId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String account = getRequestParamValue("account");

        //TODO: We should extract these into separate actions e.g., AdminInstructorDowngradeAction
        if (courseId == null && account == null) {
            //delete instructor status
            try {
                logic.downgradeInstructorToStudentCascade(instructorId);
                statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_STATUS_DELETED,
                                                   StatusMessageColor.SUCCESS));
                statusToAdmin = "Instructor Status for <span class=\"bold\">" + instructorId + "</span> has been deleted.";
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                this.setStatusForException(e);
            }
            return createRedirectResult(Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE);
        }

        if (courseId == null && account != null) {
            //delete entire account
            try {
                logic.deleteAccount(instructorId);
                statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_ACCOUNT_DELETED,
                                                   StatusMessageColor.SUCCESS));
                statusToAdmin = "Instructor Account for <span class=\"bold\">" + instructorId + "</span> has been deleted.";
            } catch (InvalidParametersException | EntityDoesNotExistException e) {
                setStatusForException(e);
            }
            return createRedirectResult(Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE);
        }

        String studentId = getRequestParamValue(Const.ParamsNames.STUDENT_ID);
        if (courseId != null && studentId != null) {
            //remove student from course
            StudentAttributes student = logic.getStudentForGoogleId(courseId, studentId);
            try {
                logic.deleteStudent(courseId, student.email);
                statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_DELETED,
                                                   StatusMessageColor.SUCCESS));
                statusToAdmin = "Instructor <span class=\"bold\">" + instructorId
                                + "</span>'s student status in Course"
                                + "<span class=\"bold\">[" + courseId + "]</span> has been deleted";
            } catch (InvalidParametersException e) {
                setStatusForException(e);
            }
            return createRedirectResult(Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE + "?instructorid=" + studentId);
        }

        //remove instructor from course
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, instructorId);
        try {
            logic.deleteInstructor(courseId, instructor.email);
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_REMOVED_FROM_COURSE,
                                               StatusMessageColor.SUCCESS));
            statusToAdmin = "Instructor <span class=\"bold\">" + instructorId
                          + "</span> has been deleted from Course<span class=\"bold\">[" + courseId + "]</span>";
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            setStatusForException(e);
        }

        return createRedirectResult(Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE + "?instructorid=" + instructorId);
    }

}

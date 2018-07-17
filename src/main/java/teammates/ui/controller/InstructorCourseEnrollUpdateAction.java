package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

/**
 * Action: saving the list of edited students for a course of an instructor.
 */
public class InstructorCourseEnrollUpdateAction extends Action {

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        String updatedStudentsInfo = getRequestParamValue(Const.ParamsNames.STUDENTS_UPDATED_INFO).trim();
        String sanitizedUpdatedStudentsInfo = SanitizationHelper.sanitizeForHtml(updatedStudentsInfo); // for admin message
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENTS_UPDATED_INFO, updatedStudentsInfo);

        boolean isSessionSummarySendEmail = getRequestParamAsBoolean(Const.ParamsNames.SESSION_SUMMARY_EMAIL_SEND_CHECK);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        statusToUser.add(new StatusMessage(isSessionSummarySendEmail
                ? Const.StatusMessages.STUDENT_UPDATED_AND_EMAIL_SENT
                : Const.StatusMessages.STUDENT_UPDATED, StatusMessageColor.SUCCESS));

        statusToAdmin = "Students Updated in Course <span class=\"bold\">["
                + courseId + "]:</span><br>" + sanitizedUpdatedStudentsInfo.replace("\n", "<br>");

        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE);
        result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return result;
    }
}

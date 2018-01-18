package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorCourseStudentDetailsEditPageData;

public class InstructorCourseStudentDetailsEditPageAction extends Action {

    @Override
    public ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENT_EMAIL, studentEmail);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);

        if (student == null) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENT_NOT_FOUND_FOR_EDIT,
                                               StatusMessageColor.DANGER));
            isError = true;
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        }

        boolean hasSection = logic.hasIndicatedSections(courseId);
        boolean isOpenOrPublishedEmailSentForTheCourse = logic.isOpenOrPublishedEmailSentForTheCourse(courseId);

        InstructorCourseStudentDetailsEditPageData data =
                new InstructorCourseStudentDetailsEditPageData(account, sessionToken, student, student.email, hasSection,
                        isOpenOrPublishedEmailSentForTheCourse);

        statusToAdmin = "instructorCourseStudentEdit Page Load<br>"
                        + "Editing Student <span class=\"bold\">" + studentEmail + "'s</span> details "
                        + "in Course <span class=\"bold\">[" + courseId + "]</span>";

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_STUDENT_EDIT, data);

    }
}

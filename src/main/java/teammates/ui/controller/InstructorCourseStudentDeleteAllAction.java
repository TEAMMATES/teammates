package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorCourseStudentDeleteAllAction extends Action {

    @Override
    public ActionResult execute() {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        logic.deleteAllStudentsInCourse(courseId);
        statusToUser.add(new StatusMessage(Const.StatusMessages.STUDENTS_DELETED, StatusMessageColor.SUCCESS));
        statusToAdmin = "All the Students "
                + "in Course <span class=\"bold\">[" + courseId + "]</span> are deleted.";

        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE);
        result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return result;

    }

}

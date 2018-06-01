package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorCourseEnrollAjaxPageData;

/**
 * Action: returns list of current students in the course through AJAX.
 */
public class InstructorCourseEnrollAjaxPageAction extends Action{

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String user = getRequestParamValue(Const.ParamsNames.USER_ID);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.USER_ID, user);

        InstructorCourseEnrollAjaxPageData data = new InstructorCourseEnrollAjaxPageData(
                logic.getAccount(user), sessionToken, logic.getStudentsForCourse(courseId));

        return createAjaxResult(data);
    }
}

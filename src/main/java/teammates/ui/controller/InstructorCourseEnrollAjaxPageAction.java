package teammates.ui.controller;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorCourseEnrollAjaxPageData;

/**
 * Action: returns list of current students in the course through AJAX.
 */
public class InstructorCourseEnrollAjaxPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        gateKeeper.verifyInstructorPrivileges(account);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        CourseAttributes course = logic.getCourse(courseId);

        gateKeeper.verifyAccessible(instructor, course);

        InstructorCourseEnrollAjaxPageData data = new InstructorCourseEnrollAjaxPageData(
                account, sessionToken, logic.getStudentsForCourse(courseId));

        return createAjaxResult(data);
    }
}

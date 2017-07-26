package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorCourseStudentListPageData;

public class InstructorCourseStudentListAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String user = getRequestParamValue(Const.ParamsNames.USER_ID);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.USER_ID, user);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));

        InstructorCourseStudentListPageData data =
                            new InstructorCourseStudentListPageData(logic.getAccount(user), sessionToken,
                                                logic.getStudentsForCourse(courseId));

        return createAjaxResult(data);
    }
}

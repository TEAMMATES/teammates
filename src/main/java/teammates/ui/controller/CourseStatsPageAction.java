package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.CourseStatsPageData;

public class CourseStatsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);

        CourseStatsPageData data = new CourseStatsPageData(account);

        gateKeeper.verifyInstructorPrivileges(account);

        data.courseDetails = logic.getCourseDetails(courseId);

        return createAjaxResult(data);
    }
}

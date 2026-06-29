package teammates.ui.webapi;

import teammates.common.datatransfer.InstructorQuery;
import teammates.common.util.Const;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorsData;

/**
 * Get a full-detail list of instructors of a course.
 */
public class GetInstructorsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            // Only admins can access the full list of instructors without a course ID.
            return;
        }

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyInstructorInCourse(requestContext, courseId);
    }

    @Override
    public JsonResult execute() {
        InstructorQuery query = new InstructorQuery(
                getRequestParamValue(Const.ParamsNames.COURSE_ID),
                getRequestParamValue(Const.ParamsNames.SEARCH_KEY),
                getLimitParamValue());

        return new JsonResult(new InstructorsData(logic.getInstructors(query)));
    }

}

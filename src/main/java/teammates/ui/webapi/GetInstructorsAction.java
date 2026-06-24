package teammates.ui.webapi;

import teammates.common.datatransfer.InstructorQuery;
import teammates.common.util.Const;
import teammates.ui.exception.InvalidHttpParameterException;
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
                getNullablePositiveIntRequestParamValue(Const.ParamsNames.LIMIT));

        return new JsonResult(new InstructorsData(logic.getInstructors(query)));
    }

    private Integer getNullablePositiveIntRequestParamValue(String paramName) {
        String value = getRequestParamValue(paramName);
        if (value == null) {
            return null;
        }

        int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(
                    "Expected integer value for " + paramName + " parameter, but found: [" + value + "]", e);
        }
        if (parsed <= 0) {
            throw new InvalidHttpParameterException(
                    "Expected positive integer value for " + paramName + " parameter, but found: [" + value + "]");
        }
        return parsed;
    }
}

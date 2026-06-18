package teammates.ui.webapi;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorCoursesData;

/**
 * Gets all courses for the logged-in instructor, filtered by active or soft-deleted status.
 */
public class GetInstructorCoursesAction extends LoggedInAction {

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Courses are filtered to those where the user is an instructor.
    }

    @Override
    public JsonResult execute() {
        String courseStatus = getNonNullRequestParamValue(Const.ParamsNames.COURSE_STATUS);
        try {
            return new JsonResult(new InstructorCoursesData(
                    logic.getCoursesForInstructorAccount(requestContext.getAccount().getId(), courseStatus)));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpParameterException(e);
        }
    }
}

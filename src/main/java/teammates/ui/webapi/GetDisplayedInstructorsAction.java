package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;

/**
 * Get a student-safe list of instructors of a course.
 */
public class GetDisplayedInstructorsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyUserInCourse(requestContext, courseId);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorsData instructorsData = new InstructorsData(logic.getDisplayedInstructorsByCourse(courseId));
        for (InstructorData instructor : instructorsData.getInstructors()) {
            instructor.hideInformationForStudent();
        }

        return new JsonResult(instructorsData);
    }
}

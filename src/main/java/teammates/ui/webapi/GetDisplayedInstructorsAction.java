package teammates.ui.webapi;

import java.util.List;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
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
        List<Instructor> instructorsOfCourse = logic.getInstructorsByCourse(courseId).stream()
                .filter(Instructor::isDisplayedToStudents)
                .toList();

        InstructorsData data = new InstructorsData(instructorsOfCourse);
        data.getInstructors().forEach(InstructorData::hideInformationForStudent);

        return new JsonResult(data);
    }
}

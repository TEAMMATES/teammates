package teammates.ui.webapi;

import java.util.List;

import teammates.storage.entity.Course;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CoursesData;

/**
 * Gets all courses for the logged-in student.
 */
public class GetStudentCoursesAction extends LoggedInAction {

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        // Courses are filtered to those where the user is a student.
    }

    @Override
    public JsonResult execute() {
        List<Course> courses = logic.getCoursesForStudentAccount(requestContext.getAccount());
        return new JsonResult(new CoursesData(courses));
    }
}

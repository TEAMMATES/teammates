package teammates.ui.webapi;

import java.util.List;

import teammates.common.util.Const;
import teammates.storage.entity.Student;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.StudentsData;

/**
 * Get a list of students.
 */
public class GetStudentsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        gateKeeper.verifyInstructorHasPrivilege(requestContext, courseId,
                Const.InstructorPermissions.CAN_VIEW_STUDENT);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        List<Student> students = requestContext.isAdmin()
                ? logic.getStudentsForCourse(courseId)
                : logic.getStudentsVisibleToInstructor(courseId, getInstructorFromRequest(courseId));

        return new JsonResult(new StudentsData(students));
    }
}

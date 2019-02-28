package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

/**
 * Action: get all students list from a course in csv.
 */
public class GetStudentsAsCsvAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, userInfo.id),
                logic.getCourse(courseId));
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        String content = "";
        try {
            content = logic.getCourseStudentListAsCsv(courseId, userInfo.id);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult("No course with given instructor is found.", HttpStatus.SC_NOT_FOUND);
        }
        return new CsvResult(content);
    }
}

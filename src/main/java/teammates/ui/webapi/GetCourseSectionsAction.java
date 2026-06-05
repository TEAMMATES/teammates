package teammates.ui.webapi;

import java.util.Set;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CourseSectionsData;

/**
 * Gets the sections of a course.
 */
public class GetCourseSectionsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Course course = logic.getCourse(courseId);
        Instructor instructor = getInstructorFromRequest(courseId);

        gateKeeper.verifyAccessible(instructor, course);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        try {
            Set<Section> sections = logic.getSectionsForCourse(courseId);
            return new JsonResult(new CourseSectionsData(sections));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }
}

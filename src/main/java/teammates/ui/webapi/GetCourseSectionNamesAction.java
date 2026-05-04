package teammates.ui.webapi;

import java.util.List;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.entity.Course;
import teammates.logic.entity.Instructor;
import teammates.ui.output.CourseSectionNamesData;

/**
 * Gets the section names of a course.
 */
public class GetCourseSectionNamesAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Course course = logic.getCourse(courseId);
        Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.id);

        gateKeeper.verifyAccessible(instructor, course);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        try {
            List<String> sectionNames = logic.getSectionNamesForCourse(courseId);
            return new JsonResult(new CourseSectionNamesData(sectionNames));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}

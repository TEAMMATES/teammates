package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseSectionNamesData;

/**
 * Gets the section names of a course.
 */
class GetCourseSectionNamesAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes courseAttributes = logic.getCourse(courseId);

        if (courseAttributes != null && !courseAttributes.isMigrated()) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(instructor, courseAttributes);
            return;
        }

        Course course = sqlLogic.getCourse(courseId);
        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(instructor, course);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        try {
            // courseAttributes is only for checking if in datastore or postgresql
            CourseAttributes courseAttributes = logic.getCourse(courseId);
            if (courseAttributes != null && !courseAttributes.isMigrated()) {
                List<String> sectionNames = logic.getSectionNamesForCourse(courseId);
                return new JsonResult(new CourseSectionNamesData(sectionNames));
            }

            List<String> sectionNames = sqlLogic.getSectionNamesForCourse(courseId);
            return new JsonResult(new CourseSectionNamesData(sectionNames));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}

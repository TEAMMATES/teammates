package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorsData;
import teammates.ui.request.Intent;

/**
 * Get a list of instructors of a course.
 */
public class GetInstructorsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Course course = logic.getCourse(courseId);

        if (course == null) {
            throw new EntityNotFoundException("course not found");
        }

        Intent intent = getNullableEnumRequestParamValue(Const.ParamsNames.INTENT, Intent.class);

        if (intent == null) {
            // get partial details of instructors with information hiding
            // student should belong to the course
            gateKeeper.verifyStudentInCourse(requestContext, courseId);
        } else if (intent == Intent.FULL_DETAIL) {
            // get all instructors of a course without information hiding
            // this need instructor privileges
            gateKeeper.verifyInstructorInCourse(requestContext, courseId);
        } else {
            throw new InvalidHttpParameterException("Invalid intent for this action");
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        Intent intent = getNullableEnumRequestParamValue(Const.ParamsNames.INTENT, Intent.class);
        InstructorsData data;

        List<Instructor> instructorsOfCourse = logic.getInstructorsByCourse(courseId);

        if (intent == null) {
            instructorsOfCourse = instructorsOfCourse
                    .stream()
                    .filter(Instructor::isDisplayedToStudents)
                    .collect(Collectors.toList());
            data = new InstructorsData(instructorsOfCourse);

            // hide information
            data.getInstructors().forEach(i -> {
                i.setAccountId(null);
                i.setJoinState(null);
                i.setIsDisplayedToStudents(null);
                i.setRole(null);
            });
        } else if (intent == Intent.FULL_DETAIL) {
            // get all instructors of a course without information hiding
            data = new InstructorsData(instructorsOfCourse);
        } else {
            throw new InvalidHttpParameterException("Invalid intent for this action");
        }

        return new JsonResult(data);
    }

}

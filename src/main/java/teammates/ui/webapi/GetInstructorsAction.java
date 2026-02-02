package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;
import teammates.ui.request.Intent;

/**
 * Get a list of instructors of a course.
 */
public class GetInstructorsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (userInfo.isAdmin) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Course course = sqlLogic.getCourse(courseId);

        if (course == null) {
            throw new EntityNotFoundException("course not found");
        }

        String intentStr = getRequestParamValue(Const.ParamsNames.INTENT);

        if (intentStr == null) {
            // get partial details of instructors with information hiding
            // student should belong to the course
            Student student = sqlLogic.getStudentByGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(student, course);
        } else if (intentStr.equals(Intent.FULL_DETAIL.toString())) {
            // get all instructors of a course without information hiding
            // this need instructor privileges
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor, course);
        } else {
            throw new InvalidHttpParameterException("unknown intent");
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String intentStr = getRequestParamValue(Const.ParamsNames.INTENT);
        InstructorsData data;

        List<Instructor> instructorsOfCourse = sqlLogic.getInstructorsByCourse(courseId);

        if (intentStr == null) {
            instructorsOfCourse = instructorsOfCourse
                    .stream()
                    .filter(Instructor::isDisplayedToStudents)
                    .collect(Collectors.toList());
            data = new InstructorsData(instructorsOfCourse);

            // hide information
            data.getInstructors().forEach(i -> {
                i.setGoogleId(null);
                i.setJoinState(null);
                i.setIsDisplayedToStudents(null);
                i.setRole(null);
            });
        } else if (intentStr.equals(Intent.FULL_DETAIL.toString())) {
            // get all instructors of a course without information hiding
            // adds googleId if caller is admin or has the appropriate privilege to modify instructor
            if (userInfo.isAdmin || sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()).getPrivileges()
                    .isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)) {
                data = new InstructorsData();

                for (Instructor instructor : instructorsOfCourse) {
                    InstructorData instructorData = new InstructorData(instructor);
                    instructorData.setGoogleId(instructor.getGoogleId());
                    if (userInfo.isAdmin) {
                        instructorData.setKey(instructor.getRegKey());
                    }
                    data.getInstructors().add(instructorData);
                }
            } else {
                data = new InstructorsData(instructorsOfCourse);
            }
        } else {
            throw new InvalidHttpParameterException("unknown intent");
        }

        return new JsonResult(data);
    }

}

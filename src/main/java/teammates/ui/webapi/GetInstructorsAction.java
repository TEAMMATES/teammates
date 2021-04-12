package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;
import teammates.ui.request.Intent;

/**
 * Get a list of instructors of a course.
 */
class GetInstructorsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (userInfo.isAdmin) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("course not found"));
        }

        String intentStr = getRequestParamValue(Const.ParamsNames.INTENT);
        if (intentStr == null) {
            // get partial details of instructors with information hiding
            // student should belong to the course
            StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(student, course);
        } else if (intentStr.equals(Intent.FULL_DETAIL.toString())) {
            // get all instructors of a course without information hiding
            // this need instructor privileges
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor, course);
        } else {
            throw new InvalidHttpParameterException("unknown intent");
        }

    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        List<InstructorAttributes> instructorsOfCourse = logic.getInstructorsForCourse(courseId);

        InstructorsData data;

        String intentStr = getRequestParamValue(Const.ParamsNames.INTENT);
        if (intentStr == null) {
            instructorsOfCourse =
                    instructorsOfCourse.stream()
                            .filter(InstructorAttributes::isDisplayedToStudents)
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
            if (userInfo.isAdmin || logic.getInstructorForGoogleId(courseId, userInfo.getId()).getPrivileges()
                    .isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)) {
                data = new InstructorsData();
                for (InstructorAttributes instructor : instructorsOfCourse) {
                    InstructorData instructorData = new InstructorData(instructor);
                    instructorData.setGoogleId(instructor.googleId);
                    if (userInfo.isAdmin) {
                        instructorData.setKey(StringHelper.encrypt(instructor.getKey()));
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

package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.EnrollResults;
import teammates.common.exception.EnrollException;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.EnrollStudentsData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.StudentEnrollRequest;
import teammates.ui.request.StudentsEnrollRequest;

/**
 * Enroll a list of students.
 *
 * <p>Create the students who are not in the course.
 *
 * <p>Update the students who are already existed.
 *
 * <p>Return all students who are successfully enrolled.
 */
public class EnrollStudentsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                    instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        StudentsEnrollRequest enrollRequests = getAndValidateRequestBody(StudentsEnrollRequest.class);
        List<StudentEnrollRequest> studentEnrollRequests = enrollRequests.getStudentEnrollRequests();
        Course course = logic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("Course with id " + courseId + " not found.");
        }

        EnrollResults enrollResults;
        try {
            enrollResults = logic.enrollStudents(course, studentEnrollRequests);
        } catch (EnrollException e) {
            throw new InvalidOperationException(e);
        }

        return new JsonResult(new EnrollStudentsData(enrollResults));
    }
}

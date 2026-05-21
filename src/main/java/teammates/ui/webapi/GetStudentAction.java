package teammates.ui.webapi;

import java.util.Optional;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.StudentData;

/**
 * Get the information of a student inside a course.
 */
public class GetStudentAction extends Action {

    /** Message indicating that a student not found. */
    static final String STUDENT_NOT_FOUND = "No student found";

    /** String indicating ACCESS is not given. */
    private static final String UNAUTHORIZED_ACCESS = "You are not allowed to view this resource!";

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.REG_KEY;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Course course = logic.getCourse(courseId);

        Student student;

        UUID studentId = getNullableUuidRequestParamValue(Const.ParamsNames.USER_ID);
        String regKey = getRequestParamValue(Const.ParamsNames.REGKEY);
        if (studentId != null) {
            student = getStudentInCourse(courseId, studentId);
            if (student == null) {
                throw new EntityNotFoundException(STUDENT_NOT_FOUND);
            }

            Instructor instructor = logic.getInstructorByGoogleId(courseId, getCurrentUserGoogleId());

            gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                    student.getTeamName(),
                    Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS);
        } else if (regKey != null) {
            getUnregisteredStudent().orElseThrow(() -> new UnauthorizedAccessException(UNAUTHORIZED_ACCESS));
        } else {
            student = logic.getStudentByGoogleId(courseId, getCurrentUserGoogleId());
            gateKeeper.verifyAccessible(student, course);
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Student student;

        UUID studentId = getNullableUuidRequestParamValue(Const.ParamsNames.USER_ID);

        if (studentId == null) {
            student = getPossiblyUnregisteredStudent(courseId);
        } else {
            student = getStudentInCourse(courseId, studentId);
        }

        if (student == null) {
            throw new EntityNotFoundException(STUDENT_NOT_FOUND);
        }

        StudentData studentData = new StudentData(student);
        if (authContext.isAdmin()) {
            studentData.setKey(student.getRegKey());
            studentData.setGoogleId(
                    Optional.ofNullable(student.getAccount())
                        .map(Account::getGoogleId)
                        .orElse("")
            );
        }

        if (studentId == null) {
            // hide information if not an instructor
            studentData.hideInformationForStudent();
            // add student institute
            studentData.setInstitute(student.getCourse().getInstitute());
        }

        return new JsonResult(studentData);
    }

    private Student getStudentInCourse(String courseId, UUID studentId) {
        Student student = logic.getStudent(studentId);
        if (student == null || !courseId.equals(student.getCourseId())) {
            return null;
        }
        return student;
    }
}

package teammates.ui.webapi;

import java.util.Optional;

import teammates.common.util.Const;
import teammates.logic.entity.Account;
import teammates.logic.entity.Course;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;
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
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Course course = logic.getCourse(courseId);

        Student student;

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String regKey = getRequestParamValue(Const.ParamsNames.REGKEY);
        if (studentEmail != null) {
            student = logic.getStudentForEmail(courseId, studentEmail);

            if (student == null || userInfo == null || !userInfo.isInstructor) {
                throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
            }

            Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.id);

            gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                    student.getTeamName(),
                    Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS);
        } else if (regKey != null) {
            getUnregisteredStudent().orElseThrow(() -> new UnauthorizedAccessException(UNAUTHORIZED_ACCESS));
        } else {
            if (userInfo == null || !userInfo.isStudent) {
                throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
            }

            student = logic.getStudentByGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(student, course);
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Student student;

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        if (studentEmail == null) {
            student = getPossiblyUnregisteredStudent(courseId);
        } else {
            student = logic.getStudentForEmail(courseId, studentEmail);
        }

        if (student == null) {
            throw new EntityNotFoundException(STUDENT_NOT_FOUND);
        }

        StudentData studentData = new StudentData(student);
        if (userInfo != null && userInfo.isAdmin) {
            studentData.setKey(student.getRegKey());
            studentData.setGoogleId(
                    Optional.ofNullable(student.getAccount())
                        .map(Account::getGoogleId)
                        .orElse("")
            );
        }

        if (studentEmail == null) {
            // hide information if not an instructor
            studentData.hideInformationForStudent();
            // add student institute
            studentData.setInstitute(student.getCourse().getInstitute());
        }

        return new JsonResult(studentData);
    }
}

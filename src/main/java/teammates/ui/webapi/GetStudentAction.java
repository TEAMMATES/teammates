package teammates.ui.webapi;

import java.util.Optional;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Account;
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

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.REG_KEY;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        UUID studentId = getNullableUuidRequestParamValue(Const.ParamsNames.USER_ID);

        if (studentId != null) {
            Student student = getStudentInCourse(courseId, studentId);
            if (student == null) {
                throw new EntityNotFoundException(STUDENT_NOT_FOUND);
            }

            gateKeeper.verifyInstructorHasPrivilegeForSection(requestContext, courseId,
                    student.getSectionName(),
                    Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS);
        } else {
            gateKeeper.verifyStudentInCourse(requestContext, courseId);
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Student student;

        UUID studentId = getNullableUuidRequestParamValue(Const.ParamsNames.USER_ID);

        if (studentId == null) {
            student = getStudentFromRequest(courseId);
        } else {
            student = getStudentInCourse(courseId, studentId);
        }

        if (student == null) {
            throw new EntityNotFoundException(STUDENT_NOT_FOUND);
        }

        StudentData studentData = new StudentData(student);
        if (requestContext.isAdmin()) {
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

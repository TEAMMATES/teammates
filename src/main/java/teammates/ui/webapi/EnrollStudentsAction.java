package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.output.StudentsData;
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
class EnrollStudentsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return authType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    JsonResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        StudentsEnrollRequest enrollRequests = getAndValidateRequestBody(StudentsEnrollRequest.class);
        List<StudentAttributes> studentsToEnroll = new ArrayList<>();
        enrollRequests.getStudentEnrollRequests().forEach(studentEnrollRequest -> {
            studentsToEnroll.add(StudentAttributes.builder(courseId, studentEnrollRequest.getEmail())
                    .withName(studentEnrollRequest.getName())
                    .withSectionName(studentEnrollRequest.getSection())
                    .withTeamName(studentEnrollRequest.getTeam())
                    .withComment(studentEnrollRequest.getComments())
                    .build());
        });

        try {
            logic.validateSectionsAndTeams(studentsToEnroll, courseId);
        } catch (EnrollException e) {
            throw new InvalidHttpRequestBodyException(e.getMessage(), e);
        }

        List<StudentAttributes> existingStudents = logic.getStudentsForCourse(courseId);

        Set<String> existingStudentsEmail =
                existingStudents.stream().map(StudentAttributes::getEmail).collect(Collectors.toSet());
        List<StudentAttributes> enrolledStudents = new ArrayList<>();
        studentsToEnroll.forEach(student -> {
            if (existingStudentsEmail.contains(student.email)) {
                // The student has been enrolled in the course.
                StudentAttributes.UpdateOptions updateOptions =
                        StudentAttributes.updateOptionsBuilder(student.getCourse(), student.getEmail())
                                .withName(student.getName())
                                .withSectionName(student.getSection())
                                .withTeamName(student.getTeam())
                                .withComment(student.getComments())
                                .build();
                try {
                    StudentAttributes updatedStudent = logic.updateStudentCascade(updateOptions);
                    enrolledStudents.add(updatedStudent);
                } catch (InvalidParametersException | EntityDoesNotExistException
                        | EntityAlreadyExistsException exception) {
                    // Unsuccessfully enrolled students will not be returned.
                    return;
                }
            } else {
                // The student is new.
                try {
                    StudentAttributes newStudent = logic.createStudent(student);
                    enrolledStudents.add(newStudent);
                } catch (InvalidParametersException | EntityAlreadyExistsException exception) {
                    // Unsuccessfully enrolled students will not be returned.
                    return;
                }
            }
        });
        return new JsonResult(new StudentsData(enrolledStudents));
    }
}

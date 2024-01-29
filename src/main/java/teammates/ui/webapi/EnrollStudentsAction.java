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
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.RequestTracer;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.EnrollStudentsData;
import teammates.ui.output.StudentData;
import teammates.ui.output.StudentsData;
import teammates.ui.request.InvalidHttpRequestBodyException;
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

        if (!isCourseMigrated(courseId)) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(
                    instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);

            return;
        }

        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                    instructor, sqlLogic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {

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
            throw new InvalidOperationException(e);
        }

        List<StudentAttributes> enrolledStudents = new ArrayList<>();
        List<EnrollStudentsData.EnrollErrorResults> failToEnrollStudents = new ArrayList<>();

        if (!isCourseMigrated(courseId)) {

            List<StudentAttributes> existingStudents = logic.getStudentsForCourse(courseId);

            Set<String> existingStudentsEmail =
                    existingStudents.stream().map(StudentAttributes::getEmail).collect(Collectors.toSet());

            for (StudentAttributes student : studentsToEnroll) {
                RequestTracer.checkRemainingTime();
                if (existingStudentsEmail.contains(student.getEmail())) {
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
                        taskQueuer.scheduleStudentForSearchIndexing(updatedStudent.getCourse(), updatedStudent.getEmail());
                        enrolledStudents.add(updatedStudent);
                    } catch (InvalidParametersException | EntityDoesNotExistException
                            | EntityAlreadyExistsException exception) {
                        // Unsuccessfully enrolled students will not be returned.
                        failToEnrollStudents.add(new EnrollStudentsData.EnrollErrorResults(student.getEmail(),
                                exception.getMessage()));
                    }
                } else {
                    // The student is new.
                    try {
                        StudentAttributes newStudent = logic.createStudent(student);
                        taskQueuer.scheduleStudentForSearchIndexing(newStudent.getCourse(), newStudent.getEmail());
                        enrolledStudents.add(newStudent);
                    } catch (InvalidParametersException | EntityAlreadyExistsException exception) {
                        // Unsuccessfully enrolled students will not be returned.
                        failToEnrollStudents.add(new EnrollStudentsData.EnrollErrorResults(student.getEmail(),
                                exception.getMessage()));
                    }
                }
            }
        } else {
            
            List<Student> existingStudents = sqlLogic.getStudentsForCourse(courseId);

            Set<String> existingStudentsEmail =
                    existingStudents.stream().map(Student::getEmail).collect(Collectors.toSet());

            for (StudentAttributes student : studentsToEnroll) {
                RequestTracer.checkRemainingTime();
                if (existingStudentsEmail.contains(student.getEmail())) {
                    // The student has been enrolled in the course.
                    try {
                        Student updatedStudent = sqlLogic.updateStudentCascade(student);
                        taskQueuer.scheduleStudentForSearchIndexing(updatedStudent.getCourse().toString(), updatedStudent.getEmail());
                        enrolledStudents.add(StudentAttributes.valueOf(updatedStudent));
                    } catch (InvalidParametersException | EntityDoesNotExistException
                            | EntityAlreadyExistsException exception) {
                        // Unsuccessfully enrolled students will not be returned.
                        failToEnrollStudents.add(new EnrollStudentsData.EnrollErrorResults(student.getEmail(),
                                exception.getMessage()));
                    }
                } else {
                    // The student is new.
                    try {
                        Student newStudent = sqlLogic.createStudent(student);
                        taskQueuer.scheduleStudentForSearchIndexing(newStudent.getCourse().toString(), newStudent.getEmail());
                        enrolledStudents.add(StudentAttributes.valueOf(newStudent));
                    } catch (InvalidParametersException | EntityAlreadyExistsException exception) {
                        // Unsuccessfully enrolled students will not be returned.
                        failToEnrollStudents.add(new EnrollStudentsData.EnrollErrorResults(student.getEmail(),
                                exception.getMessage()));
                    }
                }
            }

        }

        List<StudentData> studentDataList = enrolledStudents
                .stream()
                .map(StudentData::new)
                .collect(Collectors.toList());
        StudentsData data = new StudentsData();

        data.setStudents(studentDataList);

        return new JsonResult(new EnrollStudentsData(data, failToEnrollStudents));
    }
}

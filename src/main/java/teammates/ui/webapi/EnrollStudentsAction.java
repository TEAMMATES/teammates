package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.RequestTracer;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
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

        Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                    instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        StudentsEnrollRequest enrollRequests = getAndValidateRequestBody(StudentsEnrollRequest.class);
        List<StudentsEnrollRequest.StudentEnrollRequest> studentEnrollRequests = enrollRequests.getStudentEnrollRequests();
        Course course = logic.getCourse(courseId);

        List<Student> studentsToEnroll = new ArrayList<>();
        studentEnrollRequests.forEach(studentEnrollRequest -> {
            String normalizedEmail = normalizeEmail(studentEnrollRequest.getEmail());
            Section section = new Section(studentEnrollRequest.getSection());
            course.addSection(section);
            Team team = new Team(studentEnrollRequest.getTeam());
            section.addTeam(team);
            Student student = new Student(
                    course, studentEnrollRequest.getName(),
                    normalizedEmail, studentEnrollRequest.getComments());
            team.addUser(student);
            studentsToEnroll.add(student);
        });
        try {
            logic.validateSectionsAndTeams(studentsToEnroll, courseId);
        } catch (EnrollException e) {
            throw new InvalidOperationException(e);
        }

        List<Student> enrolledStudents = new ArrayList<>();
        List<EnrollStudentsData.EnrollErrorResults> failToEnrollStudents = new ArrayList<>();
        Set<String> existingStudentsEmail;

        List<Student> existingStudents = logic.getStudentsForCourse(courseId);
        existingStudentsEmail = existingStudents.stream()
                .map(Student::getEmail)
                .map(EnrollStudentsAction::normalizeEmail)
                .collect(Collectors.toSet());

        for (StudentsEnrollRequest.StudentEnrollRequest enrollRequest : studentEnrollRequests) {
            RequestTracer.checkRemainingTime();

            String requestEmail = enrollRequest.getEmail();
            String normalizedEmail = normalizeEmail(requestEmail);

            // Check if email already belongs to an instructor in this course
            Instructor existingInstructor = logic.getInstructorForEmail(courseId, normalizedEmail);
            if (existingInstructor != null) {
                failToEnrollStudents.add(new EnrollStudentsData.EnrollErrorResults(requestEmail,
                        "Cannot enroll student with email " + requestEmail
                        + " as this email is already used by an instructor in course " + courseId));
                continue;
            }

            if (existingStudentsEmail.contains(normalizedEmail)) {
                // The student has been enrolled in the course.
                try {
                    Section section = logic.getSectionOrCreate(courseId, enrollRequest.getSection());
                    Team team = logic.getTeamOrCreate(section, enrollRequest.getTeam());
                    Student existingStudent = logic.getStudentForEmail(courseId, normalizedEmail);
                    Student newStudent = new Student(
                            course, enrollRequest.getName(),
                            normalizedEmail, enrollRequest.getComments());
                    newStudent.setId(existingStudent.getId());
                    team.addUser(newStudent);
                    Student updatedStudent = logic.updateStudentCascade(newStudent);
                    enrolledStudents.add(updatedStudent);
                } catch (InvalidParametersException | EntityDoesNotExistException
                        | EntityAlreadyExistsException exception) {
                    // Unsuccessfully enrolled students will not be returned.
                    failToEnrollStudents.add(new EnrollStudentsData.EnrollErrorResults(requestEmail,
                            exception.getMessage()));
                }
            } else {
                // The student is new.
                try {
                    Section section = logic.getSectionOrCreate(courseId, enrollRequest.getSection());
                    Team team = logic.getTeamOrCreate(section, enrollRequest.getTeam());
                    Student newStudent = new Student(
                            course, enrollRequest.getName(),
                            normalizedEmail, enrollRequest.getComments());
                    team.addUser(newStudent);
                    newStudent = logic.createStudent(newStudent);
                    enrolledStudents.add(newStudent);
                } catch (InvalidParametersException | EntityAlreadyExistsException exception) {
                    // Unsuccessfully enrolled students will not be returned.
                    failToEnrollStudents.add(new EnrollStudentsData.EnrollErrorResults(requestEmail,
                            exception.getMessage()));
                }
            }
        }

        List<StudentData> studentDataList = enrolledStudents
                .stream()
                .map(StudentData::new)
                .toList();
        StudentsData data = new StudentsData();

        data.setStudents(studentDataList);

        return new JsonResult(new EnrollStudentsData(data, failToEnrollStudents));
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.toLowerCase(Locale.ROOT);
    }
}

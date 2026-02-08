package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.RequestTracer;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
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

        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                    instructor, sqlLogic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        StudentsEnrollRequest enrollRequests = getAndValidateRequestBody(StudentsEnrollRequest.class);
        List<StudentsEnrollRequest.StudentEnrollRequest> studentEnrollRequests = enrollRequests.getStudentEnrollRequests();
        Course course = sqlLogic.getCourse(courseId);

        List<Student> studentsToEnroll = new ArrayList<>();
        studentEnrollRequests.forEach(studentEnrollRequest -> {
            Section section = new Section(course, studentEnrollRequest.getSection());
            Team team = new Team(section, studentEnrollRequest.getTeam());
            studentsToEnroll.add(new Student(
                    course, studentEnrollRequest.getName(),
                    studentEnrollRequest.getEmail(), studentEnrollRequest.getComments(), team));
        });
        try {
            sqlLogic.validateSectionsAndTeams(studentsToEnroll, courseId);
        } catch (EnrollException e) {
            throw new InvalidOperationException(e);
        }

        List<Student> enrolledStudents = new ArrayList<>();
        List<EnrollStudentsData.EnrollErrorResults> failToEnrollStudents = new ArrayList<>();

        for (StudentsEnrollRequest.StudentEnrollRequest enrollRequest : studentEnrollRequests) {
            RequestTracer.checkRemainingTime();

            // Check if email already belongs to an instructor in this course
            Instructor existingInstructor = sqlLogic.getInstructorForEmail(courseId, enrollRequest.getEmail());
            if (existingInstructor != null) {
                failToEnrollStudents.add(new EnrollStudentsData.EnrollErrorResults(enrollRequest.getEmail(),
                        "Cannot enroll student with email " + enrollRequest.getEmail()
                        + " as this email is already used by an instructor in course " + courseId));
                continue;
            }

            // Check if student exists at the time of processing this specific request
            Student existingStudent = sqlLogic.getStudentForEmail(courseId, enrollRequest.getEmail());

            if (existingStudent != null) {
                // The student has been enrolled in the course.
                try {
                    Section section = sqlLogic.getSectionOrCreate(courseId, enrollRequest.getSection());
                    Team team = sqlLogic.getTeamOrCreate(section, enrollRequest.getTeam());
                    Student newStudent = new Student(
                            course, enrollRequest.getName(),
                            enrollRequest.getEmail(), enrollRequest.getComments(), team);
                    newStudent.setId(existingStudent.getId());
                    Student updatedStudent = sqlLogic.updateStudentCascade(newStudent);
                    taskQueuer.scheduleStudentForSearchIndexing(
                            updatedStudent.getCourseId(), updatedStudent.getEmail());
                    enrolledStudents.add(updatedStudent);
                } catch (InvalidParametersException | EntityDoesNotExistException
                        | EntityAlreadyExistsException exception) {
                    // Unsuccessfully enrolled students will not be returned.
                    failToEnrollStudents.add(new EnrollStudentsData.EnrollErrorResults(enrollRequest.getEmail(),
                            exception.getMessage()));
                }
            } else {
                // The student is new.
                try {
                    Section section = sqlLogic.getSectionOrCreate(courseId, enrollRequest.getSection());
                    Team team = sqlLogic.getTeamOrCreate(section, enrollRequest.getTeam());
                    Student newStudent = new Student(
                            course, enrollRequest.getName(),
                            enrollRequest.getEmail(), enrollRequest.getComments(), team);
                    newStudent = sqlLogic.createStudent(newStudent);
                    taskQueuer.scheduleStudentForSearchIndexing(
                            newStudent.getCourseId(), newStudent.getEmail());
                    enrolledStudents.add(newStudent);
                } catch (InvalidParametersException | EntityAlreadyExistsException exception) {
                    // Unsuccessfully enrolled students will not be returned.
                    failToEnrollStudents.add(new EnrollStudentsData.EnrollErrorResults(enrollRequest.getEmail(),
                            exception.getMessage()));
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

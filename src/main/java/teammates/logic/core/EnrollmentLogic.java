package teammates.logic.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import teammates.common.datatransfer.EnrollResults;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.ui.request.StudentEnrollRequest;
import teammates.ui.request.StudentUpdateRequest;

/**
 * Handles operations related to student enrollment.
 */
public final class EnrollmentLogic {

    static final String ERROR_ENROLL_EXCEED_SECTION_LIMIT =
            "You are trying enroll more than %s students in section \"%s\".";
    static final String ERROR_ENROLL_EXCEED_SECTION_LIMIT_INSTRUCTION =
            "To avoid performance problems, please do not enroll more than %s students in a single section.";

    private static final EnrollmentLogic instance = new EnrollmentLogic();

    private UsersLogic usersLogic;
    private CoursesLogic coursesLogic;
    private FeedbackSessionsLogic feedbackSessionsLogic;

    private EnrollmentLogic() {
        // prevent initialization
    }

    public static EnrollmentLogic inst() {
        return instance;
    }

    void initLogicDependencies(UsersLogic usersLogic, CoursesLogic coursesLogic,
                               FeedbackSessionsLogic feedbackSessionsLogic) {
        this.usersLogic = usersLogic;
        this.coursesLogic = coursesLogic;
        this.feedbackSessionsLogic = feedbackSessionsLogic;
    }

    /**
     * Updates a student by student id and update request, cascading to responses and comments if needed,
     * and validates that section limits are not exceeded.
     */
    public Student updateStudentEnrollment(UUID studentId, StudentUpdateRequest updateRequest)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException,
            EnrollException {
        Student student = usersLogic.getStudent(studentId);
        if (student == null) {
            throw new EntityDoesNotExistException(String.format("Student with id %s not found", studentId));
        }

        String newEmail = updateRequest.getEmail();
        if (newEmail != null && !student.getEmail().equals(newEmail)) {
            if (usersLogic.getInstructorForEmail(student.getCourseId(), newEmail) != null) {
                throw new EntityAlreadyExistsException(String.format(
                        "Cannot update student email to %s as this email is already used by an instructor in course %s",
                        newEmail, student.getCourseId()));
            }
            if (usersLogic.getStudentForEmail(student.getCourseId(), newEmail) != null) {
                throw new EntityAlreadyExistsException(String.format(
                        "Cannot update student email to %s as this email is already used by another student in course %s",
                        newEmail, student.getCourseId()));
            }
        }

        Section section = coursesLogic.getSectionByName(student.getCourseId(), updateRequest.getSection());
        if (section == null) {
            section = coursesLogic.createSection(student.getCourse(), updateRequest.getSection());
        }

        Team team = coursesLogic.getTeamByName(section.getId(), updateRequest.getTeam());
        if (team == null) {
            team = coursesLogic.createTeam(section, updateRequest.getTeam());
        }

        usersLogic.updateStudentCascade(student, newEmail, updateRequest.getName(), team,
                updateRequest.getComments());

        List<Student> studentsInCourse = usersLogic.getStudentsForCourse(student.getCourseId());

        // Validate section limit violations.
        // Precondition: this is executed within a transaction; throwing an exception here will roll back all changes.
        String errorMessage = getSectionInvalidityInfo(studentsInCourse);
        if (!errorMessage.isEmpty()) {
            throw new EnrollException(errorMessage);
        }

        return student;
    }

    /**
     * Updates a student and enqueues the corresponding feedback session summary email.
     */
    public Student updateStudentAndEnqueueSummaryEmail(UUID studentId, StudentUpdateRequest updateRequest)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException,
            EnrollException {
        Student student = updateStudentEnrollment(studentId, updateRequest);
        feedbackSessionsLogic.enqueueFeedbackSessionSummaryEmail(student, EmailType.STUDENT_EMAIL_CHANGED);
        return student;
    }

    /**
     * Enrolls students in a course according to the enroll requests, creating the section and team if needed.
     */
    public EnrollResults enrollStudents(Course course,
            List<StudentEnrollRequest> enrollRequests) throws EnrollException {
        Set<String> instructorEmails = usersLogic.getInstructorsForCourse(course.getId())
                .stream()
                .map(Instructor::getEmail)
                .collect(Collectors.toSet());

        // student email -> student
        Map<String, Student> studentsInCourse = usersLogic.getStudentsForCourse(course.getId())
                .stream()
                .collect(Collectors.toMap(Student::getEmail, Function.identity()));

        // section name -> section
        Map<String, Section> sections = course.getSections()
                .stream()
                .collect(Collectors.toMap(Section::getName, Function.identity()));

        // section -> team name -> team
        Map<String, Map<String, Team>> teams = coursesLogic.getTeamsForCourse(course.getId())
                .stream()
                .collect(Collectors.groupingBy(team -> team.getSection().getName(),
                        Collectors.toMap(Team::getName, Function.identity())));

        EnrollResults enrollResults = new EnrollResults();

        // Process individual enroll requests
        for (StudentEnrollRequest enrollRequest : enrollRequests) {
            String email = enrollRequest.getEmail();
            if (instructorEmails.contains(email)) {
                String errorMsg = String.format(
                        "Cannot enroll student with email %s as this email is already used by an instructor in course %s",
                        email, course.getId());
                enrollResults.addUnsuccessfulEnroll(email, errorMsg);
                continue;
            }

            try {
                Student student = processEnrollRequest(course, studentsInCourse, sections, teams, enrollRequest);
                enrollResults.addEnrolledStudent(student);
            } catch (InvalidParametersException | EntityAlreadyExistsException e) {
                enrollResults.addUnsuccessfulEnroll(email, e.getMessage());
            }
        }

        // Validate section limit violations.
        // Precondition: this is executed within a transaction; throwing an exception here will roll back all changes.
        String errorMessage = getSectionInvalidityInfo(studentsInCourse.values());
        if (!errorMessage.isEmpty()) {
            throw new EnrollException(errorMessage);
        }

        return enrollResults;
    }

    private Student processEnrollRequest(
            Course course,
            Map<String, Student> studentsInCourse,
            Map<String, Section> sections,
            Map<String, Map<String, Team>> teams,
            StudentEnrollRequest enrollRequest) throws InvalidParametersException, EntityAlreadyExistsException {
        String email = enrollRequest.getEmail();

        Section section = sections.get(enrollRequest.getSection());
        if (section == null) {
            section = coursesLogic.createSection(course, enrollRequest.getSection());
            sections.put(section.getName(), section);
        }

        Team team = teams.getOrDefault(section.getName(), Collections.emptyMap()).get(enrollRequest.getTeam());
        if (team == null) {
            team = coursesLogic.createTeam(section, enrollRequest.getTeam());
            teams.computeIfAbsent(section.getName(), k -> new HashMap<>()).put(team.getName(), team);
        }

        Student student = studentsInCourse.get(email);
        if (student != null) {
            usersLogic.updateStudentCascade(student, null, enrollRequest.getName(), team, enrollRequest.getComments());
        } else {
            student = usersLogic.createStudent(course, team, enrollRequest.getName(), email,
                    enrollRequest.getComments());
            studentsInCourse.put(email, student);
        }

        return student;
    }

    private static String getSectionInvalidityInfo(Collection<Student> studentList) {
        Map<String, Integer> sectionCountMap = new HashMap<>();
        for (Student student : studentList) {
            String sectionName = student.getSectionName();
            assert sectionName != null : "Section name should not be null";
            sectionCountMap.put(sectionName, sectionCountMap.getOrDefault(sectionName, 0) + 1);
        }

        StringJoiner errorMessage = new StringJoiner(" ");
        sectionCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (entry.getValue() > Const.SECTION_SIZE_LIMIT) {
                        errorMessage.add(String.format(
                                ERROR_ENROLL_EXCEED_SECTION_LIMIT,
                                Const.SECTION_SIZE_LIMIT, entry.getKey()));
                    }
                });

        if (errorMessage.length() > 0) {
            errorMessage.add(String.format(
                    ERROR_ENROLL_EXCEED_SECTION_LIMIT_INSTRUCTION,
                    Const.SECTION_SIZE_LIMIT));
        }

        return errorMessage.toString();
    }
}

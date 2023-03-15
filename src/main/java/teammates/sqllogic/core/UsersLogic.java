package teammates.sqllogic.core;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;

/**
 * Handles operations related to user (instructor & student).
 *
 * @see User
 * @see UsersDb
 */
public final class UsersLogic {

    private static final UsersLogic instance = new UsersLogic();

    private UsersDb usersDb;

    private AccountsLogic accountsLogic;

    private UsersLogic() {
        // prevent initialization
    }

    public static UsersLogic inst() {
        return instance;
    }

    void initLogicDependencies(UsersDb usersDb, AccountsLogic accountsLogic) {
        this.usersDb = usersDb;
        this.accountsLogic = accountsLogic;
    }

    /**
     * Create an instructor.
     * @return the created instructor
     * @throws InvalidParametersException if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the database.
     */
    public Instructor createInstructor(Instructor instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return usersDb.createInstructor(instructor);
    }

    /**
     * Creates a student.
     * @return the created student
     * @throws InvalidParametersException if the student is not valid
     * @throws EntityAlreadyExistsException if the student already exists in the database.
     */
    public Student createStudent(Student student) throws InvalidParametersException, EntityAlreadyExistsException {
        return usersDb.createStudent(student);
    }

    /**
     * Gets instructor associated with {@code id}.
     *
     * @param id Id of Instructor.
     * @return Returns Instructor if found else null.
     */
    public Instructor getInstructor(UUID id) {
        assert id != null;

        return usersDb.getInstructor(id);
    }

    /**
     * Gets the instructor with the specified email.
     */
    public Instructor getInstructorForEmail(String courseId, String userEmail) {
        return usersDb.getInstructorForEmail(courseId, userEmail);
    }

    /**
     * Gets an instructor by associated {@code regkey}.
     */
    public Instructor getInstructorByRegistrationKey(String regKey) {
        assert regKey != null;

        return usersDb.getInstructorByRegKey(regKey);
    }

    /**
     * Gets an instructor by associated {@code googleId}.
     */
    public Instructor getInstructorByGoogleId(String courseId, String googleId) {
        assert courseId != null;
        assert googleId != null;

        return usersDb.getInstructorByGoogleId(courseId, googleId);
    }

    /**
     * Deletes an instructor or student.
     */
    public <T extends User> void deleteUser(T user) {
        usersDb.deleteUser(user);
    }

    /**
     * Gets the list of instructors with co-owner privileges in a course.
     */
    public List<Instructor> getCoOwnersForCourse(String courseId) {
        List<Instructor> instructors = getInstructorsForCourse(courseId);
        List<Instructor> instructorsWithCoOwnerPrivileges = new ArrayList<>();
        for (Instructor instructor : instructors) {
            if (!instructor.hasCoownerPrivileges()) {
                continue;
            }
            instructorsWithCoOwnerPrivileges.add(instructor);
        }
        return instructorsWithCoOwnerPrivileges;
    }

    /**
     * Gets a list of instructors for the specified course.
     */
    public List<Instructor> getInstructorsForCourse(String courseId) {
        List<Instructor> instructorReturnList = usersDb.getInstructorsForCourse(courseId);
        sortByName(instructorReturnList);

        return instructorReturnList;
    }

    /**
     * Gets all instructors associated with a googleId.
     */
    public List<Instructor> getInstructorsForGoogleId(String googleId) {
        assert googleId != null;
        return usersDb.getInstructorsForGoogleId(googleId);
    }

    /**
     * Returns true if the user associated with the googleId is an instructor in any course in the system.
     */
    public boolean isInstructorInAnyCourse(String googleId) {
        return !usersDb.getAllInstructorsByGoogleId(googleId).isEmpty();
    }

    /**
     * Gets student associated with {@code id}.
     *
     * @param id Id of Student.
     * @return Returns Student if found else null.
     */
    public Student getStudent(UUID id) {
        assert id != null;

        return usersDb.getStudent(id);
    }

    /**
     * Gets the student with the specified email.
     */
    public Student getStudentForEmail(String courseId, String userEmail) {
        return usersDb.getStudentForEmail(courseId, userEmail);
    }

    /**
     * Gets a list of students with the specified email.
     */
    public List<Student> getAllStudentsForEmail(String email) {
        return usersDb.getAllStudentsForEmail(email);
    }

    /**
     * Gets a list of students for the specified course.
     */
    public List<Student> getStudentsForCourse(String courseId) {
        List<Student> studentReturnList = usersDb.getStudentsForCourse(courseId);
        sortByName(studentReturnList);

        return studentReturnList;
    }

    /**
     * Gets all students of a section.
     */
    public List<Student> getStudentsForSection(Section section, String courseId) {
        return usersDb.getStudentsForSection(section, courseId);
    }

    /**
     * Gets all students of a team.
     */
    public List<Student> getStudentsForTeam(String teamName, String courseId) {
        return usersDb.getStudentsForTeam(teamName, courseId);
    }

    /**
     * Gets a student by associated {@code regkey}.
     */
    public Student getStudentByRegistrationKey(String regKey) {
        assert regKey != null;

        return usersDb.getStudentByRegKey(regKey);
    }

    /**
     * Gets a student by associated {@code googleId}.
     */
    public Student getStudentByGoogleId(String courseId, String googleId) {
        assert courseId != null;
        assert googleId != null;

        return usersDb.getStudentByGoogleId(courseId, googleId);
    }

    /**
     * Returns true if the user associated with the googleId is a student in any course in the system.
     */
    public boolean isStudentInAnyCourse(String googleId) {
        return !usersDb.getAllStudentsByGoogleId(googleId).isEmpty();
    }

    /**
     * Gets all instructors and students by {@code googleId}.
     */
    public List<User> getAllUsersByGoogleId(String googleId) {
        assert googleId != null;

        return usersDb.getAllUsersByGoogleId(googleId);
    }

    /**
     * Resets the googleId associated with the instructor.
     */
    public void resetInstructorGoogleId(String email, String courseId, String googleId)
            throws EntityDoesNotExistException {
        assert email != null;
        assert courseId != null;
        assert googleId != null;

        Instructor instructor = getInstructorForEmail(courseId, email);

        if (instructor == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT
                    + "Instructor [courseId=" + courseId + ", email=" + email + "]");
        }

        instructor.setAccount(null);

        if (usersDb.getAllUsersByGoogleId(googleId).isEmpty()) {
            accountsLogic.deleteAccountCascade(googleId);
        }
    }

    /**
     * Resets the googleId associated with the student.
     */
    public void resetStudentGoogleId(String email, String courseId, String googleId)
            throws EntityDoesNotExistException {
        assert email != null;
        assert courseId != null;
        assert googleId != null;

        Student student = getStudentForEmail(courseId, email);

        if (student == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT
                    + "Student [courseId=" + courseId + ", email=" + email + "]");
        }

        student.setAccount(null);

        if (usersDb.getAllUsersByGoogleId(googleId).isEmpty()) {
            accountsLogic.deleteAccountCascade(googleId);
        }
    }

    /**
     * Sorts the instructors list alphabetically by name.
     */
    public static <T extends User> void sortByName(List<T> users) {
        users.sort(Comparator.comparing(user -> user.getName().toLowerCase()));
    }

    /**
     * Checks if an instructor with {@code googleId} can create a course with {@code institute}
     * (ie. has an existing course(s) with the same {@code institute}).
     */
    public boolean canInstructorCreateCourse(String googleId, String institute) {
        assert googleId != null;
        assert institute != null;

        List<Instructor> existingInstructors = getInstructorsForGoogleId(googleId);
        return existingInstructors
                .stream()
                .filter(Instructor::hasCoownerPrivileges)
                .map(instructor -> instructor.getCourse())
                .anyMatch(course -> institute.equals(course.getInstitute()));
    }
}

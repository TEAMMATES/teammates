package teammates.logic.core;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivilegesLegacy;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.api.CoursesDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.ui.request.CourseCreateRequest;

/**
 * Handles operations related to courses.
 *
 * @see Course
 * @see CoursesDb
 */
public final class CoursesLogic {

    private static final CoursesLogic instance = new CoursesLogic();

    private CoursesDb coursesDb;
    private UsersLogic usersLogic;
    private InstructorPermissionsLogic instructorPermissionsLogic;

    private CoursesLogic() {
        // prevent initialization
    }

    public static CoursesLogic inst() {
        return instance;
    }

    void initLogicDependencies(CoursesDb coursesDb, UsersLogic usersLogic, InstructorPermissionsLogic instructorPermissionsLogic) {
        this.coursesDb = coursesDb;
        this.usersLogic = usersLogic;
        this.instructorPermissionsLogic = instructorPermissionsLogic;
    }

    /**
     * Creates a course.
     *
     * @return the created course
     * @throws InvalidParametersException   if the course is not valid
     * @throws EntityAlreadyExistsException if a course with the same ID already exists
     */
    public Course createCourse(String courseId, String courseName, String timeZone, String institute)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = new Course(courseId, courseName, timeZone, institute);

        validateCourse(course);

        if (getCourse(course.getId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, course.toString()));
        }

        return coursesDb.persistCourse(course);
    }

    /**
     * Creates a course and an associated instructor for the course.
     *
     * @param courseCreator      the account of the instructor creating the course.
     * @param courseCreateRequest the course creation details.
     * @throws InvalidParametersException   if the course is not valid.
     * @throws EntityAlreadyExistsException if the course already exists.
     */
    public Course createCourseAndInstructor(
            Account courseCreator, CourseCreateRequest courseCreateRequest)
            throws InvalidParametersException, EntityAlreadyExistsException {

        String timeZone = courseCreateRequest.getTimeZone();
        String timeZoneErrorMessage = FieldValidator.getInvalidityInfoForTimeZone(timeZone);
        if (!timeZoneErrorMessage.isEmpty()) {
            throw new InvalidParametersException(timeZoneErrorMessage);
        }

        Course course = createCourse(courseCreateRequest.getCourseId().trim(), courseCreateRequest.getCourseName(),
                timeZone, courseCreateRequest.getInstitute());

        // Create the initial instructor for the course
        InstructorPrivilegesLegacy privileges = instructorPermissionsLogic.legacyPrivilegesForRole(
                Const.InstructorPermissionRoleNames.COOWNER);
        Instructor instructor = new Instructor(
                course,
                courseCreator.getName(),
                courseCreator.getEmail(),
                false,
                courseCreator.getName(),
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                privileges);
        instructor.setAccount(courseCreator);

        try {
            usersLogic.createInstructor(instructor);
        } catch (InvalidParametersException | EntityAlreadyExistsException e) {
            assert false : "Unexpected exception while trying to create instructor for a new course "
                                  + System.lineSeparator() + course.toString();
        }

        return course;
    }

    /**
     * Gets a course by course id.
     *
     * @param courseId of course.
     * @return the specified course.
     */
    public Course getCourse(String courseId) {
        return coursesDb.getCourse(courseId);
    }

    /**
     * Returns a list of {@link Course} for all courses a given student is enrolled in.
     *
     * @param account The account of the student
     */
    public List<Course> getCoursesForStudentAccount(Account account) {
        List<Student> students = usersLogic.getStudentsByAccountId(account.getId());

        return students
                .stream()
                .map(Student::getCourse)
                .filter(course -> !course.isCourseDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of {@link Course} for all courses for a given list of instructors
     * except for courses in Recycle Bin.
     */
    public List<Course> getCoursesForInstructors(List<Instructor> instructors) {
        assert instructors != null;

        return instructors
                .stream()
                .map(Instructor::getCourse)
                .filter(course -> !course.isCourseDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of soft-deleted {@link Course} for a given list of instructors.
     */
    public List<Course> getSoftDeletedCoursesForInstructors(List<Instructor> instructors) {
        assert instructors != null;

        return instructors
                .stream()
                .map(Instructor::getCourse)
                .filter(course -> course.isCourseDeleted())
                .collect(Collectors.toList());
    }

    /**
     * Deletes a course and cascade its students, instructors, sessions, responses, deadline extensions and comments.
     * Fails silently if no such course.
     */
    public void deleteCourse(String courseId) {
        Course course = coursesDb.getCourse(courseId);
        if (course == null) {
            return;
        }

        coursesDb.removeCourse(course);
    }

    /**
     * Moves a course to Recycle Bin by its given corresponding ID.
     *
     * @return the time when the course is moved to the recycle bin.
     */
    public Course moveCourseToRecycleBin(String courseId) throws EntityDoesNotExistException {
        Course course = coursesDb.getCourse(courseId);
        if (course == null) {
            throw new EntityDoesNotExistException("Trying to move a non-existent course to recycling bin.");
        }

        Instant now = Instant.now();
        course.setDeletedAt(now);
        return course;
    }

    /**
     * Restores a course from Recycle Bin by its given corresponding ID.
     */
    public void restoreCourseFromRecycleBin(String courseId) throws EntityDoesNotExistException {
        Course course = coursesDb.getCourse(courseId);
        if (course == null) {
            throw new EntityDoesNotExistException("Trying to restore a non-existent course from recycling bin.");
        }

        course.setDeletedAt(null);
    }

    /**
     * Updates a course.
     *
     * @return updated course
     * @throws InvalidParametersException  if attributes to update are not valid
     * @throws EntityDoesNotExistException if the course cannot be found
     */
    public Course updateCourse(String courseId, String name, String timezone)
            throws InvalidParametersException, EntityDoesNotExistException {
        Course course = getCourse(courseId);
        if (course == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + Course.class);
        }
        course.setName(name);
        course.setTimeZone(timezone);

        validateCourse(course);

        return course;
    }

    /**
     * Returns the section with the given name in the given course, or null if not found.
     */
    public Section getSectionByName(String courseId, String sectionName) {
        return coursesDb.getSectionByName(courseId, sectionName);
    }

    /**
     * Returns the section with the given UUID, or null if not found.
     */
    public Section getSectionById(UUID sectionId) {
        return coursesDb.getSectionById(sectionId);
    }

    /**
     * Creates a section.
     */
    public Section createSection(Course course, String sectionName)
            throws InvalidParametersException, EntityAlreadyExistsException {
        if (coursesDb.getSectionByName(course.getId(), sectionName) != null) {
            throw new EntityAlreadyExistsException("Section with name "
                    + sectionName + " already exists in course " + course.getId());
        }

        Section section = new Section(sectionName);
        course.addSection(section);

        validateSection(section);

        return coursesDb.persistSection(section);
    }

    /**
     * Gets the sections for the given {@code courseId}.
     */
    public Set<Section> getSectionsForCourse(String courseId) throws EntityDoesNotExistException {
        Course course = getCourse(courseId);

        if (course == null) {
            throw new EntityDoesNotExistException("Trying to get section names for a non-existent course.");
        }

        return course.getSections();
    }

    /**
     * Creates a team.
     */
    public Team createTeam(Section section, String teamName)
            throws InvalidParametersException, EntityAlreadyExistsException {
        if (coursesDb.getTeamByName(section.getId(), teamName) != null) {
            throw new EntityAlreadyExistsException("Team with name "
                    + teamName + " already exists in section " + section.getName());
        }

        Team team = new Team(teamName);
        section.addTeam(team);

        validateTeam(team);

        return coursesDb.persistTeam(team);
    }

    /**
     * Returns teams for a course.
     */
    public List<Team> getTeamsForCourse(String courseId) {
        return coursesDb.getTeamsForCourse(courseId);
    }

    /**
     * Sorts the courses list alphabetically by id.
     */
    public static void sortById(List<Course> courses) {
        courses.sort(Comparator.comparing(Course::getId));
    }

    private void validateTeam(Team team) throws InvalidParametersException {
        if (!team.isValid()) {
            throw new InvalidParametersException(team.getInvalidityInfo());
        }
    }

    private void validateSection(Section section) throws InvalidParametersException {
        if (!section.isValid()) {
            throw new InvalidParametersException(section.getInvalidityInfo());
        }
    }

    private void validateCourse(Course course) throws InvalidParametersException {
        if (!course.isValid()) {
            throw new InvalidParametersException(course.getInvalidityInfo());
        }
    }
}

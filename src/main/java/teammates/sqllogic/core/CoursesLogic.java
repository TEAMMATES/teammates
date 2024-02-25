package teammates.sqllogic.core;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

/**
 * Handles operations related to courses.
 *
 * @see Course
 * @see CoursesDb
 */
public final class CoursesLogic {

    private static final CoursesLogic instance = new CoursesLogic();

    private CoursesDb coursesDb;

    private FeedbackSessionsLogic fsLogic;

    private UsersLogic usersLogic;

    private CoursesLogic() {
        // prevent initialization
    }

    public static CoursesLogic inst() {
        return instance;
    }

    void initLogicDependencies(CoursesDb coursesDb, FeedbackSessionsLogic fsLogic, UsersLogic usersLogic) {
        this.coursesDb = coursesDb;
        this.fsLogic = fsLogic;
        this.usersLogic = usersLogic;
    }

    /**
     * Creates a course.
     *
     * @return the created course
     * @throws InvalidParametersException   if the course is not valid
     * @throws EntityAlreadyExistsException if the course already exists in the
     *                                      database.
     */
    public Course createCourse(Course course) throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesDb.createCourse(course);
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
     * @param googleId The Google ID of the student
     */
    public List<Course> getCoursesForStudentAccount(String googleId) {
        List<Student> students = usersLogic.getAllStudentsByGoogleId(googleId);

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
    public void deleteCourseCascade(String courseId) {
        Course course = coursesDb.getCourse(courseId);
        if (course == null) {
            return;
        }

        usersLogic.deleteStudentsInCourseCascade(courseId);
        List<FeedbackSession> feedbackSessions = fsLogic.getFeedbackSessionsForCourse(courseId);
        feedbackSessions.forEach(feedbackSession -> {
            fsLogic.deleteFeedbackSessionCascade(feedbackSession.getName(), courseId);
        });
        coursesDb.deleteSectionsByCourseId(courseId);
        List<Instructor> instructors = usersLogic.getInstructorsForCourse(courseId);
        instructors.forEach(instructor -> {
            usersLogic.deleteInstructorCascade(courseId, instructor.getEmail());
        });

        coursesDb.deleteCourse(course);
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

        if (!course.isValid()) {
            throw new InvalidParametersException(course.getInvalidityInfo());
        }

        return course;
    }

    /**
     * Creates a section.
     */
    public Section createSection(Section section) throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesDb.createSection(section);
    }

    /**
     * Get section by {@code courseId} and {@code teamName}.
     */
    public Section getSectionByCourseIdAndTeam(String courseId, String teamName) {
        assert courseId != null;
        assert teamName != null;

        return coursesDb.getSectionByCourseIdAndTeam(courseId, teamName);
    }

    /**
     * Gets a list of section names for the given {@code courseId}.
     */
    public List<String> getSectionNamesForCourse(String courseId) throws EntityDoesNotExistException {
        assert courseId != null;
        Course course = getCourse(courseId);

        if (course == null) {
            throw new EntityDoesNotExistException("Trying to get section names for a non-existent course.");
        }

        return course.getSections()
                .stream()
                .map(section -> section.getName())
                .collect(Collectors.toList());
    }

    /**
     * Gets the institute of the course.
     */
    public String getCourseInstitute(String courseId) {
        Course course = getCourse(courseId);
        assert course != null : "Trying to getCourseInstitute for inexistent course with id " + courseId;
        return course.getInstitute();
    }

    /**
     * Creates a team.
     */
    public Team createTeam(Team team) throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesDb.createTeam(team);
    }

    /**
     * Returns teams for a particular section.
     */
    public List<Team> getTeamsForSection(Section section) {
        return coursesDb.getTeamsForSection(section);
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
}

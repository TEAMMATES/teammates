package teammates.sqllogic.core;

import java.time.Instant;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlentity.Course;

/**
 * Handles operations related to courses.
 *
 * @see Course
 * @see CoursesDb
 */
public final class CoursesLogic {

    private static final CoursesLogic instance = new CoursesLogic();

    private CoursesDb coursesDb;

    // private FeedbackSessionsLogic fsLogic;

    private CoursesLogic() {
        // prevent initialization
    }

    public static CoursesLogic inst() {
        return instance;
    }

    void initLogicDependencies(CoursesDb coursesDb, FeedbackSessionsLogic fsLogic) {
        this.coursesDb = coursesDb;
        // this.fsLogic = fsLogic;
    }

    /**
     * Creates a course.
     * @return the created course
     * @throws InvalidParametersException if the course is not valid
     * @throws EntityAlreadyExistsException if the course already exists in the database.
     */
    public Course createCourse(Course course) throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesDb.createCourse(course);
    }

    /**
     * Gets a course by course id.
     * @param courseId of course.
     * @return the specified course.
     */
    public Course getCourse(String courseId) {
        return coursesDb.getCourse(courseId);
    }

    /**
     * Deletes a course and cascade its students, instructors, sessions, responses, deadline extensions and comments.
     * Fails silently if no such course.
     */
    public void deleteCourseCascade(String courseId) {
        if (getCourse(courseId) == null) {
            return;
        }

        // TODO: Migrate after other Logic classes have been migrated.
        // AttributesDeletionQuery query = AttributesDeletionQuery.builder()
        //         .withCourseId(courseId)
        //         .build();
        // frcLogic.deleteFeedbackResponseComments(query);
        // frLogic.deleteFeedbackResponses(query);
        // fqLogic.deleteFeedbackQuestions(query);
        // feedbackSessionsLogic.deleteFeedbackSessions(query);
        // studentsLogic.deleteStudents(query);
        // instructorsLogic.deleteInstructors(query);
        // deadlineExtensionsLogic.deleteDeadlineExtensions(query);

        coursesDb.deleteCourse(courseId);
    }

    /**
     * Moves a course to Recycle Bin by its given corresponding ID.
     * @return the time when the course is moved to the recycle bin.
     */
    public Instant moveCourseToRecycleBin(String courseId) throws EntityDoesNotExistException {
        return coursesDb.softDeleteCourse(courseId);
    }

    /**
     * Restores a course from Recycle Bin by its given corresponding ID.
     */
    public void restoreCourseFromRecycleBin(String courseId) throws EntityDoesNotExistException {
        coursesDb.restoreDeletedCourse(courseId);
    }

    /**
     * Updates a course by {@link CourseAttributes.UpdateOptions}.
     *
     * <p>If the {@code timezone} of the course is changed, cascade the change to its corresponding feedback sessions.
     *
     * @return updated course
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the course cannot be found
     */
    public Course updateCourseCascade(Course course)
            throws InvalidParametersException, EntityDoesNotExistException {
        Course oldCourse = coursesDb.getCourse(course.getId());
        Course updatedCourse = coursesDb.updateCourse(course);

        if (!updatedCourse.getTimeZone().equals(oldCourse.getTimeZone())) {
            // TODO: Migrate once Feedback Session is ready.
            // feedbackSessionsLogic.updateFeedbackSessionsTimeZoneForCourse(updatedCourse.getId(), updatedCourse.getTimeZone());
        }

        return updatedCourse;
    }
}

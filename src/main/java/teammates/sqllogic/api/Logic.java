package teammates.sqllogic.api;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.sqllogic.core.CoursesLogic;
import teammates.sqllogic.core.UsageStatisticsLogic;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.UsageStatistics;

import java.time.Instant;
import java.util.List;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>This is a Facade class which simply forwards the method to internal classes.
 */
public class Logic {
    private static final Logic instance = new Logic();

    final CoursesLogic coursesLogic = CoursesLogic.inst();
    // final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    final UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();

    Logic() {
        // prevent initialization
    }

    public static Logic inst() {
        return instance;
    }

    // Courses

    /**
     * Gets a course by course id.
     * @param courseId courseId of the course.
     * @return the specified course.
     */
    public Course getCourse(String courseId) {
        return coursesLogic.getCourse(courseId);
    }

    /**
     * Creates a course.
     * @param course the course to create.
     * @return the created course.
     * @throws InvalidParametersException if the course is not valid.
     * @throws EntityAlreadyExistsException if the course already exists.
     */
    public Course createCourse(Course course) throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesLogic.createCourse(course);
    }

    public List<UsageStatistics> getUsageStatisticsForTimeRange(Instant startTime, Instant endTime) {
        assert startTime != null;
        assert endTime != null;
        assert startTime.toEpochMilli() < endTime.toEpochMilli();

        return usageStatisticsLogic.getUsageStatisticsForTimeRange(startTime, endTime);
    }

}

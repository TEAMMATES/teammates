package teammates.logic.email.model;

import java.util.List;

/**
 * Feedback session access links grouped by course.
 *
 * @param courseId the course ID
 * @param courseName the course name
 * @param sessionLinks the session links for the course
 */
public record CourseSessionLinks(
        String courseId,
        String courseName,
        List<SessionAccessLink> sessionLinks) {
}

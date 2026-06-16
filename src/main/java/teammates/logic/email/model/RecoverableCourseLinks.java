package teammates.logic.email.model;

import java.util.List;

/**
 * Recoverable links grouped by course.
 * 
 * @param courseId the course ID
 * @param courseName the course name
 * @param sessionLinks the recoverable session links for the course
 */
public record RecoverableCourseLinks(
        String courseId,
        String courseName,
        List<RecoverableSessionLink> sessionLinks) {
}

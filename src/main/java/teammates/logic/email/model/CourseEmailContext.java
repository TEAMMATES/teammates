package teammates.logic.email.model;

import java.util.List;

/**
 * Shared email context for course-related emails.
 *
 * @param courseId the ID of the course
 * @param courseName the name of the course
 * @param coOwnerContacts the email contacts of the course co-owners, if any
 */
public record CourseEmailContext(
        String courseId,
        String courseName,
        List<EmailContact> coOwnerContacts
) {
}

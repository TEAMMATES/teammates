package teammates.logic.email.model;

import java.util.List;

/**
 * Data required to render a course feedback-session summary email.
 *
 * @param recipientEmailAddress the email recipient
 * @param recipientName the recipient name
 * @param courseId the course ID
 * @param courseName the course name
 * @param coOwnerContacts the contactable co-owner instructors for the course
 * @param isInstructor whether the recipient is an instructor
 * @param isYetToJoinCourse whether the recipient has not yet joined the course
 * @param joinUrl the course join URL for the recipient
 * @param courseSessionLinks the feedback session links grouped by course
 */
public record FeedbackSessionSummaryEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String courseId,
        String courseName,
        List<EmailContact> coOwnerContacts,
        boolean isInstructor,
        boolean isYetToJoinCourse,
        String joinUrl,
        List<CourseSessionLinks> courseSessionLinks) {
}

package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;

/**
 * Holds a bundle of *Attributes data transfer objects.
 * This class is mainly used for serializing JSON strings.
 */
public class DataBundle {
    public Map<String, AccountAttributes> accounts = new LinkedHashMap<String, AccountAttributes>();
    public Map<String, CourseAttributes> courses = new LinkedHashMap<String, CourseAttributes>();
    public Map<String, InstructorAttributes> instructors = new LinkedHashMap<String, InstructorAttributes>();
    public Map<String, StudentAttributes> students = new LinkedHashMap<String, StudentAttributes>();
    public Map<String, FeedbackSessionAttributes> feedbackSessions =
            new LinkedHashMap<String, FeedbackSessionAttributes>();
    public Map<String, FeedbackQuestionAttributes> feedbackQuestions =
            new LinkedHashMap<String, FeedbackQuestionAttributes>();
    public Map<String, FeedbackResponseAttributes> feedbackResponses =
            new LinkedHashMap<String, FeedbackResponseAttributes>();
    public Map<String, FeedbackResponseCommentAttributes> feedbackResponseComments =
            new LinkedHashMap<String, FeedbackResponseCommentAttributes>();
    public Map<String, CommentAttributes> comments = new LinkedHashMap<String, CommentAttributes>();
    public Map<String, StudentProfileAttributes> profiles = new LinkedHashMap<String, StudentProfileAttributes>();
    public Map<String, AdminEmailAttributes> adminEmails = new LinkedHashMap<>();

    /**
     * Sanitize each attribute in the dataBundle for saving.
     */
    public void sanitizeForSaving() {
        sanitizeMapForSaving(accounts);
        sanitizeMapForSaving(courses);
        sanitizeMapForSaving(instructors);
        sanitizeMapForSaving(students);
        sanitizeMapForSaving(feedbackSessions);
        sanitizeMapForSaving(feedbackQuestions);
        sanitizeMapForSaving(feedbackResponses);
        sanitizeMapForSaving(feedbackResponseComments);
        sanitizeMapForSaving(comments);
        sanitizeMapForSaving(profiles);
        sanitizeMapForSaving(adminEmails);
    }

    /**
     * Sanitize each attribute in the {@code map} for saving.
     */
    private <T extends EntityAttributes> void sanitizeMapForSaving(Map<String, T> map) {
        for (T attribute : map.values()) {
            attribute.sanitizeForSaving();
        }
    }
}

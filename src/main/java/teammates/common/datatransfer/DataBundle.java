package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.storage.entity.BaseEntity;

/**
 * Holds a bundle of *Attributes data transfer objects.
 * This class is mainly used for serializing JSON strings.
 */
public class DataBundle {
    public Map<String, AccountAttributes> accounts = new LinkedHashMap<>();
    public Map<String, CourseAttributes> courses = new LinkedHashMap<>();
    public Map<String, InstructorAttributes> instructors = new LinkedHashMap<>();
    public Map<String, StudentAttributes> students = new LinkedHashMap<>();
    public Map<String, FeedbackSessionAttributes> feedbackSessions = new LinkedHashMap<>();
    public Map<String, FeedbackQuestionAttributes> feedbackQuestions = new LinkedHashMap<>();
    public Map<String, FeedbackResponseAttributes> feedbackResponses = new LinkedHashMap<>();
    public Map<String, FeedbackResponseCommentAttributes> feedbackResponseComments = new LinkedHashMap<>();
    public Map<String, StudentProfileAttributes> profiles = new LinkedHashMap<>();
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
        sanitizeMapForSaving(profiles);
        sanitizeMapForSaving(adminEmails);
    }

    /**
     * Sanitize each attribute in the {@code map} for saving.
     */
    private <T extends EntityAttributes<? extends BaseEntity>> void sanitizeMapForSaving(Map<String, T> map) {
        for (T attribute : map.values()) {
            attribute.sanitizeForSaving();
        }
    }
}

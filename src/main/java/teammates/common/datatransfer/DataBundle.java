package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;

import teammates.common.datatransfer.attributes.*;

/**
 * Holds a bundle of *Attributes data transfer objects.
 *
 * <p>This class is mainly used for serializing JSON strings.
 */
// CHECKSTYLE.OFF:JavadocVariable each field represents different entity types
public class DataBundle {
    public Map<String, AccountAttributes> accounts = new LinkedHashMap<>();
    public Map<String, AccountRequestAttributes> accountRequests = new LinkedHashMap<>();
    public Map<String, CourseAttributes> courses = new LinkedHashMap<>();
    public Map<String, DeadlineExtensionAttributes> deadlineExtensions = new LinkedHashMap<>();
    public Map<String, InstructorAttributes> instructors = new LinkedHashMap<>();
    public Map<String, StudentAttributes> students = new LinkedHashMap<>();
    public Map<String, FeedbackSessionAttributes> feedbackSessions = new LinkedHashMap<>();
    public Map<String, FeedbackQuestionAttributes> feedbackQuestions = new LinkedHashMap<>();
    public Map<String, FeedbackResponseAttributes> feedbackResponses = new LinkedHashMap<>();
    public Map<String, FeedbackResponseCommentAttributes> feedbackResponseComments = new LinkedHashMap<>();
    public Map<String, StudentProfileAttributes> profiles = new LinkedHashMap<>();
    public Map<String, NotificationAttributes> notifications = new LinkedHashMap<>();
    public Map<String, FeedbackSessionLogEntryAttributes> feedbackSessionLogEntries = new LinkedHashMap<>();
}

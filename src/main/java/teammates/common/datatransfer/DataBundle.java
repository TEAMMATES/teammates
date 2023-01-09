package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

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
    public Map<String, NotificationAttributes> notifications = new LinkedHashMap<>();
}

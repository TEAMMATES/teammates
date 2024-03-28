package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;

import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

/**
 * Holds a bundle of entities.
 *
 * <p>This class is mainly used for serializing JSON strings.
 */
// CHECKSTYLE.OFF:JavadocVariable each field represents different entity types
public class SqlDataBundle {
    public Map<String, Account> accounts = new LinkedHashMap<>();
    public Map<String, AccountRequest> accountRequests = new LinkedHashMap<>();
    public Map<String, Course> courses = new LinkedHashMap<>();
    public Map<String, Section> sections = new LinkedHashMap<>();
    public Map<String, Team> teams = new LinkedHashMap<>();
    public Map<String, DeadlineExtension> deadlineExtensions = new LinkedHashMap<>();
    public Map<String, Instructor> instructors = new LinkedHashMap<>();
    public Map<String, Student> students = new LinkedHashMap<>();
    public Map<String, FeedbackSession> feedbackSessions = new LinkedHashMap<>();
    public Map<String, FeedbackQuestion> feedbackQuestions = new LinkedHashMap<>();
    public Map<String, FeedbackResponse> feedbackResponses = new LinkedHashMap<>();
    public Map<String, FeedbackResponseComment> feedbackResponseComments = new LinkedHashMap<>();
    public Map<String, FeedbackSessionLog> feedbackSessionLogs = new LinkedHashMap<>();
    public Map<String, Notification> notifications = new LinkedHashMap<>();
    public Map<String, ReadNotification> readNotifications = new LinkedHashMap<>();
}

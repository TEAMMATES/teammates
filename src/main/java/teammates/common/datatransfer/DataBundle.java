package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;

import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;

/**
 * Holds a bundle of entities.
 *
 * <p>This class is mainly used for serializing JSON strings.
 */
// CHECKSTYLE.OFF:JavadocVariable each field represents different entity types
public class DataBundle {
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

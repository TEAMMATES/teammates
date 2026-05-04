package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;

import teammates.logic.entity.Account;
import teammates.logic.entity.AccountRequest;
import teammates.logic.entity.Course;
import teammates.logic.entity.DeadlineExtension;
import teammates.logic.entity.FeedbackQuestion;
import teammates.logic.entity.FeedbackResponse;
import teammates.logic.entity.FeedbackResponseComment;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.FeedbackSessionLog;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Notification;
import teammates.logic.entity.ReadNotification;
import teammates.logic.entity.Section;
import teammates.logic.entity.Student;
import teammates.logic.entity.Team;

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

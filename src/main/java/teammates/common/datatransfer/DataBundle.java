package teammates.common.datatransfer;

import java.util.LinkedHashMap;
import java.util.Map;

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
}

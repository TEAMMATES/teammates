package teammates.common.datatransfer;

import java.util.HashMap;

/**
 * Holds a bundle of *Attributes data transfer objects.
 * This class is mainly used for serializing JSON strings.
 */
public class DataBundle {
    public HashMap<String, AccountAttributes> accounts = new HashMap<String, AccountAttributes>();
    public HashMap<String, InstructorAttributes> instructors = new HashMap<String, InstructorAttributes>();
    public HashMap<String, CourseAttributes> courses = new HashMap<String, CourseAttributes>();
    public HashMap<String, StudentAttributes> students = new HashMap<String, StudentAttributes>();
    public HashMap<String, FeedbackSessionAttributes> feedbackSessions = new HashMap<String, FeedbackSessionAttributes>();
    public HashMap<String, FeedbackQuestionAttributes> feedbackQuestions = new HashMap<String, FeedbackQuestionAttributes>();
    public HashMap<String, FeedbackResponseAttributes> feedbackResponses = new HashMap<String, FeedbackResponseAttributes>();
    public HashMap<String, FeedbackResponseCommentAttributes> feedbackResponseComments = new HashMap<String, FeedbackResponseCommentAttributes>();
    public HashMap<String, CommentAttributes> comments = new HashMap<String, CommentAttributes>();
    public HashMap<String, StudentProfileAttributes> profiles = new HashMap<String, StudentProfileAttributes>();
}

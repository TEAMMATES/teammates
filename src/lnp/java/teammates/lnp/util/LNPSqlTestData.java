package teammates.lnp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * L&P test data generator.
 */
public abstract class LNPSqlTestData {

    // CHECKSTYLE.OFF:MissingJavadocMethod generator for different entities are self-explained by the method name

    protected Map<String, Account> generateAccounts() {
        return new HashMap<>();
    }

    protected Map<String, Course> generateCourses() {
        return new HashMap<>();
    }

    protected Map<String, Instructor> generateInstructors() {
        return new HashMap<>();
    }

    protected Map<String, Student> generateStudents() {
        return new HashMap<>();
    }

    protected Map<String, FeedbackSession> generateFeedbackSessions() {
        return new HashMap<>();
    }

    protected Map<String, FeedbackQuestion> generateFeedbackQuestions() {
        return new HashMap<>();
    }

    protected Map<String, FeedbackResponse> generateFeedbackResponses() {
        return new HashMap<>();
    }

    protected Map<String, FeedbackResponseComment> generateFeedbackResponseComments() {
        return new HashMap<>();
    }

    // CHECKSTYLE.ON:MissingJavadocMethod

    /**
     * Returns a JSON data bundle containing the data relevant for the performance test.
     */
    public SqlDataBundle generateJsonData() {
        SqlDataBundle dataBundle = new SqlDataBundle();

        dataBundle.accounts = generateAccounts();
        dataBundle.courses = generateCourses();
        dataBundle.instructors = generateInstructors();
        dataBundle.students = generateStudents();
        dataBundle.feedbackSessions = generateFeedbackSessions();
        dataBundle.feedbackQuestions = generateFeedbackQuestions();
        dataBundle.feedbackResponses = generateFeedbackResponses();
        dataBundle.feedbackResponseComments = generateFeedbackResponseComments();

        return dataBundle;
    }

    /**
     * Returns list of header fields for the data in the CSV file to be generated.
     *
     * <p>Note that these header names should correspond to the variables used in the JMeter L&P test.</p>
     */
    public abstract List<String> generateCsvHeaders();

    /**
     * Returns the data for the entries in the CSV file to be generated.
     * The order of the field values for each entry should correspond to the order of headers specified
     * in {@link #generateCsvHeaders()}.
     *
     * @return List of entries, which are made up of a list of field values.
     */
    public abstract List<List<String>> generateCsvData();

}

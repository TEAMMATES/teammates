package teammates.lnp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * L&P test data generator.
 */
public abstract class LNPTestData {

    // CHECKSTYLE.OFF:MissingJavadocMethod generator for different entities are self-explained by the method name

    protected Map<String, AccountAttributes> generateAccounts() {
        return new HashMap<>();
    }

    protected Map<String, CourseAttributes> generateCourses() {
        return new HashMap<>();
    }

    protected Map<String, InstructorAttributes> generateInstructors() {
        return new HashMap<>();
    }

    protected Map<String, StudentAttributes> generateStudents() {
        return new HashMap<>();
    }

    protected Map<String, FeedbackSessionAttributes> generateFeedbackSessions() {
        return new HashMap<>();
    }

    protected Map<String, FeedbackQuestionAttributes> generateFeedbackQuestions() {
        return new HashMap<>();
    }

    protected Map<String, FeedbackResponseAttributes> generateFeedbackResponses() {
        return new HashMap<>();
    }

    protected Map<String, FeedbackResponseCommentAttributes> generateFeedbackResponseComments() {
        return new HashMap<>();
    }

    // CHECKSTYLE.ON:MissingJavadocMethod

    /**
     * Returns a JSON data bundle containing the data relevant for the performance test.
     */
    public DataBundle generateJsonData() {
        DataBundle dataBundle = new DataBundle();

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

package teammates.e2e.util;

import java.io.IOException;
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
import teammates.common.datatransfer.attributes.StudentProfileAttributes;

/**
 * L&P test data generator.
 */
public abstract class LNPTestData {

    protected abstract Map<String, AccountAttributes> generateAccounts();

    protected abstract Map<String, CourseAttributes> generateCourses();

    protected abstract Map<String, InstructorAttributes> generateInstructors();

    protected abstract Map<String, StudentAttributes> generateStudents();

    protected abstract Map<String, FeedbackSessionAttributes> generateFeedbackSessions();

    protected abstract Map<String, FeedbackQuestionAttributes> generateFeedbackQuestions();

    protected abstract Map<String, FeedbackResponseAttributes> generateFeedbackResponses();

    protected abstract Map<String, FeedbackResponseCommentAttributes> generateFeedbackResponseComments();

    protected abstract Map<String, StudentProfileAttributes> generateProfiles();

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
        dataBundle.profiles = generateProfiles();

        return dataBundle;
    }

    /**
     * Returns list of header fields for the data in the CSV file to be generated.
     */
    public abstract List<String> generateCsvHeaders();

    /**
     * Returns the data for the entries in the CSV file to be generated.
     * The order of the field values for each entry should correspond to the order of headers specified
     * in {@link #generateCsvHeaders()}.
     *
     * @return List of entries, which are made up of a list of field values.
     */
    public abstract List<List<String>> generateCsvData() throws IOException;

}

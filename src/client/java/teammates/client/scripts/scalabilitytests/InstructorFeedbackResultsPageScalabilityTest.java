package teammates.client.scripts.scalabilitytests;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.testng.annotations.Test;

import teammates.client.scripts.util.Stopwatch;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.test.cases.browsertests.BaseUiTestCase;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_RESULTS_PAGE}.
 *
 * <p>Run InstructorFeedbackResultsPageScaleTestDataGenerator.java before running the tests.
 */
public class InstructorFeedbackResultsPageScalabilityTest extends BaseUiTestCase {

    private static final String DATA_FOLDER_PATH = "src/client/java/teammates/client/scripts/scalabilitytests/data/";
    private static final Logger log = Logger.getLogger();
    private static DataBundle testDataMax;

    @Override
    protected void prepareTestData() {
        // the actual test data is refreshed before each test method
    }

    private void refreshTestData(int numStudents, int numQuestions) {

        // modify set of students for each test according to its requirements
        if (testData.students.size() == 0) { // if current collection of students is empty
            increaseNumOfStudents(numStudents);
        } else if (testData.students.size() < numStudents) { // current number of students is below required
            increaseNumOfStudents(numStudents - testData.students.size());
        } else if (testData.students.size() > numStudents) { // current number of students is above required
            decreaseNumOfStudents(numStudents);
        }

        // modify set of questions for each test according to its requirements
        if (testData.feedbackQuestions.size() == 0) { // if current collection of questions is empty
            increaseNumOfQuestions(numQuestions);
        } else if (testData.feedbackQuestions.size() < numQuestions) { // current number of questions is below required
            increaseNumOfQuestions(numQuestions - testData.feedbackQuestions.size());
        } else if (testData.feedbackQuestions.size() > numQuestions) { // current number of questions is above required
            decreaseNumOfQuestions(numQuestions);
        }

        // modify set of responses for each test according to its sets of students and questions
        // obtain set of e-mails for current set of students
        Set<String> studentsEmails = new HashSet<>(testData.students.size());
        for (StudentAttributes student : testData.students.values()) {
            studentsEmails.add(student.email);
        }

        // obtain set of keys for current Map of questions
        Set<Integer> questionsNumbers = new HashSet<>(testData.feedbackQuestions.size());
        for (FeedbackQuestionAttributes question : testData.feedbackQuestions.values()) {
            questionsNumbers.add(question.questionNumber);
        }

        // collect feedbackResponses for selected students and questions
        Set<String> feedbackResponsesKeys = testDataMax.feedbackResponses.keySet();
        String giver;
        String recipient;
        String questionId;
        for (String key : feedbackResponsesKeys) {
            giver = testDataMax.feedbackResponses.get(key).giver;
            recipient = testDataMax.feedbackResponses.get(key).recipient;
            questionId = testDataMax.feedbackResponses.get(key).feedbackQuestionId;

            if (!studentsEmails.contains(giver)
                    || !studentsEmails.contains(recipient)
                    || !questionsNumbers.contains(questionId)) {
                if (testData.feedbackResponses.containsKey(key)) {
                    testData.feedbackResponses.remove(key);
                }
                continue;
            }
            if (testData.feedbackResponses.containsKey(key)) {
                continue;
            }
            testData.feedbackResponses.put(key, testDataMax.feedbackResponses.get(key));
        }

        removeAndRestoreDataBundle(testData);
    }

    private void increaseNumOfStudents(int numStudentsToAdd) {
        int countLimit = 0;
        for (String key : testDataMax.students.keySet()) {
            if (countLimit >= numStudentsToAdd) {
                return;
            }
            if (testData.students.containsKey(key)) {
                continue;
            }
            testData.students.put(key, testDataMax.students.get(key));
            countLimit++;
        }
        countLimit = 0;
    }

    private void decreaseNumOfStudents(int remainingNumOfStudents) {
        Set<String> studentsNames = testData.students.keySet();
        for (String key : studentsNames) {
            if (testData.students.size() == remainingNumOfStudents) {
                return;
            }
            testData.students.remove(key);
        }
    }

    private void increaseNumOfQuestions(int numQuestionsToAdd) {
        int countLimit = 0;
        for (String key : testDataMax.feedbackQuestions.keySet()) {
            if (countLimit >= numQuestionsToAdd) {
                return;
            }
            if (testData.feedbackQuestions.containsKey(key)) {
                continue;
            }
            testData.feedbackQuestions.put(key, testDataMax.feedbackQuestions.get(key));
            countLimit++;
        }
        countLimit = 0;
    }

    private void decreaseNumOfQuestions(int remainingNumOfQuestions) {
        Set<String> questionsKeys = testData.feedbackQuestions.keySet();
        for (String key : questionsKeys) {
            if (testData.feedbackQuestions.size() == remainingNumOfQuestions) {
                return;
            }
            testData.feedbackQuestions.remove(key);
        }
    }

    private void loadTestData(int numStudentsMax, int numQuestionsMax) {
        // load maximum test data
        testDataMax = loadDataBundle(
                DATA_FOLDER_PATH + "InstructorFeedbackResultsPageScaleTest-" + numStudentsMax
                + "Students" + numQuestionsMax + "Questions.json");

        // prepare non-modified test data
        testData = new DataBundle();
        testData.accounts = testDataMax.accounts;
        testData.courses = testDataMax.courses;
        testData.instructors = testDataMax.instructors;
        testData.feedbackSessions = testDataMax.feedbackSessions;
        testData.feedbackResponseComments = testDataMax.feedbackResponseComments;
        testData.comments = testDataMax.comments;
        testData.profiles = testDataMax.profiles;
        testData.adminEmails = testDataMax.adminEmails;
    }

    @Test
    public void testContentWithIncreasingLoad() throws Exception {
        //Number of students and questions for each case.
        int[] studentLoads = {10, 20};
        int[] questionLoads = {1, 5, 10};

        // Maximum number of students and questions for all cases.
        int studentNumsMax = 20;
        int questionNumsMax = 10;

        // load single test data for all tests with maximum number of students and questions
        loadTestData(studentNumsMax, questionNumsMax);

        for (int studentLoad : studentLoads) {
            for (int questionLoad : questionLoads) {
                log.info("Testing with " + studentLoad + " students, " + questionLoad + " questions...");
                refreshTestData(studentLoad, questionLoad);
                loginToInstructorFeedbackResultsPage("CFResultsScT.instr", "Open Session");
            }
        }
    }

    @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn") // Needs to log before returning.
    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPage(String instructorName, String fsName) {
        AppUrl resultsUrl =
                createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                        .withUserId(testData.instructors.get(instructorName).googleId)
                        .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                        .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();

        InstructorFeedbackResultsPage resultsPage =
                loginAdminToPage(resultsUrl, InstructorFeedbackResultsPage.class);
        resultsPage.clickCollapseExpandButton();
        log.info("Time taken: " + stopwatch.getTimeElapsedInSeconds());

        return resultsPage;
    }
}

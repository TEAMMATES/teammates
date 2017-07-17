package teammates.client.scripts.scalabilitytests;

import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.Test;

import teammates.client.scripts.util.Stopwatch;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
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

    @Override
    protected void prepareTestData() {
        // the actual test data is refreshed before each test method
    }

    private void refreshTestData(int numStudents, int numQuestions) {
        refreshTestData(
                "InstructorFeedbackResultsPageScaleTest-" + numStudents + "Students" + numQuestions + "Questions.json");
    }

    private void refreshTestData(String filename) {
        testData = loadDataBundle(DATA_FOLDER_PATH + filename);
        verifyOrPersistTestDataToDatastore();
    }

    // verify if entities for testing already exist in datastore
    private void verifyOrPersistTestDataToDatastore() {
        log.info("Verifying test data existance in datastore...");

        for (CourseAttributes course : testData.courses.values()) {
            if (getCourse(course) == null) {
                doPutCourse(course);
                log.info("Course was added to datastore: " + course);
            }
        }

        for (AccountAttributes account : testData.accounts.values()) {
            if (getAccount(account) == null) {
                doPutAccount(account);
                log.info("Account was added to datastore: " + account);
            }
        }

        for (FeedbackSessionAttributes feedbackSession : testData.feedbackSessions.values()) {
            if (getFeedbackSession(feedbackSession) == null) {
                doPutFeedbackSession(feedbackSession);
                log.info("Feedback Session was added to datastore: " + feedbackSession);
            }
        }

        for (InstructorAttributes instructor : testData.instructors.values()) {
            if (getInstructor(instructor) == null) {
                doPutInstructor(instructor);
                log.info("Instructor was added to datastore: " + instructor);
            }
        }

        int writesCounter = 0;
        for (StudentAttributes student : testData.students.values()) {
            if (getStudent(student) == null) {
                doPutStudent(student);
                log.info("Student was added to datastore (" + writesCounter + "): " + student);
                writesCounter++;
            }
        }

        Map<Integer, String> feedbackQuestionsRealIds = new TreeMap<>();
        writesCounter = 0;
        for (FeedbackQuestionAttributes question : testData.feedbackQuestions.values()) {
            FeedbackQuestionAttributes questionInDatastore = getFeedbackQuestion(question);
            if (questionInDatastore == null) {
                doPutFeedbackQuestion(question);
                log.info("Feedback Question was added to datastore (" + writesCounter + "): " + question);
                writesCounter++;
            }
            feedbackQuestionsRealIds.put(questionInDatastore.questionNumber, questionInDatastore.getId());
        }

        writesCounter = 0;
        for (FeedbackResponseAttributes feedbackResponse : testData.feedbackResponses.values()) {
            feedbackResponse.feedbackQuestionId = feedbackQuestionsRealIds.get(
                    Integer.valueOf(feedbackResponse.feedbackQuestionId));
            if (getFeedbackResponse(feedbackResponse) == null) {
                doPutFeedbackResponse(feedbackResponse);
                log.info("Feedback Response was added to datastore (" + writesCounter + "): " + feedbackResponse);
                writesCounter++;
            }
        }
    }

    @Test
    public void testContentWithIncreasingLoad() throws Exception {
        //Number of students and questions for each case.
        int[] studentLoads = {10, 20};
        int[] questionLoads = {1, 5, 10};

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

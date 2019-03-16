package teammates.e2e.cases.scalability;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.e2e.cases.e2e.BaseE2ETestCase;
import teammates.e2e.util.Stopwatch;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_RESULTS_PAGE}.
 *
 * <p>Run {@link InstructorFeedbackResultsPageScaleTestDataGenerator} before running the tests.
 */
public class InstructorFeedbackResultsPageScalabilityTest extends BaseE2ETestCase {

    private static final String DATA_FOLDER_PATH = "src/e2e/java/teammates/e2e/cases/scalability/data/";
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
        removeAndRestoreDataBundle(testData);
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

    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPage(String instructorName, String fsName) {
        AppUrl resultsUrl =
                createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                        .withUserId(testData.instructors.get(instructorName).googleId)
                        .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                        .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();

        InstructorFeedbackResultsPage resultsPage =
                loginAdminToPageOld(resultsUrl, InstructorFeedbackResultsPage.class);
        resultsPage.clickCollapseExpandButtonAndWaitForPanelsToExpand();
        log.info("Time taken: " + stopwatch.getTimeElapsedInSeconds());

        return resultsPage;
    }
}
